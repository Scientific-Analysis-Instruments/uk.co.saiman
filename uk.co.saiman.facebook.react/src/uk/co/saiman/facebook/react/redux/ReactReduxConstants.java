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
 * This file is part of uk.co.saiman.facebook.react.
 *
 * uk.co.saiman.facebook.react is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.facebook.react is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.facebook.react.redux;

import static uk.co.saiman.facebook.react.redux.ReactReduxConstants.REACT_REDUX_WEB_RESOURCE_NAME;
import static uk.co.saiman.facebook.react.redux.ReactReduxConstants.REACT_REDUX_WEB_RESOURCE_VERSION;

import aQute.bnd.annotation.headers.ProvideCapability;
import osgi.enroute.namespace.WebResourceNamespace;
import uk.co.saiman.facebook.react.RequireReactWebResource;
import uk.co.saiman.redux.RequireReduxWebResource;

@RequireReactWebResource
@RequireReduxWebResource
@ProvideCapability(
		ns = WebResourceNamespace.NS,
		version = REACT_REDUX_WEB_RESOURCE_VERSION,
		value = ("root=/META-INF/resources/webjars/react-redux/" + REACT_REDUX_WEB_RESOURCE_VERSION)
				+ ";" + (WebResourceNamespace.NS + "=" + REACT_REDUX_WEB_RESOURCE_NAME))
public interface ReactReduxConstants {
	final String REACT_REDUX_WEB_RESOURCE_NAME = "/facebook/react-redux";
	final String REACT_REDUX_WEB_RESOURCE_VERSION = "5.0.5";
}
