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
 * This file is part of uk.co.saiman.text.
 *
 * uk.co.saiman.text is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.text is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.text.properties;

public class PropertyResourceBundleStrategy implements PropertyResourceStrategy<PropertyResourceBundleStrategy> {
	private static final PropertyResourceBundleStrategy INSTANCE = new PropertyResourceBundleStrategy();

	private PropertyResourceBundleStrategy() {}

	@Override
	public Class<PropertyResourceBundleStrategy> strategyClass() {
		return PropertyResourceBundleStrategy.class;
	}

	@Override
	public <T> PropertyResource getPropertyResourceBundle(Class<T> accessor, String resource) {
		return new PropertyResourceBundle(this, accessor, resource);
	}

	/**
	 * @return an instance of the strategy
	 */
	public static final PropertyResourceBundleStrategy getInstance() {
		return INSTANCE;
	}
}