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
 * This file is part of uk.co.saiman.experiment.api.
 *
 * uk.co.saiman.experiment.api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment;

import static uk.co.saiman.experiment.ExperimentNodeConstraint.FULFILLED;
import static uk.co.saiman.experiment.ExperimentNodeConstraint.UNFULFILLED;
import static uk.co.saiman.reflection.token.TypeToken.forType;

import uk.co.saiman.reflection.token.TypeParameter;
import uk.co.saiman.reflection.token.TypeToken;

public interface ProcessingType<S, T, U> extends ExperimentType<S, U> {
  @Override
  default boolean hasAutomaticExecution() {
    return true;
  }

  @Override
  default boolean isExecutionContextDependent() {
    return false;
  }

  @Override
  default U execute(ExecutionContext<S, U> context) {
    @SuppressWarnings("unchecked")
    T input = (T) context.node().getParent().get().getResult().get();
    return process(input);
  }

  U process(T input);

  @Override
  default ExperimentNodeConstraint mayComeAfter(ExperimentNode<?, ?> parentNode) {
    return parentNode.getType().getResultType().isAssignableTo(getInputType())
        ? FULFILLED
        : UNFULFILLED;
  }

  @Override
  default ExperimentNodeConstraint mayComeBefore(
      ExperimentNode<?, ?> penultimateDescendantNode,
      ExperimentType<?, ?> descendantNodeType) {
    return FULFILLED;
  }

  /**
   * @return the exact generic type of the input of this processing step
   */
  default TypeToken<T> getInputType() {
    return forType(getThisType())
        .resolveSupertype(ExperimentType.class)
        .resolveTypeArgument(new TypeParameter<T>() {})
        .getTypeToken();
  }
}
