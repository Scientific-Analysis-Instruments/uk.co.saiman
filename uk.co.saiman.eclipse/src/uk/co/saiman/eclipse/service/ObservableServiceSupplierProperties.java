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
package uk.co.saiman.eclipse.service;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 * Text resource accessor for Eclipse OSGi utilities
 * 
 * @author Elias N Vasylenko
 */
public interface ObservableServiceSupplierProperties {
	/**
	 * @return invalid type was annotated with {@link ObservableService} for service
	 *         collection injection
	 */
	default String illegalInjectionTarget() {
		return illegalInjectionTarget(
				ObservableService.class,
				ObservableList.class,
				ObservableSet.class,
				ObservableValue.class);
	}

	/**
	 * @param observableService
	 *          the {@link ObservableService} class for service collection injection
	 * @param list
	 *          an observable list in service ranking order
	 * @param set
	 *          an observable set in service ranking order
	 * @param value
	 *          an observable value of the highest ranking service
	 * @return invalid type was annotated with {@link ObservableService} for service
	 *         collection injection
	 */
	@SuppressWarnings("rawtypes")
	String illegalInjectionTarget(
			Class<ObservableService> observableService,
			Class<ObservableList> list,
			Class<ObservableSet> set,
			Class<ObservableValue> value);

	/**
	 * @return an unexpected error occurred
	 */
	String unexpectedError();
}