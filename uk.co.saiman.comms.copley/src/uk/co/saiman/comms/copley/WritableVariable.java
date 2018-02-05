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
package uk.co.saiman.comms.copley;

import java.util.function.Function;

public interface WritableVariable<U> extends Variable<U> {
  void set(int axis, U value);

  default <T> WritableVariable<T> map(Class<T> type, Function<U, T> out, Function<T, U> in) {
    WritableVariable<U> base = this;

    return new WritableVariable<T>() {
      @Override
      public CopleyController getController() {
        return base.getController();
      }

      @Override
      public CopleyVariableID getID() {
        return base.getID();
      }

      @Override
      public VariableBank getBank() {
        return base.getBank();
      }

      @Override
      public Class<T> getType() {
        return type;
      }

      @Override
      public T get(int axis) {
        return out.apply(base.get(axis));
      }

      @Override
      public void set(int axis, T value) {
        base.set(axis, in.apply(value));
      }
    };
  }
}
