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
package uk.co.saiman.utility;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This interface represents a gettable and settable property of a given type.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          The type of the property.
 */
/* @I */
public interface Property<T> {
  /**
   * Set the value of this property to the given value.
   * 
   * @param to
   *          The new value to set for this property.
   * @return The previous value of this property.
   */
  T set(/* @Mutable Property<T, R> this, */T to);

  /**
   * Get the current value of the property.
   * 
   * @return The current value.
   */
  /* @I */T get();

  /**
   * Create a property which defers its implementation to the given callbacks.
   * 
   * @param get
   *          the property retrieval callback
   * @param set
   *          the property assignment callback
   * @return a property over the given callbacks
   */
  static <T> Property<T> over(Supplier<T> get, Consumer<T> set) {
    return new Property<T>() {
      @Override
      public T set(T to) {
        T previous = get();
        set.accept(to);
        return previous;
      }

      @Override
      public T get() {
        return get.get();
      }

      @Override
      public String toString() {
        return Objects.toString(get());
      }
    };
  }
}