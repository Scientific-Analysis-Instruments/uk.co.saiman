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
 * This file is part of uk.co.saiman.experiment.api.
 *
 * uk.co.saiman.experiment.api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.persistence;

import java.util.stream.Stream;

public interface PersistedStateList extends Iterable<PersistedState> {
  PersistedState add();

  PersistedState add(int index);

  PersistedState get(int index);

  PersistedState remove(int index);

  PersistedState move(int fromIndex, int toIndex);

  void clear();

  default PersistedState remove(PersistedState state) {
    for (int i = 0; i < size(); i++) {
      if (get(i).equals(state)) {
        return remove(i);
      }
    }
    throw new IndexOutOfBoundsException();
  }

  int size();

  Stream<PersistedState> stream();
}
