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
 * This file is part of uk.co.saiman.utilities.
 *
 * uk.co.saiman.utilities is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.utilities is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.function;

import java.util.function.Function;

/**
 * As {@link Function} but parameterized over an exception type which is allowed
 * to be thrown by {@link #apply(Object)}.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the type of the input to the function
 * @param <R>
 *          the type of the result of the function
 * @param <E>
 *          the type of exception which may be thrown
 */
public interface ThrowingFunction<T, R, E extends Exception> {
	/**
	 * Applies this function to the given argument.
	 *
	 * @param t
	 *          the function argument
	 * @return the function result
	 * @throws E
	 *           an exception thrown by the implementor
	 */
	R apply(T t) throws E;
}
