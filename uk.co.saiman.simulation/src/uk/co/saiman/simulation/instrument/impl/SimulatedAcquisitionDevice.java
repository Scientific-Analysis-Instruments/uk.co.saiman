/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ReferenceCardinality.OPTIONAL;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;
import static uk.co.saiman.instrument.ConnectionState.CONNECTED;
import static uk.co.saiman.log.Log.Level.ERROR;
import static uk.co.saiman.measurement.Quantities.quantityFormat;
import static uk.co.saiman.measurement.Units.count;
import static uk.co.saiman.measurement.Units.second;
import static uk.co.saiman.observable.Observer.forObservation;
import static uk.co.saiman.observable.Observer.onObservation;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

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

import uk.co.saiman.data.function.SampledContinuousFunction;
import uk.co.saiman.instrument.ConnectionState;
import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.DeviceImpl;
import uk.co.saiman.instrument.DeviceRegistration;
import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.instrument.InstrumentRegistration;
import uk.co.saiman.instrument.acquisition.AcquisitionControl;
import uk.co.saiman.instrument.acquisition.AcquisitionDevice;
import uk.co.saiman.instrument.acquisition.AcquisitionException;
import uk.co.saiman.log.Log;
import uk.co.saiman.measurement.scalar.Scalar;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.observable.ObservableValue;
import uk.co.saiman.observable.Observation;
import uk.co.saiman.properties.PropertyLoader;
import uk.co.saiman.simulation.SimulationProperties;
import uk.co.saiman.simulation.instrument.DetectorSimulation;
import uk.co.saiman.simulation.instrument.DetectorSimulationService;
import uk.co.saiman.simulation.instrument.impl.SimulatedAcquisitionDevice.AcquisitionSimulationConfiguration;

/**
 * Implementation of a simulation of an acquisition device.
 * 
 * @author Elias N Vasylenko
 */
@Designate(ocd = AcquisitionSimulationConfiguration.class, factory = true)
@Component(configurationPid = SimulatedAcquisitionDevice.CONFIGURATION_PID, configurationPolicy = REQUIRE, service = {
    Device.class,
    AcquisitionDevice.class })
