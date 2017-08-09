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
 * This file is part of uk.co.saiman.fx.
 *
 * uk.co.saiman.fx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.fx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.fx;

/**
 * A type of contribution for items in a {@link ModularTreeView}.
 * 
 * Loosely based on ideas from Eclipse CNF
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the type of the tree item data
 */
public interface TreeTextContribution<T> extends TreeContribution<T> {
	/**
	 * @param <U>
	 *          a capture of the exact type of the tree item data
	 * @param data
	 *          the data item we wish to retrieve the text for
	 * @return the primary text representation for the given data item, or null if
	 *         none is provided
	 */
	<U extends T> String getText(TreeItemData<U> data);

	/**
	 * @param <U>
	 *          a capture of the exact type of the tree item data
	 * @param data
	 *          the data item we wish to retrieve the text for
	 * @return any supplemental text information for the given data item, or null
	 *         if none is provided
	 */
	<U extends T> String getSupplementalText(TreeItemData<U> data);
}