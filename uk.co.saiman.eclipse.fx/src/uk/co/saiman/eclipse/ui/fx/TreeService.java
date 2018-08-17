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
 * This file is part of uk.co.saiman.eclipse.fx.
 *
 * uk.co.saiman.eclipse.fx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.fx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.ui.fx;

import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.model.ui.Tree;

public interface TreeService {
  String TEXT_ID = "text";
  String SUPPLEMENTAL_TEXT_ID = "supplementalText";

  Control createTree(Tree treeModel, Parent owner);

  default Control createTree(String treeModelId, Parent owner) {
    return createTree(getTree(treeModelId), owner);
  }

  Tree getTree(String treeModelId);

  static void setLabel(HBox node, String text) {
    ((Label) node.lookup("#" + TEXT_ID)).setText(text);
  }

  static void setSupplemental(HBox node, String text) {
    ((Label) node.lookup("#" + SUPPLEMENTAL_TEXT_ID)).setText(text);
  }
}
