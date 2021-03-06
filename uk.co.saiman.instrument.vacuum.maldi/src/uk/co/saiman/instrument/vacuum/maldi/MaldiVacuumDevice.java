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
 * This file is part of uk.co.saiman.instrument.vacuum.maldi.
 *
 * uk.co.saiman.instrument.vacuum.maldi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.vacuum.maldi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.vacuum.maldi;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static uk.co.saiman.instrument.ConnectionState.CONNECTED;
import static uk.co.saiman.instrument.ConnectionState.DISCONNECTED;
import static uk.co.saiman.log.Log.Level.ERROR;
import static uk.co.saiman.measurement.Quantities.quantityFormat;
import static uk.co.saiman.measurement.Units.pascal;
import static uk.co.saiman.measurement.Units.second;
import static uk.co.saiman.observable.ObservableProperty.over;
import static uk.co.saiman.observable.Observer.onFailure;
import static uk.co.saiman.observable.Observer.onObservation;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Time;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.instrument.ConnectionState;
import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.DeviceImpl;
import uk.co.saiman.instrument.DeviceRegistration;
import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.instrument.InstrumentRegistration;
import uk.co.saiman.instrument.vacuum.VacuumControl;
import uk.co.saiman.instrument.vacuum.VacuumDevice;
import uk.co.saiman.instrument.vacuum.VacuumSample;
import uk.co.saiman.instrument.vacuum.maldi.MaldiVacuumDevice.MaldiVacuumConfiguration;
import uk.co.saiman.log.Log;
import uk.co.saiman.messaging.MessageReceiver;
import uk.co.saiman.messaging.MessageSender;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservableValue;

@Designate(ocd = MaldiVacuumConfiguration.class, factory = true)
@Component(configurationPid = MaldiVacuumDevice.CONFIGURATION_PID, configurationPolicy = REQUIRE, service = {
    Device.class,
    VacuumDevice.class })
public class MaldiVacuumDevice extends DeviceImpl<VacuumControl>
    implements VacuumDevice<VacuumControl> {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "Maldi Vacuum Device", description = "A vaccum device implementation for MALDI")
  public @interface MaldiVacuumConfiguration {
    @AttributeDefinition(name = "Sample Resolution", description = "The minimum resolvable units of time for samples")
    String sampleResolution() default DEFAULT_SAMPLE_RESOLUTION_SECONDS + "s";
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.instrument.vacuum.maldi";

  /**
   * The default acquisition resolution when none is provided.
   */
  public static final double DEFAULT_SAMPLE_RESOLUTION_SECONDS = 2;

  private final Quantity<Time> sampleResolution;
  private final Unit<Pressure> pressureUnit;
  private final Unit<Time> timeUnit;

  private final DeviceRegistration registration;
  private final Log log;

  private final MessageReceiver status;
  private final MessageSender command;
  private final ObservableProperty<ConnectionState> connectionState;

  private VacuumSample lastSample;
  private final HotObservable<VacuumSample> samples;

  @Activate
  public MaldiVacuumDevice(
      MaldiVacuumConfiguration configuration,
      @Reference Log log,
      @Reference Instrument instrument,
      @Reference(name = "status") MessageReceiver status,
      @Reference(name = "command") MessageSender command) {
    this(
        quantityFormat().parse(configuration.sampleResolution()).asType(Time.class),
        log,
        instrument,
        status,
        command);
  }

  public MaldiVacuumDevice(
      Quantity<Time> sampleResolution,
      Log log,
      Instrument instrument,
      MessageReceiver status,
      MessageSender command) {
    super("MALDI Vacuum Device");

    this.sampleResolution = sampleResolution;
    this.pressureUnit = pascal().getUnit();
    this.timeUnit = second().getUnit();

    this.log = log;

    this.samples = new HotObservable<>();

    this.status = status;
    this.command = command;
    this.connectionState = over(DISCONNECTED);

    this.registration = instrument.registerDevice(this);

    receiveStatus();

    sendCommand("Get gauge magnetron 1 STATE");
  }

  private void receiveStatus() {
    this.status
        .receiveMessages()
        .then(onObservation(o -> connectionState.set(CONNECTED)))
        .then(onFailure(e -> log.log(ERROR, e)))
        .observe(status -> {
          connectionState.set(CONNECTED);
          String statusText = UTF_8.decode(status).toString();
          // System.out.println(" ?????????????????????????????????");
          // System.out.println(statusText);
        });
  }

  private void sendCommand(String commandText) {
    try {
      var command = UTF_8.encode(commandText);
      this.command.sendData(command);
    } catch (Exception e) {
      connectionState.set(DISCONNECTED);
      log.log(ERROR, e);
    }
  }

  @Override
  public VacuumControl acquireControl(long timeout, TimeUnit unit) {
    return new MaldiVacuumControl(this, timeout, unit);
  }

  @Override
  public InstrumentRegistration getInstrumentRegistration() {
    return registration.getInstrumentRegistration();
  }

  @Override
  public ObservableValue<ConnectionState> connectionState() {
    return connectionState;
  }

  @Override
  public Unit<Pressure> getPressureMeasurementUnit() {
    return pressureUnit;
  }

  @Override
  public Unit<Time> getSampleTimeUnit() {
    return timeUnit;
  }

  @Override
  public Optional<VacuumSample> getLastSample() {
    return Optional.ofNullable(lastSample);
  }

  @Override
  public Observable<VacuumSample> sampleEvents() {
    return samples;
  }

  @Override
  public Quantity<Time> getSampleResolution() {
    return sampleResolution;
  }

  @Override
  public Quantity<Frequency> getSampleFrequency() {
    return getSampleResolution().inverse().asType(Frequency.class);
  }
}
