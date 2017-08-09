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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

import uk.co.saiman.collection.SetDecorator;
import uk.co.saiman.observable.HotObservable;

public abstract class ObservableSetDecorator<S extends ObservableSet<S, E>, E>
    extends HotObservable<S> implements SetDecorator<E>, ObservableSet<S, E> {
  static class ObservableSetDecoratorImpl<C extends Set<E>, E>
      extends ObservableSetDecorator<ObservableSetDecoratorImpl<C, E>, E> {
    private Function<? super C, ? extends C> copy;

    ObservableSetDecoratorImpl(C set, Function<? super C, ? extends C> copy) {
      super(set);
      this.copy = copy;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ObservableSetDecoratorImpl<C, E> copy() {
      return new ObservableSetDecoratorImpl<>(copy.apply((C) getComponent()), copy);
    }
  }

  class ChangeImpl implements Change<E> {
    Set<E> adding = new HashSet<>();
    Set<E> removing = new HashSet<>();

    private final Set<E> added = Collections.unmodifiableSet(adding);
    private final Set<E> removed = Collections.unmodifiableSet(removing);

    @Override
    public Set<E> added() {
      return added;
    }

    @Override
    public Set<E> removed() {
      return removed;
    }
  }

  private final Set<E> component;

  private final HotObservable<Change<E>> changeObservable = new HotObservable<>();

  private int firingDepth = 0;
  private boolean doChange;

  private int changeDepth = 0;
  private ChangeImpl change;

  protected ObservableSetDecorator(Set<E> component) {
    this.component = component;
  }

  @Override
  public Set<E> getComponent() {
    return component;
  }

  protected boolean beginChange() {
    if (changeDepth++ == 0) {
      doChange = changeObservable.hasObservers();

      if (doChange) {
        if (change == null || firingDepth > 0) {
          change = new ChangeImpl();
        }
      } else {
        change = null;
      }

      return true;
    } else {
      return false;
    }
  }

  protected boolean endChange() {
    if (--changeDepth == 0) {
      if (change != null) {
        fireChange(change);

        change.adding.clear();
        change.removing.clear();
      }

      return true;
    } else {
      return false;
    }
  }

  protected void fireChange(Change<E> change) {
    if (!change.added().isEmpty() || !change.removed().isEmpty()) {
      firingDepth++;
      changeObservable.next(change);
      firingDepth--;
      fireEvent();
    }
  }

  protected void fireEvent() {
    next(getThis());
  }

  @Override
  public boolean add(E e) {
    try {
      beginChange();

      boolean changed = SetDecorator.super.add(e);

      if (doChange && changed && !change.removing.remove(e)) {
        change.adding.add(e);
      }

      return changed;
    } finally {
      endChange();
    }
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    try {
      beginChange();

      boolean changed = false;

      for (E e : c) {
        changed = add(e) || changed;
      }

      return changed;
    } finally {
      endChange();
    }
  }

  @Override
  public void clear() {
    try {
      beginChange();

      for (E e : this) {
        if (doChange && !change.adding.remove(e)) {
          change.removing.add(e);
        }
      }

      SetDecorator.super.clear();
    } finally {
      endChange();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean remove(Object o) {
    try {
      beginChange();

      boolean changed = SetDecorator.super.remove(o);

      if (doChange && changed && !change.adding.remove(o)) {
        change.removing.add((E) o);
      }

      return changed;
    } finally {
      endChange();
    }
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    try {
      beginChange();

      boolean changed = false;

      for (Object o : c) {
        changed = remove(o) || changed;
      }

      return changed;
    } finally {
      endChange();
    }
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    try {
      beginChange();

      boolean changed = false;

      Iterator<E> i = iterator();
      while (i.hasNext()) {
        E e = i.next();

        if (!c.contains(e)) {
          changed = remove(e) || changed;
        }
      }

      return changed;
    } finally {
      endChange();
    }
  }

  @Override
  public HotObservable<Change<E>> changes() {
    return changeObservable;
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
  public Set<E> silent() {
    return component;
  }
}