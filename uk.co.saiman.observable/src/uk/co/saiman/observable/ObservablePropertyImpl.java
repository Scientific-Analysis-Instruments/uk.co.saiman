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
 * This file is part of uk.co.saiman.observable.
 *
 * uk.co.saiman.observable is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.observable is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.observable;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * A simple implementation of {@link ObservableProperty} which maintains a list
 * of listeners to receive change events fired with {@link #set(Object)}.
 * <p>
 * Addition and removal of observers, as well as the firing of events, are
 * synchronized on the implementation object.
 * 
 * @author Elias N Vasylenko
 * @param <T>
 *          the type of event message to produce
 */
public class ObservablePropertyImpl<T> implements ObservableProperty<T> {
  private final HotObservable<T> backingObservable;

  private T value;
  private Throwable failure;

  private final BiPredicate<T, T> equality;

  public ObservablePropertyImpl(T initialValue) {
    this(Objects::equals, initialValue);
  }

  public ObservablePropertyImpl(BiPredicate<T, T> equality, T initialValue) {
    this.backingObservable = new HotObservable<>();
    this.equality = equality;
    this.value = requireNonNull(initialValue);
  }

  public ObservablePropertyImpl(Throwable initialProblem) {
    this(Objects::equals, initialProblem);
  }

  public ObservablePropertyImpl(BiPredicate<T, T> equality, Throwable initialProblem) {
    this.backingObservable = new HotObservable<>();
    this.equality = equality;
    this.failure = requireNonNull(initialProblem);
  }

  @Override
  public Observable<Change<T>> changes() {
    return observer -> materialize().retrying(backingObservable.materialize()).observe(
        new PassthroughObserver<ObservableValue<T>, Change<T>>(observer) {
          private ObservableValue<T> previousValue;

          @Override
          public void onNext(ObservableValue<T> message) {
            ObservableValue<T> previousValue = this.previousValue;
            this.previousValue = message;

            if (previousValue != null) {
              getDownstreamObserver().onNext(new Change<T>() {
                @Override
                public ObservableValue<T> previousValue() {
                  return previousValue;
                }

                @Override
                public ObservableValue<T> newValue() {
                  return message;
                }
              });
            }
          }
        });
  }

  @Override
  public Disposable observe(Observer<? super T> observer) {
    Disposable disposable = backingObservable.observe(observer);

    if (value != null) {
      observer.onNext(value);
    } else {
      observer.onFail(failure);
    }

    return disposable;
  }

  @Override
  public synchronized T set(T value) {
    if (failure == null && equality.test(this.value, value))
      return value;

    backingObservable.next(value);

    T previous = this.value;
    failure = null;
    this.value = value;

    return previous;
  }

  @Override
  public synchronized void setProblem(Throwable t) {
    backingObservable.fail(t);

    value = null;
    failure = t;

    backingObservable.start();
  }

  @Override
  public T get() {
    if (value == null)
      throw new MissingValueException(this, failure);
    return value;
  }
}