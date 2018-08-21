/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.state;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static uk.co.saiman.experiment.state.StateKind.LIST;
import static uk.co.saiman.experiment.state.StateKind.MAP;
import static uk.co.saiman.experiment.state.StateKind.PROPERTY;
import static uk.co.saiman.experiment.state.StateList.toStateList;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public interface Accessor<T, U extends State> {
  String id();

  T read(U data);

  U write(T value);

  StateKind getKind();

  <V> Accessor<V, U> map(
      Function<? super T, ? extends V> read,
      Function<? super V, ? extends T> write);

  interface PropertyAccessor<T> extends Accessor<T, StateProperty> {
    @Override
    default StateKind getKind() {
      return PROPERTY;
    }

    @Override
    default <V> PropertyAccessor<V> map(
        Function<? super T, ? extends V> read,
        Function<? super V, ? extends T> write) {
      return propertyAccessor(id(), s -> read.apply(read(s)), s -> write(write.apply(s)));
    }
  }

  interface MapAccessor<T> extends Accessor<T, StateMap> {
    @Override
    default StateKind getKind() {
      return MAP;
    }

    @Override
    default <V> MapAccessor<V> map(
        Function<? super T, ? extends V> read,
        Function<? super V, ? extends T> write) {
      return mapAccessor(id(), s -> read.apply(read(s)), s -> write(write.apply(s)));
    }
  }

  interface ListAccessor<T> extends Accessor<T, StateList> {
    @Override
    default StateKind getKind() {
      return LIST;
    }

    @Override
    default <V> ListAccessor<V> map(
        Function<? super T, ? extends V> read,
        Function<? super V, ? extends T> write) {
      return listAccessor(id(), s -> read.apply(read(s)), s -> write(write.apply(s)));
    }
  }

  static <T> PropertyAccessor<T> propertyAccessor(
      String name,
      Function<? super StateProperty, ? extends T> read,
      Function<? super T, ? extends StateProperty> write) {
    return new PropertyAccessor<T>() {
      @Override
      public String id() {
        return name;
      }

      @Override
      public T read(StateProperty data) {
        return read.apply(data);
      }

      @Override
      public StateProperty write(T value) {
        return write.apply(value);
      }
    };
  }

  static <T> MapAccessor<T> mapAccessor(
      String name,
      Function<? super StateMap, ? extends T> read,
      Function<? super T, ? extends StateMap> write) {
    return new MapAccessor<T>() {
      @Override
      public String id() {
        return name;
      }

      @Override
      public T read(StateMap data) {
        return read.apply(data);
      }

      @Override
      public StateMap write(T value) {
        return write.apply(value);
      }
    };
  }

  static <T> ListAccessor<T> listAccessor(
      String name,
      Function<? super StateList, ? extends T> read,
      Function<? super T, ? extends StateList> write) {
    return new ListAccessor<T>() {
      @Override
      public String id() {
        return name;
      }

      @Override
      public T read(StateList data) {
        return read.apply(data);
      }

      @Override
      public StateList write(T value) {
        return write.apply(value);
      }
    };
  }

  static PropertyAccessor<String> stringAccessor(String name) {
    return propertyAccessor(name, StateProperty::getValue, StateProperty::stateProperty);
  }

  static PropertyAccessor<Integer> intAccessor(String name) {
    return stringAccessor(name).map(Integer::parseInt, Objects::toString);
  }

  static PropertyAccessor<Long> longAccessor(String name) {
    return stringAccessor(name).map(Long::parseLong, Objects::toString);
  }

  static PropertyAccessor<Float> floatAccessor(String name) {
    return stringAccessor(name).map(Float::parseFloat, Objects::toString);
  }

  static PropertyAccessor<Double> doubleAccessor(String name) {
    return stringAccessor(name).map(Double::parseDouble, Objects::toString);
  }

  static PropertyAccessor<Boolean> booleanAccessor(String name) {
    return stringAccessor(name).map(Boolean::parseBoolean, Objects::toString);
  }

  @SuppressWarnings("unchecked")
  default ListAccessor<Stream<T>> toStreamAccessor() {
    return listAccessor(
        id(),
        s -> s.stream().map(e -> read((U) e)),
        a -> a.map(e -> write(e)).collect(toStateList()));
  }

  default ListAccessor<T[]> toArrayAccessor(IntFunction<T[]> newArray) {
    return toStreamAccessor().map(s -> s.toArray(newArray), Stream::of);
  }

  default ListAccessor<List<T>> toListAccessor() {
    return toStreamAccessor().map(s -> s.collect(toList()), List::stream);
  }

  default ListAccessor<Set<T>> toSetAccessor() {
    return toStreamAccessor().map(s -> s.collect(toSet()), Set::stream);
  }
}