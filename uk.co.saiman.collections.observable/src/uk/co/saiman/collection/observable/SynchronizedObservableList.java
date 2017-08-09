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
import java.util.List;

import uk.co.saiman.collection.ListDecorator;
import uk.co.saiman.observable.Disposable;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.observable.Observer;

public abstract class SynchronizedObservableList<S extends ObservableList<S, E>, E>
    extends HotObservable<S> implements ListDecorator<E>, ObservableList<S, E> {
  static class SynchronizedObservableListImpl<E>
      extends SynchronizedObservableList<SynchronizedObservableListImpl<E>, E> {
    SynchronizedObservableListImpl(ObservableList<?, E> component) {
      super(component);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SynchronizedObservableListImpl<E> copy() {
      return new SynchronizedObservableListImpl<>(((ObservableList<?, E>) getComponent()).copy());
    }
  }

  private final List<E> component;
  private final List<E> silentComponent;

  private final HotObservable<Change<E>> changes;

  protected SynchronizedObservableList(ObservableList<?, E> component) {
    this.component = Collections.synchronizedList(component);
    silentComponent = component.silent();

    component.weakReference(this).observe(m -> m.owner().next(m.owner().getThis()));

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
    component.changes().weakReference(this).observe(m -> m.owner().changes.next(m.message()));
  }

  public static <E> SynchronizedObservableListImpl<E> over(ObservableList<?, E> component) {
    return new SynchronizedObservableListImpl<>(component);
  }

  public Object getMutex() {
    return component;
  }

  @Override
  public List<E> getComponent() {
    return component;
  }

  @Override
  public Observable<Change<E>> changes() {
    return changes;
  }

  @Override
  public List<E> silent() {
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
