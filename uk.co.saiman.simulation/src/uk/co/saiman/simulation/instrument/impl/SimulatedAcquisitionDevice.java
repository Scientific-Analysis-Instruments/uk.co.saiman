/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.simulation.
 *
 * uk.co.saiman.simulation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.simulation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.simulation.instrument.impl;

import static java.lang.Thread.MAX_PRIORITY;
import static java.lang.Thread.currentThread;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static uk.co.saiman.instrument.DeviceConnection.CONNECTED;
import static uk.co.saiman.log.Log.Level.ERROR;

import java.util.Optional;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Time;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.acquisition.AcquisitionException;
import uk.co.saiman.data.function.SampledContinuousFunction;
import uk.co.saiman.data.function.SampledDomain;
import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.DeviceConnection;
import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.log.Log;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.observable.Disposable;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.observable.ObservableValue;
import uk.co.saiman.simulation.SimulationProperties;
import uk.co.saiman.simulation.instrument.DetectorSimulation;
import uk.co.saiman.simulation.instrument.impl.SimulatedAcquisitionDevice.AcquisitionSimulationConfiguration;
import uk.co.saiman.text.properties.PropertyLoader;

/**
 * Implementation of a simulation of an acquisition device.
 * 
 * @author Elias N Vasylenko
 */
@Designate(ocd = AcquisitionSimulationConfiguration.class, factory = true)
@Component(
    configurationPid = SimulatedAcquisitionDevice.CONFIGURATION_PID,
    configurationPolicy = REQUIRE)
