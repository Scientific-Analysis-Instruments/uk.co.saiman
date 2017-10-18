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
 * This file is part of uk.co.saiman.eclipse.
 *
 * uk.co.saiman.eclipse is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.treeview;

import static java.util.Collections.emptyMap;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Creatable;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * A tree cell contribution intended to be supplied via {@link TreeContribution}
 * so as to be injected according to an eclipse context.
 * <p>
 * This contribution registers an E4 command to the cell, which can be activated
 * via double click or the enter key.
 * 
 * @author Elias N Vasylenko
 */
@Creatable
public class CommandContributor {
  @Inject
  EHandlerService handlerService;

  @Inject
  ECommandService commandService;

  private String commandId;
  private ParameterizedCommand command;

  protected ParameterizedCommand createCommand(String commandId) {
    return commandService.createCommand(commandId, emptyMap());
  }

  public Node configureCell(String commandId, Node content) {
    ParameterizedCommand command;
    synchronized (this) {
      if (this.commandId != commandId) {
        command = createCommand(commandId);

        this.commandId = commandId;
        this.command = command;
      } else {
        command = this.command;
      }
    }

    content.addEventHandler(MouseEvent.ANY, event -> {
      if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY)) {
        event.consume();

        if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
          executeCommand(command);
        }
      }
    });

    content.addEventHandler(KeyEvent.ANY, event -> {
      if (event.getCode() == KeyCode.ENTER) {
        event.consume();

        if (event.getEventType().equals(KeyEvent.KEY_PRESSED)) {
          executeCommand(command);
        }
      }
    });

    return content;
  }

  protected void executeCommand(ParameterizedCommand command) {
    handlerService.executeHandler(command);
  }
}