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
 * This file is part of uk.co.saiman.text.provider.
 *
 * uk.co.saiman.text.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.text.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.text.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

import uk.co.saiman.text.properties.PropertyConfiguration;
import uk.co.saiman.text.properties.PropertyResource;
import uk.co.saiman.text.properties.PropertyResourceBundle;
import uk.co.saiman.text.properties.PropertyResourceStrategy;
import uk.co.saiman.text.properties.ResourceBundleDescriptor;

@SuppressWarnings("javadoc")
public class OsgiPropertyResourceStrategy implements PropertyResourceStrategy<OsgiPropertyResourceStrategy> {
	private static final String DEFAULT_OSGI_LOCALIZATION_LOCATION = "OSGI-INF.l10n.bundle";
	private static final String OSGI_LOCALIZATION_HEADER = "Bundle-Localization";

	private Bundle usingBundle;

	public OsgiPropertyResourceStrategy(Bundle usingBundle) {
		this.usingBundle = usingBundle;
	}

	@Override
	public Class<OsgiPropertyResourceStrategy> strategyClass() {
		return OsgiPropertyResourceStrategy.class;
	}

	@Override
	public <T> PropertyResource getPropertyResourceBundle(Class<T> accessor, String resource) {
		return new PropertyResourceBundle(this, accessor, resource) {
			@Override
			protected <U> List<ResourceBundleDescriptor> getResources(Class<U> accessor, String resource) {
				List<ResourceBundleDescriptor> resources = new ArrayList<>();

				boolean unspecifiedResource = resource.equals(PropertyConfiguration.UNSPECIFIED_RESOURCE);

				if (unspecifiedResource) {
					resource = removePropertiesPostfix(accessor.getName());
				}

				addResources(resources, resource, unspecifiedResource, usingBundle);

				Bundle accessorBundle = FrameworkUtil.getBundle(accessor);
				if (!Objects.equals(accessorBundle, usingBundle))
					addResources(resources, resource, unspecifiedResource, accessorBundle);

				return resources;
			}

			private void addResources(
					List<ResourceBundleDescriptor> resources,
					String resource,
					boolean unspecifiedResource,
					Bundle bundle) {
				if (bundle != null) {
					ClassLoader classLoader = bundle.adapt(BundleWiring.class).getClassLoader();

					resources.add(new ResourceBundleDescriptor(classLoader, resource));
					if (unspecifiedResource) {
						resources.add(getOsgiResourceDescriptor(bundle, classLoader));
					}
				}
			}

			private ResourceBundleDescriptor getOsgiResourceDescriptor(Bundle bundle, ClassLoader classLoader) {
				String location = bundle.getHeaders().get(OSGI_LOCALIZATION_HEADER);
				if (location == null)
					location = DEFAULT_OSGI_LOCALIZATION_LOCATION;

				return new ResourceBundleDescriptor(classLoader, location);
			}
		};
	}
}
