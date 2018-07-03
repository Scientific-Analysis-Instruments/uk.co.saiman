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
 * This file is part of uk.co.saiman.webconsole.
 *
 * uk.co.saiman.webconsole is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webconsole is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webconsole;

import static uk.co.saiman.webconsole.SaiWebConsoleConstants.SAI_WEB_CONSOLE_WEB_MODULE_NAME;
import static uk.co.saiman.webconsole.SaiWebConsoleConstants.SAI_WEB_CONSOLE_WEB_MODULE_VERSION;
import static uk.co.saiman.webmodule.WebModuleConstants.DEFAULT_ENTRY_POINT;
import static uk.co.saiman.webmodule.WebModuleConstants.ESM;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.DEFAULT_RESOURCE_ROOT;

import uk.co.saiman.webmodule.ProvideWebModule;
import uk.co.saiman.webmodule.axios.RequireAxiosWebModule;
import uk.co.saiman.webmodule.prop.types.RequirePropTypesWebModule;
import uk.co.saiman.webmodule.react.RequireReactWebModule;
import uk.co.saiman.webmodule.react.dom.RequireReactDomWebModule;
import uk.co.saiman.webmodule.react.redux.RequireReactReduxWebModule;
import uk.co.saiman.webmodule.redux.thunk.RequireReduxThunkWebModule;

/**
 * Annotation to generate requirement to SAI web console utilities.
 * 
 * @author Elias N Vasylenko
 */
@RequirePropTypesWebModule
@RequireReactWebModule
@RequireAxiosWebModule
@RequireReduxThunkWebModule
@RequireReactDomWebModule
@RequireReactReduxWebModule
@SuppressWarnings("javadoc")
@ProvideWebModule(
    id = SAI_WEB_CONSOLE_WEB_MODULE_NAME,
    version = SAI_WEB_CONSOLE_WEB_MODULE_VERSION,
    resourceRoot = DEFAULT_RESOURCE_ROOT,
    entryPoint = DEFAULT_ENTRY_POINT,
    format = ESM)
public interface SaiWebConsoleConstants {
  final String SAI_WEB_CONSOLE_WEB_MODULE_NAME = "@saiman/webconsole";
  final String SAI_WEB_CONSOLE_WEB_MODULE_VERSION = "1.0.0";
}