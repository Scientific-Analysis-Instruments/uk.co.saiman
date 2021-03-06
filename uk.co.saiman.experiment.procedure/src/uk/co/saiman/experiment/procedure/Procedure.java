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
 * This file is part of uk.co.saiman.experiment.procedure.
 *
 * uk.co.saiman.experiment.procedure is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.procedure is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.procedure;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;

public class Procedure {
  private final String id;
  private final LinkedHashMap<ExperimentPath<Absolute>, Instruction<?>> instructions;

  public Procedure(String id, Collection<? extends Instruction<?>> instructions) {
    this.id = id;
    this.instructions = new LinkedHashMap<>();
    for (var instruction : instructions) {
      this.instructions.put(instruction.path(), instruction);
    }
  }

  public String id() {
    return id;
  }

  public Stream<Instruction<?>> instructions() {
    return instructions.values().stream();
  }

  public Stream<ExperimentPath<Absolute>> paths() {
    return instructions.keySet().stream();
  }

  public Optional<Instruction<?>> instruction(ExperimentPath<?> path) {
    return Optional.ofNullable(instructions.get(path));
  }
}
