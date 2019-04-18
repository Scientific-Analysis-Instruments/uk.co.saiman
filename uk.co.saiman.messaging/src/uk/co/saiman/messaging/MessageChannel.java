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
 * This file is part of uk.co.saiman.messaging.
 *
 * uk.co.saiman.messaging is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.messaging is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.messaging;

import java.io.IOException;
import java.nio.ByteBuffer;

import uk.co.saiman.observable.Observable;

public interface MessageChannel extends DataChannel, MessageSender, MessageReceiver {
  static MessageChannel over(MessageSender sender, MessageReceiver receiver) {
    return new MessageChannel() {
      @Override
      public void sendMessage(ByteBuffer message) throws IOException {
        sender.sendMessage(message);
      }

      @Override
      public Observable<ByteBuffer> receiveMessages() {
        return receiver.receiveMessages();
      }

      @Override
      public DataBuffer openDataBuffer(int size) throws IOException {
        return receiver.openDataBuffer(size);
      }

      @Override
      public DataChannel packeting(int size) {
        return DataChannel.over(sender, receiver.packeting(size));
      }
    };
  }
}