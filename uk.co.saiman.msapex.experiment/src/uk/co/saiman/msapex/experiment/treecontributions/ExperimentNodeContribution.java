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
package uk.co.saiman.msapex.experiment.treecontributions;

import java.util.stream.Stream;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import uk.co.saiman.eclipse.MenuTreeCellContribution;
import uk.co.saiman.experiment.ExperimentLifecycleState;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.Result;
import uk.co.saiman.fx.TreeChildContribution;
import uk.co.saiman.fx.TreeContribution;
import uk.co.saiman.fx.TreeItemData;
import uk.co.saiman.fx.TreeTextContribution;
import uk.co.saiman.reflection.token.TypedReference;

/**
 * Contribution for all experiment nodes in the experiment tree
 * 
 * @author Elias N Vasylenko
 */
@Component(
    service = TreeContribution.class,
    scope = ServiceScope.PROTOTYPE,
    property = Constants.SERVICE_RANKING + ":Integer=" + -100)
public class ExperimentNodeContribution extends MenuTreeCellContribution<ExperimentNode<?, ?>>
    implements TreeChildContribution<ExperimentNode<?, ?>>,
    TreeTextContribution<ExperimentNode<?, ?>> {
  private static final String EXPERIMENT_TREE_POPUP_MENU = "uk.co.saiman.msapex.experiment.popupmenu.node";

  /**
   * Create with experiment tree popup menu
   */
  public ExperimentNodeContribution() {
    super(EXPERIMENT_TREE_POPUP_MENU);
  }

  @Override
  public <U extends ExperimentNode<?, ?>> Node configureCell(TreeItemData<U> data, Node content) {
    data.data().lifecycleState().weakReference(data).observe(m -> m.owner().refresh(false));

    /*
     * label to show lifecycle state icon
     */
    Label lifecycleIndicator = new Label();
    lifecycleIndicator.pseudoClassStateChanged(
        PseudoClass.getPseudoClass(
            ExperimentLifecycleState.class.getSimpleName() + "-"
                + data.data().lifecycleState().get().toString()),
        true);

    /*
     * shift lifecycle label to the far right
     */
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.SOMETIMES);

    HBox contentWrapper = new HBox(content, spacer, lifecycleIndicator);
    contentWrapper.setMinWidth(0);
    contentWrapper.prefWidth(0);

    return super.configureCell(data, configurePseudoClass(content));
  }

  @Override
  public <U extends ExperimentNode<?, ?>> boolean hasChildren(TreeItemData<U> data) {
    return data.data().getChildren().findAny().isPresent()
        || data.data().getResults().findAny().isPresent();
  }

  @Override
  public <U extends ExperimentNode<?, ?>> Stream<TypedReference<?>> getChildren(
      TreeItemData<U> data) {
    return Stream.concat(
        data.data().getChildren().map(ExperimentNode::asTypedObject),
        data.data().getResults().map(Result::asTypedObject));
  }

  @Override
  public <U extends ExperimentNode<?, ?>> String getText(TreeItemData<U> data) {
    return data.data().getID();
  }

  @Override
  public <U extends ExperimentNode<?, ?>> String getSupplementalText(TreeItemData<U> data) {
    return data.data().getType().getName() + " [" + data.data().lifecycleState().get() + "]";
  }
}
