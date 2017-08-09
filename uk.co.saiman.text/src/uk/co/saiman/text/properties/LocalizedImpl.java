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

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uk.co.saiman.observable.ObservablePropertyImpl;
import uk.co.saiman.observable.ObservableValue;
import uk.co.saiman.observable.Observer;

/*
 * Implementation of localized property
 */
class LocalizedImpl<A> extends ObservablePropertyImpl<Object>
    implements Localized<Object>, Observer<Locale> {
  private final PropertyAccessorDelegate<A> propertyAccessorDelegate;

  private final PropertyAccessorConfiguration<A> source;
  private final String key;
  private final AnnotatedType propertyType;
  private final List<Object> arguments;
  private final Map<Locale, Object> cache;

  public LocalizedImpl(
      PropertyAccessorDelegate<A> propertyAccessorDelegate,
      PropertyAccessorConfiguration<A> source,
      String key,
      AnnotatedType propertyType,
      List<?> arguments) {
    super(new IllegalStateException("Locale has failed to initialize"));

    this.propertyAccessorDelegate = propertyAccessorDelegate;

    this.source = source;
    this.key = key;
    this.propertyType = getElementType(propertyType);
    this.arguments = new ArrayList<>(arguments);
    this.cache = new ConcurrentHashMap<>();

    locale().weakReference().observe(this);
    updateText(locale().get());
  }

  private AnnotatedType getElementType(AnnotatedType propertyType) {
    return ((AnnotatedParameterizedType) propertyType).getAnnotatedActualTypeArguments()[0];
  }

  private synchronized void updateText(Locale locale) {
    set(get(locale));
  }

  @Override
  public String toString() {
    return get().toString();
  }

  @Override
  public Object get(Locale locale) {
    return cache.computeIfAbsent(locale, l -> {
      return this.propertyAccessorDelegate
          .parseValueString(source, propertyType, key, locale)
          .apply(arguments);
    });
  }

  @Override
  public void onNext(Locale locale) {
    updateText(locale);
  }

  @Override
  public ObservableValue<Locale> locale() {
    return this.propertyAccessorDelegate.getLoader().locale();
  }
}