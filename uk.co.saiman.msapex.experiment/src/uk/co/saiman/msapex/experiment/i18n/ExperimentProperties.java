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
package uk.co.saiman.msapex.experiment.i18n;

import java.nio.file.Path;

import uk.co.saiman.experiment.ExperimentLifecycleState;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.properties.Localized;

/**
 * Properties interface for texts relating to experiments.
 * 
 * @author Elias N Vasylenko
 */
@SuppressWarnings("javadoc")
public interface ExperimentProperties {
  Localized<String> newExperiment();

  Localized<String> newExperimentName();

  /**
   * @param state
   *          the state to localize
   * @return localized name of the state
   */
  default Localized<String> lifecycleState(ExperimentLifecycleState state) {
    switch (state) {
    case COMPLETION:
      return lifecycleStateCompletion();
    case CONFIGURATION:
      return lifecycleStateConfiguration();
    case DETACHED:
      return lifecycleStateDetached();
    case FAILURE:
      return lifecycleStatefailure();
    case PROCESSING:
      return lifecycleStateProcessing();
    case WAITING:
      return lifecycleStateWaiting();
    }
    throw new AssertionError();
  }

  Localized<String> lifecycleStateCompletion();

  Localized<String> lifecycleStateConfiguration();

  Localized<String> lifecycleStateDetached();

  Localized<String> lifecycleStatefailure();

  Localized<String> lifecycleStateProcessing();

  Localized<String> lifecycleStateWaiting();

  Localized<String> configuration();

  Localized<String> missingResult();

  Localized<String> missingExperimentType(String id);

  Localized<String> experimentRoot();

  Localized<String> overwriteData();

  Localized<String> overwriteDataConfirmation(Path newLocation);

  Localized<String> renameExperiment();

  Localized<String> renameExperimentName(String name);

  Localized<String> cannotCreateWorkspace(Workspace experimentWorkspace);

  Localized<String> addSpectrumProcessor();

  Localized<String> addSpectrumProcessorDescription();
}