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

import java.util.stream.Stream;

import javax.measure.Quantity;

import uk.co.saiman.instrument.stage.StageDevice;
import uk.co.saiman.instrument.stage.msapex.StageDiagram;

/**
 * A base interface for sample stage configuration.
 * 
 * @author Elias N Vasylenko
 */
public interface StageConfiguration extends SampleConfiguration {
  StageDevice stageDevice();

  /**
   * The image corresponding to the {@link StageDiagram#getImage() image} of the
   * {@link StageDevice#getDiagram() device diagram} at the time the experiment
   * was executed. This is persisted as part of the configuration so that it can
   * be visualized post-execution on systems where the original device interface
   * is unavailable.
   */
  void stageImage(); // TODO actually return an image...

  Stream<Quantity<?>> coordinates();
}
