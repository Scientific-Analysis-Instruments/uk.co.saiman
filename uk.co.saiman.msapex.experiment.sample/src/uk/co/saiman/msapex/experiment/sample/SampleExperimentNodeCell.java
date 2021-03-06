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
 * This file is part of uk.co.saiman.msapex.experiment.sample.
 *
 * uk.co.saiman.msapex.experiment.sample is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment.sample is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment.sample;

import static uk.co.saiman.msapex.experiment.ExperimentStepCell.SUPPLEMENTAL_TEXT;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.MUIElement;

import javafx.scene.control.Label;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.experiment.sample.SampleExecutor;
import uk.co.saiman.experiment.variables.Variables;

public class SampleExperimentNodeCell {
  @Optional
  @PostConstruct
  public void prepare(
      Cell cell,
      SampleExecutor<?> sampleConductor,
      Variables variables,
      @Named(SUPPLEMENTAL_TEXT) Label supplemental) {
    cell = (Cell) (MUIElement) cell.getParent();

    cell
        .setIconURI(
            "platform:/plugin/uk.co.saiman.icons.fugue/uk/co/saiman/icons/fugue/size16/flask.png");
    variables
        .get(sampleConductor.sampleLocation())
        .map(Object::toString)
        .ifPresent(supplemental::setText);
  }
}
