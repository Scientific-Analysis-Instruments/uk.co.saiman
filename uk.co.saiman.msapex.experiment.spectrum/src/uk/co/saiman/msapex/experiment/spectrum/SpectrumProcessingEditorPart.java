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
 * This file is part of uk.co.saiman.msapex.experiment.spectrum.
 *
 * uk.co.saiman.msapex.experiment.spectrum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment.spectrum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment.spectrum;

import static uk.co.saiman.experiment.processing.ProcessingDeclaration.PROCESSING_VARIABLE;
import static uk.co.saiman.fx.FxmlLoadBuilder.buildWith;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.extensions.Service;
import org.eclipse.fx.core.di.LocalInstance;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.eclipse.ui.fx.TreeService;
import uk.co.saiman.experiment.processing.Processing;
import uk.co.saiman.experiment.processing.ProcessingService;
import uk.co.saiman.experiment.variables.Variables;
import uk.co.saiman.msapex.experiment.spectrum.i18n.SpectrumProperties;

public class SpectrumProcessingEditorPart {
  @Inject
  @Localize
  private SpectrumProperties properties;

  @FXML
  private ScrollPane processingTreeScrollPane;
  private Control processingTree;

  @Inject
  private IEclipseContext context;

  @Inject
  @Service
  private ProcessingService processingService;

  @Inject
  SpectrumProcessingEditorPart(
      BorderPane container,
      TreeService treeService,
      @LocalInstance FXMLLoader loader,
      Variables variables) {
    container.setCenter(buildWith(loader).controller(this).loadRoot());

    variables
        .get(PROCESSING_VARIABLE)
        .map(processingService::loadDeclaration)
        .ifPresent(p -> context.set(Processing.class, p));

    processingTree = treeService.createTree(ProcessingTree.ID, processingTreeScrollPane);
    processingTreeScrollPane.setContent(processingTree);
  }
}
