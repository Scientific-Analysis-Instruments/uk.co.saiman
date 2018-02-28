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
 * This file is part of uk.co.saiman.msapex.annotations.
 *
 * uk.co.saiman.msapex.annotations is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.annotations is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.annotations;

import static uk.co.saiman.measurement.fx.QuantityBindings.toUnit;

import javax.measure.Quantity;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public class YAnnotation<X extends Quantity<X>, Y extends Quantity<Y>> extends Annotation<X, Y> {
  private final Property<Quantity<Y>> measurementY;

  public YAnnotation() {
    measurementY = new SimpleObjectProperty<>();

    layoutYProperty().bind(measurementToLayoutY(toUnit(unitYProperty()).convert(measurementY)));
  }

  public Property<Quantity<Y>> measurementYProperty() {
    return measurementY;
  }

  public Quantity<Y> getMeasurementY() {
    return measurementY.getValue();
  }

  public void setMeasurementY(Quantity<Y> value) {
    measurementY.setValue(value);
  }
}
