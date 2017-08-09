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
 * This file is part of uk.co.saiman.fx.
 *
 * uk.co.saiman.fx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.fx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.fx;

import static uk.co.saiman.fx.FxmlLoadBuilder.build;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * The default UI node provided by {@link DefaultTreeCellContribution}.
 * 
 * @author Elias N Vasylenko
 */
public class DefaultTreeCellContent extends HBox {
	@FXML
	private Label text;
	@FXML
	private Label supplementalText;

	/**
	 * @param text
	 *          the {@link TreeTextContribution#getText(TreeItemData) main text}
	 *          for the tree item
	 * @param supplementalText
	 *          the {@link TreeTextContribution#getText(TreeItemData) supplemental
	 *          text} for the tree item
	 */
	public DefaultTreeCellContent(String text, String supplementalText) {
		build().object(this).load();

		setMinWidth(0);
		prefWidth(0);

		this.text.setText(text);
		this.supplementalText.setText(supplementalText);
	}
}
