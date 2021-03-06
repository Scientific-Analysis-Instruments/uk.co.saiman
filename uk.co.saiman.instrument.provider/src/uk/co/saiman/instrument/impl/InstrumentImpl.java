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
 * This file is part of uk.co.saiman.instrument.provider.
 *
 * uk.co.saiman.instrument.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.impl;

import static org.osgi.service.component.annotations.ConfigurationPolicy.OPTIONAL;
import static uk.co.saiman.instrument.InstrumentLifecycleState.BEGIN_OPERATION;
import static uk.co.saiman.instrument.InstrumentLifecycleState.END_OPERATION;
import static uk.co.saiman.instrument.InstrumentLifecycleState.OPERATING;
import static uk.co.saiman.instrument.InstrumentLifecycleState.STANDBY;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.DeviceRegistration;
import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.instrument.InstrumentLifecycleState;
import uk.co.saiman.instrument.InstrumentRegistration;
import uk.co.saiman.instrument.impl.InstrumentImpl.InstrumentConfiguration;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservablePropertyImpl;
import uk.co.saiman.observable.ObservableValue;

/**
 * Reference implementation of {@link Instrument}, as an OSGi service.
 * 
 * If more than one instrument exists within the framework, each instance is
 * responsible for making sure it contains the correct devices. This may be
 * achieved by e.g. filtering on device components or by subsystem isolation.
 * 
 * @author Elias N Vasylenko
 */
@Designate(ocd = InstrumentConfiguration.class, factory = true)
@Component(name = InstrumentImpl.CONFIGURATION_PID, configurationPid = InstrumentImpl.CONFIGURATION_PID, configurationPolicy = OPTIONAL)
public class InstrumentImpl implements Instrument {
  static final String CONFIGURATION_PID = "uk.co.saiman.instrument";

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(id = CONFIGURATION_PID, name = "SAINT Instrument Configuration", description = "The configuration for an instrument")
  public @interface InstrumentConfiguration {}

  private final Set<InstrumentRegistration> devices;
  private final ObservableProperty<InstrumentLifecycleState> state;

  /**
   * Create an empty instrument in standby.
   */
  public InstrumentImpl() {
    devices = new HashSet<>();
    state = new ObservablePropertyImpl<>(STANDBY);
  }

  @Override
  public Stream<? extends InstrumentRegistration> getRegistrations() {
    return devices.stream();
  }

  @Override
  public synchronized DeviceRegistration registerDevice(Device<?> device) {
    InstrumentRegistration deviceRegistration = new InstrumentRegistration() {
      @Override
      public boolean isRegistered() {
        return devices.contains(this);
      }

      @Override
      public Instrument getInstrument() {
        return InstrumentImpl.this;
      }

      @Override
      public Device<?> getDevice() {
        return device;
      }
    };

    DeviceRegistration instrumentRegistration = new DeviceRegistration() {
      @Override
      public void deregister() {
        devices.remove(deviceRegistration);
      }

      @Override
      public InstrumentRegistration getInstrumentRegistration() {
        return deviceRegistration;
      }
    };

    devices.add(deviceRegistration);

    return instrumentRegistration;
  }

  @Override
  public synchronized void requestOperation() {
    if (!state.isEqual(OPERATING))
      if (transitionToState(BEGIN_OPERATION))
        transitionToState(OPERATING);
      else
        transitionToState(STANDBY);
  }

  @Override
  public synchronized void requestStandby() {
    if (state.isEqual(OPERATING))
      if (transitionToState(END_OPERATION))
        transitionToState(STANDBY);
  }

  private synchronized boolean transitionToState(InstrumentLifecycleState state) {
    this.state.set(state);
    return this.state.isPresent();
  }

  @Override
  public ObservableValue<InstrumentLifecycleState> lifecycleState() {
    return state;
  }
}
