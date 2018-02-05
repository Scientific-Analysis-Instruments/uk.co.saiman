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
 * This file is part of uk.co.saiman.comms.copley.
 *
 * uk.co.saiman.comms.copley is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.copley is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley.impl;

import java.util.Optional;

import uk.co.saiman.comms.copley.BankedVariable;
import uk.co.saiman.comms.copley.CopleyVariableID;
import uk.co.saiman.comms.copley.VariableBank;
import uk.co.saiman.comms.copley.WritableVariable;

class BankedVariableImpl<U> extends WritableVariableImpl<U> implements BankedVariable<U> {
  public BankedVariableImpl(
      CopleyControllerImpl controller,
      CopleyVariableID id,
      Class<U> variableClass) {
    this(controller, id, variableClass, VariableBank.ACTIVE);
  }

  private BankedVariableImpl(
      CopleyControllerImpl controller,
      CopleyVariableID id,
      Class<U> variableClass,
      VariableBank bank) {
    super(controller, id, variableClass, bank);
  }

  @Override
  public Optional<WritableVariable<U>> trySwitchBank(VariableBank bank) {
    return Optional.of(switchBank(bank));
  }

  @Override
  public BankedVariable<U> switchBank(VariableBank bank) {
    return new BankedVariableImpl<>(getController(), getID(), getType(), bank);
  }
}
