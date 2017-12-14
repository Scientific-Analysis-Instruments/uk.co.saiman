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
 * This file is part of uk.co.saiman.experiment.sample.
 *
 * uk.co.saiman.experiment.sample is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.sample is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.sample;

import static uk.co.saiman.experiment.ExperimentNodeConstraint.FULFILLED;
import static uk.co.saiman.experiment.ExperimentNodeConstraint.UNFULFILLED;

import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentNodeConstraint;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.VoidExperimentType;

/**
 * Configure the sample position to perform an experiment at. Typically most
 * other experiment nodes will be descendant to a sample experiment node, such
 * that they operate on the configured sample.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the type of sample configuration for the instrument
 */
public interface SampleExperimentType<T extends SampleConfiguration> extends VoidExperimentType<T> {
  @Override
  default String getName() {
    return "Sample";
  }

  @Override
  default ExperimentNodeConstraint mayComeAfter(ExperimentNode<?, ?> parentNode) {
    /*
     * by default, must be a direct descendant of the experiment root
     */
    return parentNode.getParent().isPresent() ? UNFULFILLED : FULFILLED;
  }

  @Override
  default ExperimentNodeConstraint mayComeBefore(
      ExperimentNode<?, ?> penultimateDescendantNode,
      ExperimentType<?, ?> descendantNodeType) {
    /*
     * by default, no restrictions
     */
    return FULFILLED;
  }
}
