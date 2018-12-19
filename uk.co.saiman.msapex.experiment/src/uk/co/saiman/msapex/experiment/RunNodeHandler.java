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
 * This file is part of uk.co.saiman.msapex.experiment.
 *
 * uk.co.saiman.msapex.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;

import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.experiment.ExperimentStep;
import uk.co.saiman.log.Log;
import uk.co.saiman.msapex.experiment.i18n.ExperimentProperties;

/**
 * Add an experiment to the workspace.
 * 
 * @author Elias N Vasylenko
 */
public class RunNodeHandler {
  @Inject
  Log log;
  @Inject
  @Localize
  ExperimentProperties text;

  @Execute
  void execute(ExperimentStep<?> step) {
    step.schedule();
  }
}
