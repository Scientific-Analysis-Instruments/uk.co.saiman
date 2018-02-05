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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Locale;

import org.junit.Test;

import uk.co.saiman.property.IdentityProperty;
import uk.co.saiman.text.properties.LocaleManager;
import uk.co.saiman.text.properties.Localized;
import uk.co.saiman.text.properties.PropertyLoader;

@SuppressWarnings("javadoc")
public class LocalizedPropertiesTest {
  public LocalizerTestProperties text(LocaleManager manager) {
    return PropertyLoader.getPropertyLoader(manager).getProperties(LocalizerTestProperties.class);
  }

  private LocaleManager manager() {
    return LocaleManager.getManager(Locale.ENGLISH);
  }

  private LocaleManager manager(Locale locale) {
    return LocaleManager.getManager(locale);
  }

  @Test
  public void languageTest() {
    LocalizerTestProperties text = text(manager(Locale.FRENCH));

    assertEquals("French simple property value", text.simpleLocalized().toString());
  }

  @Test
  public void languageDefaultTest() {
    LocalizerTestProperties text = text(manager(Locale.FRENCH));

    assertEquals("another simple property value", text.anotherSimpleLocalized().toString());
  }

  @Test
  public void languageChangeTest() {
    LocaleManager manager = manager();

    LocalizerTestProperties text = text(manager);

    assertEquals("simple property value", text.simpleLocalized().toString());

    manager.setLocale(Locale.FRENCH);

    assertEquals("French simple property value", text.simpleLocalized().toString());
  }

  @Test
  public void localeChangeStringEventTest() {
    LocaleManager manager = manager();

    Localized<String> string = text(manager).simpleLocalized();

    IdentityProperty<String> result = new IdentityProperty<>();
    string.observe(t -> {
      result.set(t);
    });

    manager.setLocale(Locale.FRENCH);

    assertNotNull(result);
    assertEquals("French simple property value", string.get());
    assertEquals("French simple property value", result.get());
  }

  @Test
  public void localeChangeStringNoEventTest() {
    LocaleManager manager = manager();

    Localized<String> string = text(manager).anotherSimpleLocalized();

    IdentityProperty<String> result = new IdentityProperty<>();
    string.observe(t -> {
      result.set(t);
    });

    manager.setLocale(Locale.FRENCH);

    assertEquals("another simple property value", result.get());
  }

  @Test
  public void unlocalizedStringTest() {
    LocaleManager manager = manager(Locale.FRENCH);

    String string = text(manager).nonLocalized();

    assertEquals("This is not localized", string);
  }

  @Test
  public void unavailableInRootLocaleStringTest() {
    LocaleManager manager = manager(Locale.FRENCH);

    assertEquals("?localizer.test/non.localized.missing?[]", text(manager).nonLocalizedMissing());
  }
}
