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

import java.util.function.Supplier;

public class PrefixingObserver<T> extends PassthroughObserver<T, T> {
  private final Supplier<T> prefix;

  PrefixingObserver(Supplier<T> prefix, Observer<? super T> downstreamObserver) {
    super(downstreamObserver);
    this.prefix = prefix;
  }

  @Override
  public void onObserve(Observation observation) {
    super.onObserve(observation);

    try {
      getDownstreamObserver().onNext(prefix.get());
    } catch (Exception e) {
      getDownstreamObserver().onFail(e);
    }
  }

  @Override
  public void onNext(T message) {
    getDownstreamObserver().onNext(message);
  }
}