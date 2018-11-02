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
 * This file is part of uk.co.saiman.bytes.conversion.
 *
 * uk.co.saiman.bytes.conversion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.bytes.conversion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.bytes.conversion.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;

class AnnotatedClassUse implements AnnotatedType {
  private final Class<?> type;

  public AnnotatedClassUse(Class<?> type) {
    this.type = type;
  }

  @Override
  public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
    return null;
  }

  @Override
  public Annotation[] getAnnotations() {
    return new Annotation[0];
  }

  @Override
  public Annotation[] getDeclaredAnnotations() {
    return new Annotation[0];
  }

  @Override
  public Type getType() {
    return type;
  }
}