public class SimulatedAcquisitionDevice implements AcquisitionDevice, Device {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "Simulated Acquisition Device Configuration",
      description = "The simulated acquisition device provides an implementation which defers to a detector simulation")
  public @interface AcquisitionSimulationConfiguration {
    @AttributeDefinition(
        name = "Acquisition Resolution",
        description = "The minimum resolvable units of time for samples")
    String acquisitionResolution() default DEFAULT_ACQUISITION_RESOLUTION_SECONDS + "s";
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.simulation.instrument.acquisition";

  private class ExperimentConfiguration {
    private final SampledDomain<Time> domain;

    private int counter;

    public ExperimentConfiguration() {
      domain = getSampleDomain();

      counter = getAcquisitionCount();
      if (counter <= 0) {
        throw new AcquisitionException(text.acquisition().exceptions().countMustBePositive());
      }
    }
  }

  private class Acquisition {
    private final int remaining;
    private final SampledContinuousFunction<Time, Dimensionless> data;

    public Acquisition(int remaining, SampledContinuousFunction<Time, Dimensionless> data) {
      this.remaining = remaining;
      this.data = data;
    }

    /**
     * @return the number of spectra remaining after this one in the current
     *         experiment, or -1 if the spectra does not belong to an experiment
     */
    public int getRemaining() {
      return remaining;
    }

    public SampledContinuousFunction<Time, Dimensionless> getData() {
      return data;
    }
  }

  /**
   * The default acquisition resolution when none is provided.
   */
  public static final double DEFAULT_ACQUISITION_RESOLUTION_SECONDS = 0.00_000_025;
  /**
   * The default acquisition time when none is provided.
   */
  public static final double DEFAULT_ACQUISITION_TIME_SECONDS = 0.01;
  /**
   * The default acquisition count when none is provided.
   */
  public static final int DEFAULT_ACQUISITION_COUNT = 1000;

  @Reference
  private Log log;
  @Reference
  private Units units;
  private Unit<Dimensionless> intensityUnits;
  private Unit<Time> timeUnits;
  @Reference
  private PropertyLoader loader;
  private SimulationProperties text;
  @Reference
  private Instrument instrument;
  private boolean disposed;
  private Disposable instrumentSubscription;

  /*
   * Instrument Configuration
   */
  private int acquisitionDepth;
  private int acquisitionCount;
  private Quantity<Time> resolution;

  @Reference
  private DetectorSimulation detector;

  /*
   * External Acquisition State
   */
  private final HotObservable<SampledContinuousFunction<Time, Dimensionless>> dataListeners;
  private final HotObservable<SampledContinuousFunction<Time, Dimensionless>> acquisitionListeners;

  private boolean acquiring;
  private SampledContinuousFunction<Time, Dimensionless> acquisitionData;

  /*
   * Internal Acquisition State
   */
  private final HotObservable<Acquisition> acquisitionBuffer;
  private final Object startingLock = new Object();
  private final Object acquiringLock = new Object();
  private Optional<ExperimentConfiguration> experiment;

  public SimulatedAcquisitionDevice() {
    acquisitionBuffer = new HotObservable<>();
    dataListeners = new HotObservable<>();
    acquisitionListeners = new HotObservable<>();
    acquisitionListeners.complete();
    acquiring = false;
    experiment = Optional.empty();
  }

  @Activate
  synchronized void activate(AcquisitionSimulationConfiguration configuration) {
    text = loader.getProperties(SimulationProperties.class);

    intensityUnits = units.count().get();
    timeUnits = units.second().get();

    resolution = units.parseQuantity(configuration.acquisitionResolution()).asType(Time.class);
    setAcquisitionTime(units.second().getQuantity(DEFAULT_ACQUISITION_TIME_SECONDS));
    setAcquisitionCount(DEFAULT_ACQUISITION_COUNT);

    acquisitionBuffer.observe(this::acquired);
    new Thread(this::acquire).start();

    instrumentSubscription = instrument.addDevice(this);
  }

  @Deactivate
  public void dispose() {
    disposed = true;
    instrumentSubscription.cancel();
  }

  @Override
  public Instrument getInstrument() {
    return instrument;
  }

  @Override
  public String getName() {
    return text.acquisitionSimulationDeviceName().toString();
  }

  /*
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * TODO
   * 
   * 
   * TODO
   * 
   * 
   * 
   * TODO have a buffer pool of SoftReferenced arrays we can re-use when the
   * previous WeakReferenced ContinuousFunction which used the array is garbage
   * collected. This way we mostly mitigate unnecessary memory allocation while
   * keeping a nice API and immutability.
   * 
   * 
   * 
   * 
   */

  @Override
  public void startAcquisition() {
    synchronized (acquiringLock) {
      try {
        synchronized (startingLock) {
          this.experiment.ifPresent(e -> {
            throw new AcquisitionException(text.acquisition().exceptions().alreadyAcquiring());
          });

          // wait any previous experiment to flush from the buffer
          while (acquiring) {
            acquiringLock.wait();
          }

          // prepare a new experiment
          acquisitionListeners.start();
          // observers?
          this.experiment = Optional.of(new ExperimentConfiguration());

          // wait for the new experiment to reach the end of the buffer
          while (!acquiring) {
            acquiringLock.wait();
          }
        }
      } catch (InterruptedException e) {
        this.experiment = Optional.empty();
        throw new AcquisitionException(text.acquisition().exceptions().experimentInterrupted(), e);
      }
    }
  }

  private void acquired(Acquisition acquisition) {
    synchronized (acquiringLock) {
      acquiring = acquisition.getRemaining() >= 0;
      dataListeners.next(acquisition.getData());
      if (acquiring) {
        acquisitionListeners.next(acquisition.getData());
      }

      if (acquisition.getRemaining() == 0) {
        acquiring = false;
        acquisitionListeners.complete();
      }

      acquiringLock.notifyAll();
    }
  }

  private void acquire() {
    currentThread().setPriority(MAX_PRIORITY);

    while (!disposed) {
      SampledDomain<Time> domain;
      int counter;

      try {
        /*
         * These may remain blocking after an attempt to start an experiment, but this
         * is okay as the experiment should have failed anyway if this is blocked:
         */
        domain = experiment.map(e -> e.domain).orElse(getSampleDomain());
        counter = experiment.map(e -> --e.counter).orElse(-1);

        acquisitionData = detector.acquire(domain, getSampleIntensityUnits());
        acquisitionBuffer.next(new Acquisition(counter, acquisitionData));

        synchronized (acquiringLock) {
          if (counter == 0) {
            this.experiment = Optional.empty();
            acquiringLock.notifyAll();
          }
        }
      } catch (AcquisitionException e) {
        exception(e);
      } catch (Exception e) {
        exception(
            new AcquisitionException(text.acquisition().exceptions().unexpectedException(), e));
      }

      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    stopAcquisition();
  }

  private void exception(AcquisitionException exception) {
    dataListeners.fail(exception);
    acquisitionListeners.fail(exception);
    log.log(ERROR, exception);
  }

  @Override
  public void stopAcquisition() {
    synchronized (acquiringLock) {
      if (experiment.isPresent()) {
        experiment = Optional.empty();
        acquiringLock.notifyAll();
      }
    }
  }

  @Override
  public boolean isAcquiring() {
    return acquiring;
  }

  @Override
  public SampledContinuousFunction<Time, Dimensionless> getLastAcquisitionData() {
    return acquisitionData;
  }

  @Override
  public Observable<SampledContinuousFunction<Time, Dimensionless>> dataEvents() {
    return dataListeners;
  }

  @Override
  public Observable<SampledContinuousFunction<Time, Dimensionless>> acquisitionDataEvents() {
    return acquisitionListeners;
  }

  @Override
  public Quantity<Time> getSampleResolution() {
    return resolution;
  }

  @Override
  public Quantity<Frequency> getSampleFrequency() {
    return getSampleResolution().inverse().asType(Frequency.class);
  }

  @Override
  public void setAcquisitionTime(Quantity<Time> time) {
    synchronized (startingLock) {
      acquisitionDepth = time.divide(getSampleResolution()).getValue().intValue();
    }
  }

  @Override
  public Quantity<Time> getAcquisitionTime() {
    return getSampleResolution().multiply(getSampleDepth());
  }

  @Override
  public void setSampleDepth(int depth) {
    synchronized (startingLock) {
      acquisitionDepth = depth;
    }
  }

  @Override
  public int getSampleDepth() {
    return acquisitionDepth;
  }

  @Override
  public void setAcquisitionCount(int count) {
    if (count <= 0) {
      throw new AcquisitionException(text.invalidAcquisitionCount(count));
    }
    acquisitionCount = count;
  }

  @Override
  public int getAcquisitionCount() {
    return acquisitionCount;
  }

  @Override
  public Unit<Time> getSampleTimeUnits() {
    return timeUnits;
  }

  @Override
  public Unit<Dimensionless> getSampleIntensityUnits() {
    return intensityUnits;
  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public ObservableValue<DeviceConnection> connectionState() {
    return Observable.value(CONNECTED);
  }
}
