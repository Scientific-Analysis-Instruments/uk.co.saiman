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
package uk.co.saiman.msapex.experiment;

import static uk.co.saiman.msapex.experiment.ExperimentStepCell.SUPPLEMENTAL_PSEUDO_CLASS;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import uk.co.saiman.eclipse.dialog.DialogUtilities;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.ui.fx.EditableCellText;
import uk.co.saiman.experiment.event.MoveStepEvent;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.msapex.experiment.i18n.ExperimentProperties;
import uk.co.saiman.msapex.experiment.workspace.WorkspaceExperiment;
import uk.co.saiman.msapex.experiment.workspace.WorkspaceExperiment.Status;

/**
 * Contribution for root experiment nodes in the experiment tree
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentNameCell {
  @Inject
  IEclipseContext context;
  @Inject
  Log log;
  @Inject
  @Localize
  ExperimentProperties text;

  @Inject
  Cell cell;

  @Inject
  EditableCellText nameEditor;

  @Inject
  WorkspaceExperiment experiment;

  @PostConstruct
  public void prepare(HBox node) {
    node.getChildren().add(nameEditor);
    HBox.setHgrow(nameEditor, Priority.SOMETIMES);

    nameEditor.setText(experiment.name());
    nameEditor.setTryUpdate(name -> renameExperiment(experiment, name));
    nameEditor.getLabel().pseudoClassStateChanged(SUPPLEMENTAL_PSEUDO_CLASS, true);
  }

  private boolean renameExperiment(WorkspaceExperiment experiment, String name) {
    try {
      experiment.rename(name);
      return true;

    } catch (Exception e) {
      log.log(Level.ERROR, e);

      Alert alert = new Alert(AlertType.ERROR);
      DialogUtilities.addStackTrace(alert, e);
      alert.setTitle(text.renameExperimentFailedDialog().toString());
      alert.setHeaderText(text.renameExperimentFailedText(experiment).toString());
      alert.setContentText(text.renameExperimentFailedDescription().toString());
      alert.showAndWait();

      return false;
    }
  }

  @Inject
  @Optional
  public void updateName(MoveStepEvent event) {
    if (experiment.status() == Status.OPEN
        && Objects.equals(event.step(), experiment.experiment())) {
      nameEditor.setText(event.id());
    }
  }
}
