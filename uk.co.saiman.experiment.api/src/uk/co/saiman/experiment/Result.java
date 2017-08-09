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

import java.nio.file.Path;
import java.util.Optional;

import uk.co.saiman.observable.Observable;
import uk.co.saiman.reflection.token.ReifiedToken;
import uk.co.saiman.reflection.token.TypeArgument;
import uk.co.saiman.reflection.token.TypeToken;

public interface Result<T>
		extends Observable<Optional<T>>, ReifiedToken<Result<T>> {
	ExperimentNode<?, ?> getExperimentNode();

	/**
	 * Experiment data root directories are defined hierarchically from the
	 * {@link Workspace#getWorkspaceDataPath() workspace path}.
	 * 
	 * @return the data root of the experiment
	 */
	Path getResultDataPath();

	ResultType<T> getResultType();

	Optional<T> getData();

	@Override
	default TypeToken<Result<T>> getThisTypeToken() {
		return new TypeToken<Result<T>>() {}
				.withTypeArguments(new TypeArgument<T>(getResultType().getDataType()) {});
	}
}
