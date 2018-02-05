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
 * This file is part of uk.co.saiman.experiment.spectrum.
 *
 * uk.co.saiman.experiment.spectrum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.spectrum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.spectrum;

import static uk.co.saiman.data.function.processing.DataProcessor.identity;
import static uk.co.saiman.experiment.processing.ProcessorState.PROCESSING_KEY;
import static uk.co.saiman.text.properties.PropertyLoader.getDefaultProperties;

import java.util.List;
import java.util.Random;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.data.function.ContinuousFunction;
import uk.co.saiman.data.function.SampledContinuousFunction;
import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.data.spectrum.Spectrum;
import uk.co.saiman.data.spectrum.SpectrumCalibration;
import uk.co.saiman.experiment.ConfigurationContext;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.ProcessingType;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.experiment.processing.ProcessorService;
import uk.co.saiman.experiment.processing.ProcessorState;

/**
 * Configure the sample position to perform an experiment at. Typically most
 * other experiment nodes will be descendant to a sample experiment node, such
 * that they operate on the configured sample.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the type of sample configuration for the instrument
 */
@Component(service = ExperimentType.class)
public class SpectrumProcessingExperimentType
    implements ProcessingType<SpectrumResultConfiguration, Spectrum, Spectrum> {
  @Reference
  ProcessorService processors;

  private SpectrumProperties properties;

  public SpectrumProcessingExperimentType() {
    this(getDefaultProperties(SpectrumProperties.class));
  }

  /*
   * TODO this parameter really should be injected by DS. Hurry up OSGi r7 to make
   * this possible ...
   */
  public SpectrumProcessingExperimentType(SpectrumProperties properties) {
    this.properties = properties;
  }

  protected void setProperties(SpectrumProperties properties) {
    this.properties = properties;
  }

  protected SpectrumProperties getProperties() {
    return properties;
  }

  @Override
  public String getName() {
    return properties.spectrumProcessingExperimentName().toString();
  }

  @Override
  public String getId() {
    return getClass().getName();
  }

  @Override
  public SpectrumResultConfiguration createState(
      ConfigurationContext<SpectrumResultConfiguration> context) {
    return new SpectrumResultConfiguration() {
      private final List<ProcessorState> processors = createProcessorList(context.persistedState());
      private String name = context.getId(() -> "test-" + new Random().nextInt(Integer.MAX_VALUE));

      @Override
      public void setSpectrumName(String name) {
        this.name = name;
        context.setId(name);
      }

      @Override
      public String getSpectrumName() {
        return name;
      }

      @Override
      public List<ProcessorState> getProcessing() {
        return processors;
      }
    };
  }

  @Override
  public Spectrum process(SpectrumResultConfiguration state, Spectrum input) {
    DataProcessor processor = state
        .getProcessing()
        .stream()
        .map(ProcessorState::getProcessor)
        .reduce(identity(), DataProcessor::andThen);

    SampledContinuousFunction<Mass, Dimensionless> massData = processor
        .process(input.getMassData());

    return new Spectrum() {
      @Override
      public ContinuousFunction<Time, Dimensionless> getTimeData() {
        return input.getTimeData();
      }

      @Override
      public DataProcessor getProcessing() {
        return input.getProcessing().andThen(processor);
      }

      @Override
      public SampledContinuousFunction<Mass, Dimensionless> getMassData() {
        return massData;
      }

      @Override
      public SpectrumCalibration getCalibration() {
        return input.getCalibration();
      }
    };
  }

  protected List<ProcessorState> createProcessorList(PersistedState persistedState) {
    return persistedState
        .getMapList(PROCESSING_KEY)
        .map(processors::loadProcessorState, ProcessorState::getPersistedState);
  }
}