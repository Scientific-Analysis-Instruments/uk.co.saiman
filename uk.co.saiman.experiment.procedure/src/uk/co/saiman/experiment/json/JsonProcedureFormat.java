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
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.json;

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.collection.StreamUtilities.throwingMerger;
import static uk.co.saiman.data.format.MediaType.APPLICATION_TYPE;
import static uk.co.saiman.data.format.RegistrationTree.VENDOR;
import static uk.co.saiman.state.Accessor.mapAccessor;
import static uk.co.saiman.state.Accessor.stringAccessor;

import java.util.List;
import java.util.stream.Stream;

import uk.co.saiman.data.format.MediaType;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.format.TextFormat;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.experiment.path.ProductIndex;
import uk.co.saiman.experiment.path.ProductPath;
import uk.co.saiman.experiment.procedure.Conductor;
import uk.co.saiman.experiment.procedure.ConductorService;
import uk.co.saiman.experiment.procedure.Instruction;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.variables.VariableService;
import uk.co.saiman.experiment.variables.Variables;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.State;
import uk.co.saiman.state.StateList;
import uk.co.saiman.state.StateMap;
import uk.co.saiman.state.json.JsonStateMapFormat;

/**
 * A format for serializing and deserializing experiment procedures.
 * 
 * Because
 * 
 * @author Elias N Vasylenko
 *
 */
public class JsonProcedureFormat implements TextFormat<Procedure> {
  public static final int VERSION = 1;

  public static final String FILE_EXTENSION = "jep"; // json experiment procedure
  public static final MediaType MEDIA_TYPE = new MediaType(
      APPLICATION_TYPE,
      "saiman.procedure.v" + VERSION,
      VENDOR).withSuffix("json");

  private static final MapIndex<String> ID = new MapIndex<>("id", stringAccessor());
  private static final String CONDUCTOR = "conductor";
  private static final String VARIABLES = "variables";
  private static final String CHILDREN = "children";

  private final MapIndex<Conductor<?>> conductor;
  private final MapIndex<Variables> variables;

  private final JsonStateMapFormat stateMapFormat;

  public JsonProcedureFormat(ConductorService conductorService, VariableService variableService) {
    this(conductorService, variableService, new JsonStateMapFormat());
  }

  public JsonProcedureFormat(
      ConductorService conductorService,
      VariableService variableService,
      JsonStateMapFormat stateMapFormat) {
    this.stateMapFormat = stateMapFormat;

    this.conductor = new MapIndex<>(
        CONDUCTOR,
        stringAccessor().map(conductorService::getConductor, conductorService::getId));

    this.variables = new MapIndex<>(
        VARIABLES,
        mapAccessor(variableService::createVariables, Variables::state));
  }

  @Override
  public String getExtension() {
    return FILE_EXTENSION;
  }

  @Override
  public Stream<MediaType> getMediaTypes() {
    return Stream.of(MEDIA_TYPE);
  }

  @Override
  public Payload<? extends Procedure> decodeString(String string) {
    return new Payload<>(loadProcedure(stateMapFormat.decodeString(string).data));
  }

  public Procedure loadProcedure(StateMap data) {
    return loadInstructions(Procedure.define(data.get(ID)), data.get(CHILDREN).asList());
  }

  private <T extends Product> Procedure loadInstructions(Procedure procedure, StateList data) {
    return loadInstructions(procedure, data, null);
  }

  private <T extends Product> Procedure loadInstructions(
      Procedure procedure,
      StateList data,
      ProductPath<Absolute> path) {
    return data
        .stream()
        .map(State::asMap)
        .reduce(procedure, (p, s) -> loadInstruction(p, s, path), throwingMerger());
  }

  private Procedure loadInstruction(
      Procedure procedure,
      StateMap data,
      ProductPath<Absolute> path) {
    var instruction = Instruction
        .define(data.get(ID), data.get(variables), (Conductor<?>) data.get(conductor));
    procedure = procedure.withInstruction(instruction);

    var children = data.get(CHILDREN).asMap();
    var experimentPath = path.getExperimentPath();

    return children
        .getKeys()
        .reduce(
            procedure,
            (p, k) -> loadInstructions(
                p,
                children.get(k).asList(),
                experimentPath.resolve(ProductIndex.fromString(k))),
            throwingMerger());
  }

  @Override
  public String encodeString(Payload<? extends Procedure> payload) {
    return stateMapFormat.encodeString(new Payload<>(saveProcedure(payload.data)));
  }

  public StateMap saveProcedure(Procedure procedure) {
    return StateMap
        .empty()
        .with(ID, procedure.id())
        .with(
            CHILDREN,
            c -> saveInstructions(
                procedure,
                procedure.independentInstructions().collect(toList())));
  }

  protected StateList saveInstructions(Procedure procedure, List<ExperimentPath<Absolute>> paths) {
    return paths
        .stream()
        .reduce(
            StateList.empty(),
            (l, s) -> l.withAdded(saveInstruction(procedure, s)),
            throwingMerger());
  }

  protected StateMap saveInstruction(Procedure procedure, ExperimentPath<Absolute> path) {
    var instruction = procedure.instruction(path).get();

    return StateMap
        .empty()
        .with(ID, instruction.id())
        .with(variables, instruction.variables())
        .with(
            CHILDREN,
            procedure
                .products(path)
                .reduce(
                    StateMap.empty(),
                    (m, s) -> m
                        .with(
                            s.getProductIndex().toString(),
                            saveInstructions(
                                procedure,
                                procedure.dependentInstructions(s).collect(toList()))),
                    throwingMerger()));
  }
}