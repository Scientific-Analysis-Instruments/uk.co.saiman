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
 * This file is part of uk.co.saiman.experiment.procedure.
 *
 * uk.co.saiman.experiment.procedure is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.procedure is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.instruction;

import static java.lang.String.format;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.data.Data;
import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.resource.Location;
import uk.co.saiman.experiment.production.Dependency;
import uk.co.saiman.experiment.production.Observation;
import uk.co.saiman.experiment.production.Preparation;
import uk.co.saiman.experiment.production.Result;
import uk.co.saiman.experiment.requirement.ConditionRequirement;
import uk.co.saiman.experiment.requirement.ResultRequirement;
import uk.co.saiman.experiment.variables.Variable;

/**
 * The context of an {@link Executor#execute(ExecutionContext) experiment
 * execution}, providing information about the current state, and enabling
 * modification of that state.
 * 
 * @author Elias N Vasylenko
 * @param <S> the type of the direct requirement of the executing node
 */
public interface ExecutionContext<S extends Dependency> {
  default <T> T getVariable(Variable<T> variable) {
    return getOptionalVariable(variable)
        .orElseThrow(
            () -> new ExecutorException(
                format("Variable %s is not available for execution %s", variable, this)));
  }

  <T> Optional<T> getOptionalVariable(Variable<T> variable);

  Instruction<S> instruction();

  S dependency();

  <U> U acquireCondition(ConditionRequirement<U> resource);

  <U> Result<? extends U> acquireResult(ResultRequirement<U> requirement);

  <U> Stream<Result<? extends U>> acquireResults(ResultRequirement<U> requirement);

  /**
   * Get a location which can be used to persist resource artifacts of this
   * execution. Typically a resource in this location is used to construct one or
   * more related {@link Data data} instances, one of which will be set as the
   * {@link #setResultData(Observation, Data) result} of the execution.
   * <p>
   * The location will be empty before execution begins.
   * 
   * @return an interface over the execution location
   */
  Location getLocation();

  <U> void prepareCondition(Preparation<U> condition, U resource);

  /**
   * Set a preliminary partial result value for this execution.
   * <p>
   * This method may be invoked multiple times during processing. The purpose is
   * to support live-updating of result data, and any values passed to this method
   * will be overridden by the return value of
   * {@link Executor#execute(ExecutionContext) execution} once processing
   * completes.
   * 
   * @param value an invalidation representing the preliminary result
   */
  <R> void setPartialResult(Observation<R> observation, Supplier<? extends R> value);

  /**
   * Set the result data for this execution. If the {@link Data#get() value} of
   * the given data matches the return value of
   * {@link Executor#execute(ExecutionContext) execution} once it completes it
   * does not have to be rewritten. This means that expensive disk IO can be
   * performed during the experiment process rather than saved until the end.
   * <p>
   * This method may be invoked at most once during any given execution, and this
   * precludes invocation of
   * {@link #setResultFormat(Observation, String, DataFormat)} during the same
   * execution.
   */
  <R> void setResultData(Observation<R> observation, Data<R> data);

  /**
   * Set the result format for this execution. If invoked, then once the
   * {@link Executor#execute(ExecutionContext) execution} is complete the returned
   * value will be persisted according to the given file name and format.
   * <p>
   * This method may be invoked at most once during any given execution, and this
   * precludes invocation of {@link #setResultData(Observation, Data)} during the
   * same execution.
   * 
   * @param name   the name of the result data file
   * @param format the format of the result data file
   */
  default <R> void setResultFormat(Observation<R> observation, String name, DataFormat<R> format) {
    setResultData(observation, Data.locate(getLocation(), name, format));
  }

  default <R> void setResult(Observation<R> observation, R value) {
    setPartialResult(observation, () -> value);
    completeObservation(observation);
  }

  void completeObservation(Observation<?> observation);
}
