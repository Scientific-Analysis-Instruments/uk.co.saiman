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
 * This file is part of uk.co.saiman.collections.observable.
 *
 * uk.co.saiman.collections.observable is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.collections.observable is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.collection.observable;

import java.util.Collections;
import java.util.Set;

import uk.co.saiman.collection.SetDecorator;
import uk.co.saiman.observable.Disposable;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observer;

public abstract class SynchronizedObservableSet<S extends ObservableSet<S, E>, E>
    extends HotObservable<S> implements SetDecorator<E>, ObservableSet<S, E> {
  static class SynchronizedObservableSetImpl<E>
      extends SynchronizedObservableSet<SynchronizedObservableSetImpl<E>, E> {
    SynchronizedObservableSetImpl(ObservableSet<?, E> component) {
      super(component);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SynchronizedObservableSetImpl<E> copy() {
      return new SynchronizedObservableSetImpl<>(((ObservableSet<?, E>) getComponent()).copy());
    }
  }

  private final Set<E> component;
  private final Set<E> silentComponent;

  private final Observer<ObservableSet<?, ? extends E>> observer;
  private final HotObservable<Change<E>> changes;
  private final Observer<Change<E>> changeObserver;

  protected SynchronizedObservableSet(ObservableSet<?, E> component) {
    this.component = Collections.synchronizedSet(component);
    silentComponent = component.silent();

    observer = l -> next(getThis());
    component.weakReference().observe(observer);

    changes = new HotObservable<Change<E>>() {
      @Override
      public Disposable observe(Observer<? super Change<E>> observer) {
        synchronized (getMutex()) {
          return super.observe(observer);
        }
      }

      @Override
      public HotObservable<Change<E>> next(Change<E> item) {
        synchronized (getMutex()) {
          return super.next(item);
        }
      }
    };
    changeObserver = changes::next;
    component.changes().weakReference().observe(changeObserver);
  }

  public static <E> SynchronizedObservableSetImpl<E> over(ObservableSet<?, E> component) {
    return new SynchronizedObservableSetImpl<>(component);
  }

  public Object getMutex() {
    return component;
  }

  @Override
  public Set<E> getComponent() {
    return component;
  }

  @Override
  public HotObservable<Change<E>> changes() {
    return changes;
  }

  @Override
  public Set<E> silent() {
    return silentComponent;
  }

  @Override
  public String toString() {
    return getComponent().toString();
  }

  @Override
  public int hashCode() {
    return getComponent().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return getComponent().equals(obj);
  }

  @Override
  public Disposable observe(Observer<? super S> observer) {
    synchronized (getMutex()) {
      return super.observe(observer);
    }
  }

  @Override
  public HotObservable<S> next(S item) {
    synchronized (getMutex()) {
      return super.next(item);
    }
  }
}