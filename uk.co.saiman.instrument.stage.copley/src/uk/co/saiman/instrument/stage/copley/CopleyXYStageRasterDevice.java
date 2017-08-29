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
 * This file is part of uk.co.saiman.instrument.stage.copley.
 *
 * uk.co.saiman.instrument.stage.copley is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.stage.copley is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.stage.copley;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.instrument.raster.RasterDevice;
import uk.co.saiman.instrument.raster.RasterPattern;
import uk.co.saiman.instrument.raster.RasterPosition;
import uk.co.saiman.instrument.stage.StageDimension;
import uk.co.saiman.instrument.stage.XYStageDevice;
import uk.co.saiman.instrument.stage.copley.CopleyXYStageRasterDevice.CopleyXYStageRasterConfiguration;
import uk.co.saiman.mathematics.Interval;
import uk.co.saiman.observable.Observable;

/**
 * A Copley motor XY stage which uses stage position offset as a rastering
 * mechanism.
 * <p>
 * The start position of the raster is taken from the
 * {@link StageDimension#getRequestedPosition() requested position} at the time
 * the raster begins. If a position is
 * {@link StageDimension#requestPosition(javax.measure.Quantity) manually
 * requested} during a raster operation, the operation fails and the
 * {@link #rasterPositionEvents() raster position observable} sends a terminal
 * failure event to its observers.
 * 
 * @author Elias N Vasylenko
 */
@Designate(ocd = CopleyXYStageRasterConfiguration.class, factory = true)
@Component(
    configurationPid = CopleyXYStageRasterDevice.CONFIGURATION_PID,
    configurationPolicy = REQUIRE)
public class CopleyXYStageRasterDevice extends CopleyStageDevice
    implements RasterDevice, XYStageDevice {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "Copley XY Stage Raster Configuration",
      description = "An implementation of a rastering XY stage device interface based on copley motor hardware")
  public @interface CopleyXYStageRasterConfiguration {

  }

  static final String CONFIGURATION_PID = "uk.co.saiman.instrument.stage.copley.xy.rastering";

  private StageDimension<Length> xAxis;
  private StageDimension<Length> yAxis;

  private RasterPattern rasterPattern;
  private RasterPosition rasterPosition;
  private Quantity<Length> rasterResolutionX;
  private Quantity<Length> rasterResolutionY;

  @Activate
  void activate(CopleyXYStageRasterConfiguration configuration) {
    activate();
    xAxis = new RasteringAxis(new CopleyLinearDimension(getUnits(), 0, getController()));
    yAxis = new RasteringAxis(new CopleyLinearDimension(getUnits(), 1, getController()));
  }

  public void setRasterPattern(RasterPattern mode) {
    rasterPattern = mode;
  }

  public void setRasterResolution(Quantity<Length> x, Quantity<Length> y) {
    this.rasterResolutionX = x;
    this.rasterResolutionY = y;
  }

  public Quantity<Length> getRasterResolutionX() {
    return rasterResolutionX;
  }

  public Quantity<Length> getRasterResolutionY() {
    return rasterResolutionY;
  }

  @Override
  public int getRasterWidth() {
    // TODO Auto-generated method stub
    return 1;
  }

  @Override
  public int getRasterHeight() {
    // TODO Auto-generated method stub
    return 1;
  }

  @Override
  public boolean isRasterOperating() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public RasterPosition getRasterPosition() {
    return rasterPosition;
  }

  @Override
  public Observable<RasterPosition> rasterPositionEvents() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public StageDimension<Length> getXAxis() {
    return xAxis;
  }

  @Override
  public StageDimension<Length> getYAxis() {
    return yAxis;
  }

  class RasteringAxis implements StageDimension<Length> {
    private final StageDimension<Length> component;

    public RasteringAxis(StageDimension<Length> component) {
      this.component = component;
    }

    @Override
    public Unit<Length> getUnit() {
      return component.getUnit();
    }

    @Override
    public Interval<Quantity<Length>> getBounds() {
      return component.getBounds();
    }

    @Override
    public void requestPosition(Quantity<Length> offset) {
      // TODO Auto-generated method stub

    }

    @Override
    public Quantity<Length> getRequestedPosition() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public Quantity<Length> getActualPosition() {
      // TODO Auto-generated method stub
      return null;
    }
  }
}
