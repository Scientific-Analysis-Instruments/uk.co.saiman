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

/**
 * Represents a function that accepts two arguments and produces a result. This
 * is the two-arity specialization of Function.
 * 
 * <p>
 * This is a functional interface whose functional method is
 * {@link #apply(Object, Object, Object)}.
 * 
 * @author Elias N Vasylenko
 * @param <O1>
 *          the type of the first argument to the function
 * @param <O2>
 *          the type of the second argument to the function
 * @param <O3>
 *          the type of the third argument to the function
 * @param <R>
 *          the type of the result of the function
 */
@FunctionalInterface
public interface TriFunction<O1, O2, O3, R> {
	/**
	 * Applies this function to the given arguments.
	 * 
	 * @param firstOperand
	 *          the first function argument
	 * @param secondOperand
	 *          the second function argument
	 * @param thirdOperand
	 *          the third function argument
	 * @return the result of a function application.
	 */
	public R apply(O1 firstOperand, O2 secondOperand, O3 thirdOperand);
}