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
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.scheduling.concurrent;

import java.util.Collection;

import uk.co.saiman.experiment.ExperimentStep;
import uk.co.saiman.experiment.scheduling.Scheduler;
import uk.co.saiman.experiment.scheduling.SchedulingContext;

public class ConcurrentScheduler implements Scheduler {
  private final SchedulingContext schedulingContext;
  private final int maximumConcurrency;

  public ConcurrentScheduler(SchedulingContext schedulingContext, int maximumConcurrency) {
    this.schedulingContext = schedulingContext;
    this.maximumConcurrency = maximumConcurrency;
  }

  @Override
  public void scheduleSteps(Collection<? extends ExperimentStep<?>> steps) {
    // TODO Auto-generated method stub

  }

  @Override
  public void unscheduleSteps(Collection<? extends ExperimentStep<?>> steps) {
    // TODO Auto-generated method stub

  }
}
