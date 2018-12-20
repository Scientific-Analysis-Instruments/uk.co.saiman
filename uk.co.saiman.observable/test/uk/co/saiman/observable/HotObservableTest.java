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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import mockit.FullVerifications;
import mockit.Injectable;
import mockit.VerificationsInOrder;

@SuppressWarnings("javadoc")
public class HotObservableTest {
  @Injectable
  Observer<String> downstreamObserver;

  @Test
  public void observeTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
      }
    };
    new FullVerifications() {};
  }

  @Test
  public void isLiveAfterObserveTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);
    observable.assertLive();
  }

  @Test
  public void startWhenLiveTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);
    assertThrows(IllegalStateException.class, () -> observable.start());
  }

  @Test
  public void startWhenDeadTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);
    observable.complete();
    observable.start();
  }

  @Test
  public void isLiveAfterStartTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);
    observable.complete();
    observable.start();
    observable.assertLive();
  }

  @Test
  public void isLiveAfterInstantiationTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.assertLive();
  }

  @Test
  public void isDeadAfterCompleteTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.complete();
    observable.assertDead();
  }

  @Test
  public void isDeadAfterFailTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.fail(new Throwable());
    observable.assertDead();
  }

  @Test
  public void messageWhenDeadTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);
    observable.complete();
    assertThrows(IllegalStateException.class, () -> observable.next("fail"));
  }

  @Test
  public void completeWhenDeadTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);
    observable.complete();
    assertThrows(IllegalStateException.class, () -> observable.complete());
  }

  @Test
  public void failWhenDeadTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);
    observable.complete();
    assertThrows(IllegalStateException.class, () -> observable.fail(new Exception()));
  }

  @Test
  public void messageTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);
    observable.next("message");

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        downstreamObserver.onNext("message");
      }
    };
    new FullVerifications() {};
  }

  @Test
  public void completeTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);
    observable.complete();

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        downstreamObserver.onComplete();
      }
    };
    new FullVerifications() {};
  }

  @Test
  public void failTest() {
    Throwable t = new Throwable();

    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);
    observable.fail(t);

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        downstreamObserver.onFail(t);
      }
    };
    new FullVerifications() {};
  }

  @Test
  public void hasObserversTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe();
    assertThat(observable.hasObservers(), equalTo(true));
  }

  @Test
  public void hasNoObserversTest() {
    HotObservable<String> observable = new HotObservable<>();
    assertThat(observable.hasObservers(), equalTo(false));
  }

  @Test
  public void hasNoObserversAfterDiscardingOnlyObserverTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe().cancel();
    assertThat(observable.hasObservers(), equalTo(false));
  }

  @Test
  public void hasObserversAfterDiscardingOneOfTwoObserversTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe();
    observable.observe().cancel();
    assertThat(observable.hasObservers(), equalTo(true));
  }

  @Test
  public void failWithNullThrowableTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe();
    assertThrows(NullPointerException.class, () -> observable.fail(null));
  }

  @Test
  public void failWithNullMessageTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe();
    assertThrows(NullPointerException.class, () -> observable.next(null));
  }
}
