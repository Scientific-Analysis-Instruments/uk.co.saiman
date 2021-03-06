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

import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.instruction.Executor;
import uk.co.saiman.experiment.production.Production;
import uk.co.saiman.msapex.experiment.workspace.WorkspaceExperiment;
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

  Localized<String> configuration();

  Localized<String> missingResult();

  Localized<String> missingExperimentType(String id);

  Localized<String> experimentRoot();

  Localized<String> overwriteData();

  Localized<String> overwriteDataConfirmation(Path newLocation);

  Localized<String> renameExperiment();

  Localized<String> renameExperimentName(String name);

  Localized<String> addSpectrumProcessor();

  Localized<String> addSpectrumProcessorDescription();

  Localized<String> removeExperimentDialog();

  Localized<String> removeExperimentText(WorkspaceExperiment experiment);

  Localized<String> removeExperimentConfirmation();

  Localized<String> removeExperimentFailedDialog();

  Localized<String> removeExperimentFailedText(WorkspaceExperiment experiment);

  Localized<String> removeExperimentFailedDescription();

  Localized<String> removeStepFailedDialog();

  Localized<String> removeStepFailedText(Step step);

  Localized<String> removeStepFailedDescription();

  Localized<String> renameExperimentFailedDialog();

  Localized<String> renameExperimentFailedText(WorkspaceExperiment experiment);

  Localized<String> renameExperimentFailedDescription();

  Localized<String> newExperimentFailedDi();

  Localized<String> newExperimentFailedText(String name);

  Localized<String> newExperimentFailedDescription();

  Localized<String> openExperimentFailedDialog();

  Localized<String> openExperimentFailedText(WorkspaceExperiment experiment);

  Localized<String> openExperimentFailedDescription();

  Localized<String> attachStepFailedDialog();

  Localized<String> attachStepFailedText(WorkspaceExperiment experiment, Localized<String> name);

  Localized<String> attachStepFailedText(Step step, Executor<?> procedure);

  Localized<String> attachStepFailedDescription();

}
