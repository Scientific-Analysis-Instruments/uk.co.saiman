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
 * This file is part of uk.co.saiman.eclipse.fx.
 *
 * uk.co.saiman.eclipse.fx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.fx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.ui.fx.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.ui.ListItems;

public final class ListItemsImpl implements ListItems {
  private final Map<String, Cell> itemModels;
  private final Map<Cell, ItemList<?>> itemData;

  public ListItemsImpl(Collection<Cell> items) {
    this.itemModels = items
        .stream()
        .collect(Collectors.toMap(Cell::getElementId, Function.identity()));
    this.itemData = new HashMap<>();
  }

  private Cell readyModel(String id) {
    if (!itemModels.containsKey(id)) {
      throw new IllegalArgumentException("Child does not exist " + id);
    }
    Cell model = itemModels.get(id);
    itemData.remove(model);
    return model;
  }

  @Override
  public <T> void addItem(String id, T child) {
    Cell model = readyModel(id);
    itemData.put(model, new ItemList<>(model, child));
  }

  @Override
  public <T> void addItem(String id, T child, Consumer<? super T> update) {
    Cell model = readyModel(id);
    itemData.put(model, new ItemList<>(model, child, update));
  }

  @Override
  public <T> void addItems(String id, List<? extends T> children) {
    Cell model = readyModel(id);
    itemData.put(model, new ItemList<>(model, children));
  }

  @Override
  public <T> void addItems(
      String id,
      List<? extends T> children,
      Consumer<? super List<? extends T>> update) {
    Cell model = readyModel(id);
    itemData.put(model, new ItemList<>(model, children, update));
  }

  public Stream<ItemList<?>> getItems() {
    return itemData.values().stream();
  }
}