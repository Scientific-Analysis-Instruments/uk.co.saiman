package uk.co.saiman.comms.saint.impl;

import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.of;
import static uk.co.saiman.comms.saint.impl.SaintControllerREST.GET_ACTUAL_VALUE;
import static uk.co.saiman.comms.saint.impl.SaintControllerREST.GET_REQUESTED_VALUE;
import static uk.co.saiman.comms.saint.impl.SaintControllerREST.SET_REQUESTED_VALUE;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import osgi.enroute.dto.api.DTOs;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.rest.ControllerRESTEntry;
import uk.co.saiman.comms.saint.ValueReadback;
import uk.co.saiman.comms.saint.ValueRequest;

class SAINTCommsRESTEntry implements ControllerRESTEntry {
  private final DTOs dtos;

  private final Map<String, Object> inputData = new HashMap<>();
  private final Map<String, Object> outputData = new HashMap<>();

  private final String id;
  final ValueReadback<?> readback;
  final ValueRequest<?> request;

  public SAINTCommsRESTEntry(
      String name,
      ValueReadback<?> readback,
      ValueRequest<?> request,
      DTOs dtos) {
    this.id = name;
    this.readback = readback;
    this.request = request;
    this.dtos = dtos;
  }

  @Override
  public String getID() {
    return id;
  }

  @Override
  public Stream<String> getActions() {
    return concat(
        request != null ? of(SET_REQUESTED_VALUE, GET_REQUESTED_VALUE) : empty(),
        readback != null ? of(GET_ACTUAL_VALUE) : empty());
  }

  @Override
  public Map<String, Object> getInputData() {
    return new HashMap<>(inputData);
  }

  @Override
  public Map<String, Object> getOutputData() {
    return outputData;
  }

  public void setRequestedValue() {
    setRequestedValue(request);
  }

  private <T> void setRequestedValue(ValueRequest<T> request) {
    T value;
    try {
      value = dtos.convert(outputData).to(request.getType());
    } catch (Exception e) {
      throw new CommsException(
          "Cannot convert output data map to " + request.getType().getSimpleName(),
          e);
    }
    request.request(value);
  }

  public void getRequestedValue() {
    Object requested = request.getRequested();
    Map<String, Object> outputData;
    try {
      outputData = dtos.asMap(requested);
    } catch (Exception e) {
      throw new CommsException("Cannot convert " + requested + " to map", e);
    }
    this.outputData.clear();
    this.outputData.putAll(outputData);
  }

  public void getActualValue() {
    Object actual = readback.getActual();
    Map<String, Object> inputData;
    try {
      inputData = dtos.asMap(actual);
    } catch (Exception e) {
      throw new CommsException("Cannot convert " + actual + " to map", e);
    }
    this.inputData.clear();
    this.inputData.putAll(inputData);
  }
}