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

public class RequestCount {
  private long requestCount;

  public long getCount() {
    return requestCount < 0 ? Long.MAX_VALUE : requestCount;
  }

  public synchronized void request(long count) {
    if (count < 0)
      throw new IllegalArgumentException("Must request a positive number of messages " + count);

    else if (count == Long.MAX_VALUE)
      requestCount = -1;

    else if (requestCount >= 0)
      requestCount += count;
  }

  public synchronized boolean isFulfilled() {
    return requestCount == 0;
  }

  public synchronized void fulfil() {
    if (!tryFulfil())
      throw new IllegalStateException("No request to fulfil");
  }

  public synchronized boolean tryFulfil() {
    if (requestCount > 0) {
      requestCount--;
      return true;

    } else if (requestCount < 0)
      return true;

    else
      return false;
  }
}
