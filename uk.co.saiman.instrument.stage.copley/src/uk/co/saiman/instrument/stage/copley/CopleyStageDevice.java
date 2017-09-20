/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.instrument.stage.copley.
 *
 * uk.co.saiman.instrument.stage.copley is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.stage.copley is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.stage.copley;

import static uk.co.saiman.comms.Comms.CommsStatus.OPEN;
import static uk.co.saiman.instrument.HardwareConnection.CONNECTED;
import static uk.co.saiman.instrument.HardwareConnection.DISCONNECTED;
import static uk.co.saiman.instrument.stage.StageState.POSITION_REACHED;

import java.util.Optional;

import uk.co.saiman.comms.copley.CopleyComms;
import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.instrument.HardwareConnection;
import uk.co.saiman.instrument.stage.StageDevice;
import uk.co.saiman.instrument.stage.StageState;
import uk.co.saiman.observable.ObservablePropertyImpl;
import uk.co.saiman.observable.ObservableValue;
import uk.co.saiman.text.properties.PropertyLoader;

public abstract class CopleyStageDevice implements StageDevice {
  private CopleyStageProperties properties;
  private final ObservableValue<StageState> state = new ObservablePropertyImpl<>(POSITION_REACHED);

  private CopleyComms comms;
  private CopleyController controller;

  void initialize(CopleyComms comms, PropertyLoader loader) {
    this.comms = comms;
    this.controller = comms.openController();
    this.properties = loader.getProperties(CopleyStageProperties.class);
  }

  public CopleyStageProperties getProperties() {
    return properties;
  }

  public Optional<CopleyController> getController() {
    return Optional.ofNullable(controller);
  }

  @Override
  public String getName() {
    return properties.copleyXYStageName().get();
  }

  public boolean isConnected() {
    return comms.status().isEqual(OPEN);
  }

  public void reset() {
    comms.reset();
    controller = comms.openController();
  }

  @Override
  public ObservableValue<HardwareConnection> connectionState() {
    return comms.status().map(s -> s == OPEN ? CONNECTED : DISCONNECTED).toValue();
  }

  @Override
  public ObservableValue<StageState> state() {
    return state;
  }
}
