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
 * This file is part of uk.co.saiman.properties.
 *
 * uk.co.saiman.properties is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.properties is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.properties.impl;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import uk.co.saiman.log.Log.Level;
import uk.co.saiman.properties.Key;
import uk.co.saiman.properties.PropertyLoader;
import uk.co.saiman.properties.PropertyLoaderException;
import uk.co.saiman.properties.PropertyResource;
import uk.co.saiman.properties.PropertyValueConversion;

/**
 * Delegate implementation object for proxy instances of property accessor
 * interfaces. This class deals with most method interception from the proxies
 * generated by {@link PropertyLoader}.
 * 
 * @author Elias N Vasylenko
 *
 * @param <A> the type of the delegating proxy
 */
public class PropertyAccessorDelegate<A> {
  private static final Set<Method> DIRECT_METHODS = getDirectMethods();

  private static Set<Method> getDirectMethods() {
    return unmodifiableSet(new HashSet<>(asList(Object.class.getDeclaredMethods())));
  }

  private final PropertyLoaderImpl loader;
  private final Class<A> source;
  private final PropertyResource propertyResource;

  private final A proxy;
  private final Map<Method, PropertyValueConversion<?>> valueConversions = new ConcurrentHashMap<>();

  /**
   * @param loader which created the delegate, to call back to
   * @param source the property accessor class and configuration
   */
  public PropertyAccessorDelegate(PropertyLoaderImpl loader, Class<A> source) {
    this.loader = loader;
    this.source = source;
    this.propertyResource = loader.getResourceLoader().loadResource(source);

    if (!source.isInterface()) {
      PropertyLoaderException e = new PropertyLoaderException(
          format("Property accessor class %s must be an interface", source));
      loader.getLog().log(Level.ERROR, e);
      throw e;
    }

    this.proxy = createProxy(source);

    for (Method method : source.getMethods()) {
      if (!DIRECT_METHODS.contains(method) && !method.isDefault()) {
        loadPropertyConversion(method);
      }
    }
  }

  private PropertyValueConversion<?> loadPropertyConversion(Method method) {
    AnnotatedType type = method.getAnnotatedReturnType();
    String key = getKey(source, method);

    return valueConversions
        .computeIfAbsent(
            method,
            s -> loader
                .getValueConverter()
                .getConversion(loader.getLocaleProvider(), propertyResource, type, key));
  }

  private String getKey(Class<A> source, Method method) {
    Key key = method.getAnnotation(Key.class);
    if (key == null) {
      key = source.getAnnotation(Key.class);
    }
    String keyString;
    if (key != null) {
      keyString = key.value();
    } else {
      keyString = Key.UNQUALIFIED_DOTTED;
    }

    Object[] substitution = new String[3];
    substitution[0] = source.getPackage().getName();
    substitution[1] = source.getSimpleName();
    substitution[2] = method.getName();

    return String.format(keyString, substitution);
  }

  @SuppressWarnings("unchecked")
  private A createProxy(Class<A> accessor) {
    ClassLoader classLoader = new PropertyAccessorClassLoader(accessor.getClassLoader());

    return (A) Proxy
        .newProxyInstance(
            classLoader,
            new Class<?>[] { accessor },
            (Object p, Method method, Object[] args) -> {
              if (DIRECT_METHODS.contains(method)) {
                return method.invoke(PropertyAccessorDelegate.this, args);
              }

              if (method.isDefault()) {
                return MethodHandles
                    .privateLookupIn(accessor, MethodHandles.lookup())
                    .unreflectSpecial(method, method.getDeclaringClass())
                    .bindTo(p)
                    .invokeWithArguments(args);
              }

              PropertyValueConversion<?> conversion = loadPropertyConversion(method);

              if (args == null) {
                args = new Object[] {};
              }

              return conversion.applyConversion(asList(args));
            });
  }

  class PropertyAccessorClassLoader extends ClassLoader {
    public PropertyAccessorClassLoader(ClassLoader classLoader) {
      super(classLoader);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
      if (name.equals(PropertyAccessorDelegate.class.getName())) {
        return PropertyAccessorDelegate.class;
      } else {
        return super.findClass(name);
      }
    }
  }

  public A getProxy() {
    return proxy;
  }
}
