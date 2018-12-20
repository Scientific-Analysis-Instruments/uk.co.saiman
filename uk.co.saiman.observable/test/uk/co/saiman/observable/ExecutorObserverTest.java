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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.Executor;

import org.junit.jupiter.api.Test;

import mockit.Expectations;
import mockit.FullVerifications;
import mockit.Injectable;
import mockit.Mocked;
import mockit.VerificationsInOrder;

@SuppressWarnings("javadoc")
public class ExecutorObserverTest {
  interface MockObserver<T> extends Observer<T> {}

  interface MockObservation extends Observation {}

  @Injectable
  MockObservation upstreamObservation;

  @Injectable
  MockObserver<String> downstreamObserver;

  @Test
  public void messageEventOnInlineExecutorTest() {
    SafeObserver<String> test = new ExecutorObserver<>(downstreamObserver, r -> r.run());

    test.onObserve(upstreamObservation);
    test.getObservation().requestNext();
    test.onNext("message");

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        downstreamObserver.onNext("message");
      }
    };
  }

  @Test
  public void messageEventOnDiscardingExecutorTest() {
    Observer<String> test = new ExecutorObserver<>(downstreamObserver, r -> {});

    test.onObserve(upstreamObservation);
    test.onNext("message");

    new FullVerifications() {};
  }

  @Test
  public void messageEventOnMockedExecutorTest(@Mocked Executor executor) {
    Observer<String> test = new ExecutorObserver<>(downstreamObserver, executor);

    test.onObserve(upstreamObservation);
    test.onNext("message");

    new VerificationsInOrder() {
      {
        executor.execute((Runnable) any);
        executor.execute((Runnable) any);
      }
    };
    new FullVerifications() {};
  }

  @Test
  public void throwFromOnObserveTest() {
    Throwable throwable = new Exception();

    new Expectations() {
      {
        downstreamObserver.onObserve((Observation) any);
        result = throwable;
      }
    };

    Observer<String> test = new ExecutorObserver<>(downstreamObserver, r -> r.run());
    test.onObserve(upstreamObservation);

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        downstreamObserver.onFail(throwable);
      }
    };
  }

  @Test
  public void throwFromOnNextTest() {
    Throwable throwable = new Exception();

    new Expectations() {
      {
        downstreamObserver.onNext(anyString);
        result = throwable;
      }
    };

    SafeObserver<String> test = new ExecutorObserver<>(downstreamObserver, r -> r.run());

    test.onObserve(upstreamObservation);
    test.getObservation().requestNext();
    test.onNext("message");

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        downstreamObserver.onNext("message");
        downstreamObserver.onFail(throwable);
      }
    };
  }

  @Test
  public void nullExecutorTest() {
    assertThrows(
        NullPointerException.class,
        () -> new ExecutorObserver<>(downstreamObserver, null));
  }
}
