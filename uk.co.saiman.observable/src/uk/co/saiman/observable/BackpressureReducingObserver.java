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

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class BackpressureReducingObserver<T, M> extends PassthroughObserver<T, M> {
  private final Supplier<? extends M> identity;
  private final Function<? super T, ? extends M> initial;
  private final BiFunction<? super M, ? super T, ? extends M> accumulator;

  private M current;
  private boolean complete;
  private final RequestCount outstandingRequests = new RequestCount();

  public BackpressureReducingObserver(
      Observer<? super M> downstreamObserver,
      Supplier<? extends M> identity,
      BiFunction<? super M, ? super T, ? extends M> accumulator) {
    super(downstreamObserver);

    this.accumulator = requireNonNull(accumulator);
    this.identity = requireNonNull(identity);
    this.initial = m -> accumulator.apply(identity.get(), m);
  }

  public BackpressureReducingObserver(
      Observer<? super M> downstreamObserver,
      Function<? super T, ? extends M> initial,
      BiFunction<? super M, ? super T, ? extends M> accumulator) {
    super(downstreamObserver);

    this.accumulator = requireNonNull(accumulator);
    this.identity = null;
    this.initial = requireNonNull(initial);
  }

  @Override
  public void onObserve(Observation observation) {
    super.onObserve(new Observation() {
      @Override
      public void requestNext() {
        request(1);
      }

      @Override
      public void request(long count) {
        for (int i = 0; i < count; i++) {
          synchronized (outstandingRequests) {
            if (current == null) {
              if (complete) {
                getDownstreamObserver().onComplete();
                break;
              } else if (identity == null) {
                outstandingRequests.request(count);
                break;
              } else {
                current = identity.get();
              }
            }
            outstandingRequests.fulfil();
            getDownstreamObserver().onNext(current);
            current = null;
          }
        }
      }

      @Override
      public boolean isRequestUnbounded() {
        return Observation.super.isRequestUnbounded();
      }

      @Override
      public void cancel() {
        observation.cancel();
      }

      @Override
      public long getPendingRequestCount() {
        return outstandingRequests.getCount();
      }
    });

    observation.requestUnbounded();
  }

  @Override
  public void onNext(T message) {
    synchronized (outstandingRequests) {
      if (current == null)
        current = initial.apply(message);
      else
        current = accumulator.apply(current, message);

      if (!outstandingRequests.isFulfilled()) {
        outstandingRequests.fulfil();
        getDownstreamObserver().onNext(current);
        current = null;
      }
    }
  }

  @Override
  public void onComplete() {
    synchronized (outstandingRequests) {
      if (current == null) {
        getDownstreamObserver().onComplete();
      }
      complete = true;
    }
  }

  @Override
  public void onFail(Throwable t) {
    getDownstreamObserver().onFail(t);
  }
}
