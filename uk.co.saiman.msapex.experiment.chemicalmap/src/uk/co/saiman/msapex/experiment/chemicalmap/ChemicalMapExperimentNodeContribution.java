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
 * This file is part of uk.co.saiman.msapex.experiment.chemicalmap.
 *
 * uk.co.saiman.msapex.experiment.chemicalmap is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment.chemicalmap is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment.chemicalmap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import javafx.scene.Node;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.chemicalmap.ChemicalMapConfiguration;
import uk.co.saiman.experiment.chemicalmap.ChemicalMapExperimentType;
import uk.co.saiman.msapex.experiment.ExperimentPart;
import uk.co.saiman.eclipse.CommandTreeCellContribution;
import uk.co.saiman.eclipse.EclipseTreeContribution;
import uk.co.saiman.fx.PseudoClassTreeCellContribution;
import uk.co.saiman.fx.TreeCellContribution;
import uk.co.saiman.fx.TreeItemData;
import uk.co.saiman.fx.TreeTextContribution;

/**
 * An implementation of {@link TreeCellContribution} which registers the
 * experiment tree pop-up menu from the experiment project model fragment.
 * 
 * @author Elias N Vasylenko
 */
@Component(service = EclipseTreeContribution.class, scope = ServiceScope.PROTOTYPE)
public class ChemicalMapExperimentNodeContribution extends
		CommandTreeCellContribution<ExperimentNode<? extends ChemicalMapExperimentType<?>, ? extends ChemicalMapConfiguration>>
		implements
		TreeTextContribution<ExperimentNode<? extends ChemicalMapExperimentType<?>, ? extends ChemicalMapConfiguration>>,
		PseudoClassTreeCellContribution<ExperimentNode<? extends ChemicalMapExperimentType<?>, ? extends ChemicalMapConfiguration>> {
	/**
	 * Create over open command
	 */
	public ChemicalMapExperimentNodeContribution() {
		super(ExperimentPart.OPEN_EXPERIMENT_COMMAND);
	}

	@Override
	public <U extends ExperimentNode<? extends ChemicalMapExperimentType<?>, ? extends ChemicalMapConfiguration>> String getText(
			TreeItemData<U> data) {
		return data.data().getType().getName();
	}

	@Override
	public <U extends ExperimentNode<? extends ChemicalMapExperimentType<?>, ? extends ChemicalMapConfiguration>> String getSupplementalText(
			TreeItemData<U> data) {
		return data.data().getState().getChemicalMapName();
	}

	@Override
	public <U extends ExperimentNode<? extends ChemicalMapExperimentType<?>, ? extends ChemicalMapConfiguration>> Node configureCell(
			TreeItemData<U> data,
			Node content) {
		super.configureCell(data, content);
		return PseudoClassTreeCellContribution.super.configureCell(data, content);
	}
}