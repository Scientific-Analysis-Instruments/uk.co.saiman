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
 * This file is part of uk.co.saiman.experiment.processing.
 *
 * uk.co.saiman.experiment.processing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.processing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.processing;

import static uk.co.saiman.data.function.processing.DataProcessor.identity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.experiment.state.StateMap;

public class Processing {
  private List<Processor> processors;

  public Processing(Collection<? extends Processor> processors) {
    // TODO Auto-generated constructor stub
  }

  public Stream<Processor> processors() {
    return processors.stream();
  }

  public StateMap getState() {
    return null;
  }

  public Processing withState(StateMap state) {
    return null;
  }

  public DataProcessor getProcessor() {
    return processors().map(p -> p.getProcessor()).reduce(identity(), DataProcessor::andThen);
  }
}