public class SimulatedAcquisitionDevice extends DeviceImpl<AcquisitionControl>
    implements AcquisitionDevice<AcquisitionControl> {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "Simulated Acquisition Device Configuration", description = "The simulated acquisition device provides an implementation which defers to a detector simulation")
  public @interface AcquisitionSimulationConfiguration {
    @AttributeDefinition(name = "Acquisition Resolution", description = "The minimum resolvable units of time for samples")
    String acquisitionResolution() default DEFAULT_ACQUISITION_RESOLUTION_SECONDS + "s";
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.simulation.instrument.acquisition";

  private class ExperimentConfiguration {
    private final DetectorSimulation detector;
    private int counter;

    public ExperimentConfiguration() {
      detector = getDetector();
      counter = getAcquisitionCount();

      if (detector == null) {
        throw new AcquisitionException(
            "Failed to connect acquisition simulation to detector simulation");
      }
      if (counter <= 0) {
        throw new AcquisitionException("Requested acquisition count must be positive");
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

  private final Unit<Dimensionless> intensityUnit;
  private final Unit<Time> timeUnit;

  private final SimulationProperties simulationProperties;
  private final DeviceRegistration instrumentRegistration;
  private final Log log;

  /*
   * Instrument Configuration
   */
  private final Quantity<Time> acquisitionResolution;
  private int acquisitionDepth;
  private int acquisitionCount;

  @Reference(name = "detector", cardinality = OPTIONAL, policy = DYNAMIC)
  private volatile DetectorSimulationService detectorService;
  private volatile DetectorSimulation detector;

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

  @Activate
  public SimulatedAcquisitionDevice(
      AcquisitionSimulationConfiguration configuration,
      @Reference PropertyLoader loader,
      @Reference Log log,
      @Reference(name = "instrument") Instrument instrument) {
    this(
        quantityFormat().parse(configuration.acquisitionResolution()).asType(Time.class),
        loader.getProperties(SimulationProperties.class),
        log,
        instrument);
  }

  public SimulatedAcquisitionDevice(
      Quantity<Time> acquisitionResolution,
      SimulationProperties simulationProperties,
      Log log,
      Instrument instrument) {
    super(simulationProperties.acquisitionSimulationDeviceName().toString());

    this.acquisitionResolution = acquisitionResolution;
    this.simulationProperties = simulationProperties;
    this.log = log;

    acquisitionBuffer = new HotObservable<>();
    dataListeners = new HotObservable<>();
    acquisitionListeners = new HotObservable<>();
    acquisitionListeners.complete();
    acquiring = false;
    experiment = Optional.empty();

    intensityUnit = count().getUnit();
    timeUnit = second().getUnit();

    setAcquisitionTime(new Scalar<>(second(), DEFAULT_ACQUISITION_TIME_SECONDS));
    setAcquisitionCount(DEFAULT_ACQUISITION_COUNT);

    acquisitionBuffer
        .aggregateBackpressure()
        .concatMap(Observable::of)
        .executeOn(newSingleThreadExecutor())
        .then(onObservation(Observation::requestNext))
        .then(forObservation(o -> m -> o.requestNext()))
        .observe(this::acquired);

    instrumentRegistration = instrument.registerDevice(this);

    initializeDetector();

    new Thread(this::acquire).start();
  }

  private void initializeDetector() {
    detector = detectorService == null
        ? null
        : detectorService.getDetectorSimulation(getSampleDomain(), getSampleIntensityUnit());
  }

  @Deactivate
  public void dispose() {
    instrumentRegistration.deregister();
  }

  @Override
  public InstrumentRegistration getInstrumentRegistration() {
    return instrumentRegistration.getInstrumentRegistration();
  }

  protected DetectorSimulation getDetector() {
    if (detector == null)
      initializeDetector();
    return detector;
  }

  void startAcquisition() {
    synchronized (acquiringLock) {
      try {
        synchronized (startingLock) {
          this.experiment.ifPresent(e -> {
            throw new AcquisitionException("Cannot begin acquisition; already acquiring");
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
          while (!acquiring && acquisitionListeners.isLive()) {
            acquiringLock.wait();
          }
        }
      } catch (InterruptedException e) {
        exception(new AcquisitionException("Acquisition interrupted", e));
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

    while (getInstrumentRegistration().isRegistered()) {
      DetectorSimulation detector;
      int counter;

      try {
        detector = experiment.map(e -> e.detector).orElse(getDetector());
        counter = experiment.map(e -> --e.counter).orElse(-1);

        acquisitionData = detector.acquire();
        acquisitionBuffer.next(new Acquisition(counter, acquisitionData));

        synchronized (acquiringLock) {
          if (counter == 0) {
            this.experiment = Optional.empty();
            acquiringLock.notifyAll();
          }
        }

        Thread.sleep(1);

        if (!dataListeners.isLive())
          dataListeners.start();
      } catch (AcquisitionException e) {
        exception(e);
      } catch (Exception e) {
        exception(new AcquisitionException("Unexpected acquisition failure", e));
      }
    }

    stopAcquisition();
  }

  private void exception(AcquisitionException exception) {
    synchronized (acquiringLock) {
      this.experiment = Optional.empty();
      if (acquisitionListeners.isLive()) {
        acquisitionListeners.fail(exception);
        acquiringLock.notifyAll();
      }
    }
    if (dataListeners.isLive()) {
      dataListeners.fail(exception);
      log.log(ERROR, exception);
    }
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {}
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
    return acquisitionResolution;
  }

  @Override
  public Quantity<Frequency> getSampleFrequency() {
    return getSampleResolution().inverse().asType(Frequency.class);
  }

  void setAcquisitionTime(Quantity<Time> time) {
    synchronized (startingLock) {
      acquisitionDepth = time.divide(getSampleResolution()).getValue().intValue();
      initializeDetector();
    }
  }

  @Override
  public Quantity<Time> getAcquisitionTime() {
    return getSampleResolution().multiply(getSampleDepth());
  }

  void setSampleDepth(int depth) {
    synchronized (startingLock) {
      acquisitionDepth = depth;
      initializeDetector();
    }
  }

  @Override
  public int getSampleDepth() {
    return acquisitionDepth;
  }

  void setAcquisitionCount(int count) {
    if (count <= 0) {
      throw new AcquisitionException(simulationProperties.invalidAcquisitionCount(count));
    }
    acquisitionCount = count;
  }

  @Override
  public int getAcquisitionCount() {
    return acquisitionCount;
  }

  @Override
  public Unit<Time> getSampleTimeUnit() {
    return timeUnit;
  }

  @Override
  public Unit<Dimensionless> getSampleIntensityUnit() {
    return intensityUnit;
  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public ObservableValue<ConnectionState> connectionState() {
    return ObservableValue.of(CONNECTED);
  }

  @Override
  public AcquisitionControl acquireControl(long timeout, TimeUnit unit) {
    return new SimulatedAcquisitionControl(this, timeout, unit);
  }
}
