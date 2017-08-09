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
 * This file is part of uk.co.saiman.expressions.
 *
 * uk.co.saiman.expressions is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.expressions is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.expression;

import java.util.Observer;

import uk.co.saiman.expression.ActiveExpression;
import uk.co.saiman.expression.Expression;
import uk.co.saiman.expression.LockingExpression;
import uk.co.saiman.utility.IdentityProperty;
import uk.co.saiman.utility.Property;

/**
 * An {@link Expression} based on the behavior of the {@link IdentityProperty}
 * class, with the lazy updating behavior of {@link LockingExpression} for
 * {@link Observer}s.
 * 
 * @author Elias N Vasylenko
 * @param <T>
 *          The type of the expression.
 */
public class IdentityExpression<T> extends ActiveExpression<T> implements Property<T> {
	private T value;

	/**
	 * Construct with a default value of {@code null}.
	 */
	public IdentityExpression() {}

	/**
	 * Construct with the given default value.
	 * 
	 * @param value
	 *          The initial value of the expression.
	 */
	public IdentityExpression(T value) {
		this.value = value;
	}

	@Override
	public T set(T value) {
		beginWrite();

		try {
			T previous = this.value;
			this.value = value;
			return previous;
		} finally {
			endWrite();
		}
	}

	@Override
	protected final T getValueImpl(boolean dirty) {
		return value;
	}

	@Override
	public final T get() {
		return getValue();
	}
}