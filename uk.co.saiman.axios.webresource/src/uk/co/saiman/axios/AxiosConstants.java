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
 * This file is part of uk.co.saiman.requirejs.webresource.
 *
 * uk.co.saiman.requirejs.webresource is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.requirejs.webresource is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.axios;

import static uk.co.saiman.axios.AxiosConstants.AXIOS_WEB_RESOURCE_NAME;
import static uk.co.saiman.axios.AxiosConstants.AXIOS_WEB_RESOURCE_VERSION;

import aQute.bnd.annotation.headers.ProvideCapability;
import osgi.enroute.namespace.WebResourceNamespace;

@ProvideCapability(
    ns = WebResourceNamespace.NS,
    version = AXIOS_WEB_RESOURCE_VERSION,
    value = ("root=/META-INF/resources/webjars/axios/" + AXIOS_WEB_RESOURCE_VERSION) + ";"
        + (WebResourceNamespace.NS + "=" + AXIOS_WEB_RESOURCE_NAME))
public interface AxiosConstants {
  final String AXIOS_WEB_RESOURCE_NAME = "/axios/axios";
  final String AXIOS_WEB_RESOURCE_VERSION = "0.16.1";
}
