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
 * This file is part of uk.co.saiman.msapex.instrument.acquisition.
 *
 * uk.co.saiman.msapex.instrument.acquisition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.instrument.acquisition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.instrument.acquisition;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toCollection;
import static uk.co.saiman.fx.FxUtilities.wrap;
import static uk.co.saiman.fx.FxmlLoadBuilder.buildWith;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.fx.core.di.LocalInstance;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.acquisition.AcquisitionProperties;
import uk.co.saiman.eclipse.Localize;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.msapex.chart.ContinuousFunctionChartController;
import uk.co.saiman.msapex.chart.ContinuousFunctionSeries;

/**
 * An Eclipse part for management and display of acquisition devices.
 * 
 * @author Elias N Vasylenko
 */
public class AcquisitionPart {
  static final String ID = "uk.co.saiman.msapex.instrument.acquisition.part";

  @Localize
  @Inject
  private AcquisitionProperties text;

  @FXML
  private Pane chartPane;
  @FXML
  private Label noSelectionLabel;

  private Set<AcquisitionDevice> currentSelection;
  private Map<AcquisitionDevice, ContinuousFunctionChartController> controllers = new HashMap<>();

  @Inject
  @LocalInstance
  private FXMLLoader loaderProvider;

  @Inject
  private Units units;

  @Inject
  synchronized void updateSelectedDevices(@Optional AcquisitionDeviceSelection devices) {
    if (chartPane == null)
      return;

    Set<AcquisitionDevice> newSelection = devices == null
        ? emptySet()
        : devices.getSelectedDevices().collect(toCollection(LinkedHashSet::new));

    for (AcquisitionDevice oldDevice : currentSelection) {
      if (!newSelection.remove(oldDevice)) {
        deselectAcquisitionDevice(oldDevice);
        currentSelection.remove(oldDevice);
      }
    }
    for (AcquisitionDevice newDevice : newSelection) {
      selectAcquisitionDevice(newDevice);
      currentSelection.add(newDevice);
    }
  }

  @PostConstruct
  void postConstruct(BorderPane container, @Optional AcquisitionDeviceSelection devices) {
    container.setCenter(buildWith(loaderProvider).controller(this).loadRoot());

    currentSelection = new HashSet<>();
    updateSelectedDevices(devices);
  }

  @FXML
  void initialize() {
    noSelectionLabel.textProperty().bind(wrap(text.noAcquisitionDevices()));
  }

  private void selectAcquisitionDevice(AcquisitionDevice acquisitionDevice) {
    noSelectionLabel.setVisible(false);

    /*
     * New chart controller for device
     */
    ContinuousFunctionChartController chartController = buildWith(loaderProvider)
        .controller(ContinuousFunctionChartController.class)
        .loadController();
    chartController.setTitle(acquisitionDevice.getName());
    controllers.put(acquisitionDevice, chartController);
    chartPane.getChildren().add(chartController.getRoot());
    HBox.setHgrow(chartController.getRoot(), Priority.ALWAYS);

    /*
     * Add latest data to chart controller
     */
    ContinuousFunctionSeries series = chartController.addSeries(acquisitionDevice.dataEvents());
  }

  private void deselectAcquisitionDevice(AcquisitionDevice acquisitionDevice) {
    ContinuousFunctionChartController controller = controllers.remove(acquisitionDevice);

    chartPane.getChildren().remove(controller.getRoot());

    noSelectionLabel.setVisible(chartPane.getChildren().isEmpty());
  }
}
