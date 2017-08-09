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
 * This file is part of uk.co.saiman.mathematics.
 *
 * uk.co.saiman.mathematics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.mathematics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.mathematics.values;

import java.util.LinkedHashSet;
import java.util.Set;

import uk.co.saiman.expression.CopyDecouplingExpression;
import uk.co.saiman.expression.Expression;
import uk.co.saiman.expression.SelfExpression;
import uk.co.saiman.mathematics.operation.Incrementable;
import uk.co.saiman.mathematics.operation.Multipliable;
import uk.co.saiman.mathematics.operation.Negatable;
import uk.co.saiman.mathematics.operation.Scalable;
import uk.co.saiman.mathematics.operation.Subtractable;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.observable.Observer;
import uk.co.saiman.utility.Copyable;
import uk.co.saiman.utility.Self;

public abstract class Value<S extends Value<S>> extends Number implements Multipliable<S, Value<?>>,
    Subtractable<S, Value<?>>, Negatable<S, S>, Scalable<S>, Incrementable<S>, Self<S>,
    SelfExpression<S>, Copyable<S>, Comparable<Value<?>>, CopyDecouplingExpression<S> {
  private static final long serialVersionUID = -979949605176385397L;

  private final Set<Observer<? super S>> observers;
  private boolean evaluated = true;

  public Value() {
    this(0);
  }

  public Value(Number value) {
    observers = new LinkedHashSet<>();

    setValue(value);
  }

  public Value(Value<?> value) {
    this((Number) value);
  }

  @Override
  public int compareTo(Value<?> other) {
    int comparison = compareToAtSupportedPrecision(other);

    if (comparison == 0) {
      comparison = -other.compareToAtSupportedPrecision(this);
    }

    return comparison;
  }

  @Override
  public abstract S negate();

  public abstract S reciprocate();

  public S getReciprocal() {
    return copy().reciprocate();
  }

  @Override
  public final S getMultiplied(Value<?> value) {
    return Scalable.super.getMultiplied(value);
  }

  @Override
  public abstract String toString();

  public abstract S setValue(Number value);

  @Override
  public abstract boolean equals(Object that);

  protected abstract boolean equals(Value<?> that);

  public abstract S unitInTheLastPlaceAbove();

  public abstract S unitInTheLastPlaceBelow();

  public final S unitInTheLastPlaceLarger() {
    return maximum(unitInTheLastPlaceAbove(), unitInTheLastPlaceBelow());
  }

  public final S unitInTheLastPlaceSmaller() {
    return minimum(unitInTheLastPlaceAbove(), unitInTheLastPlaceBelow());
  }

  public static <S extends Value<S>> S maximum(S first, S second) {
    if (first.compareTo(second) > 0) {
      return first;
    } else {
      return second;
    }
  }

  public static <S extends Value<S>> S minimum(S first, S second) {
    if (first.compareTo(second) < 0) {
      return first;
    } else {
      return second;
    }
  }

  @Override
  public synchronized final S getValue() {
    evaluated = true;
    S result = getThis();
    return result;
  }

  protected final void update() {
    if (evaluated) {
      evaluated = false;
      postUpdate();
    }
  }

  protected final void postUpdate() {
    for (Observer<? super S> observer : observers)
      observer.onNext(null);
  }

  @Override
  public Observable<Expression<? extends S>> invalidations() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public abstract S multiply(Value<?> value);

  @Override
  public abstract S divide(Value<?> value);

  public abstract boolean equals(double value);

  public abstract boolean equals(float value);

  public abstract boolean equals(int value);

  public abstract boolean equals(long value);

  public abstract int compareToAtSupportedPrecision(/*  */Value<?> other);

  public abstract int getMultipliedPrimitive(int value);

  public abstract long getMultipliedPrimitive(long value);

  public abstract float getMultipliedPrimitive(float value);

  public abstract double getMultipliedPrimitive(double value);

  public abstract int getDividedPrimitive(int value);

  public abstract long getDividedPrimitive(long value);

  public abstract float getDividedPrimitive(float value);

  public abstract double getDividedPrimitive(double value);

  @Override
  public abstract int intValue();

  @Override
  public abstract long longValue();

  @Override
  public abstract float floatValue();

  @Override
  public abstract double doubleValue();

  public abstract S square();

  public S getSquared() {
    return copy().square();
  }

  public abstract S squareRoot();

  public S getSquareRoot() {
    return copy().squareRoot();
  }

  public abstract S exponentiate(Value<?> exponential);

  public S getExponentiated(Value<?> exponential) {
    return copy().exponentiate(exponential);
  }

  public abstract S root(Value<?> root);

  public S getRoot(Value<?> root) {
    return copy().root(root);
  }

  public abstract S modulus();

  public S getModulus() {
    return copy().modulus();
  }
}