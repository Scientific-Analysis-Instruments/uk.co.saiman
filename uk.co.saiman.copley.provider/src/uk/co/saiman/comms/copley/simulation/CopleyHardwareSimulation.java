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
 * This file is part of uk.co.saiman.copley.provider.
 *
 * uk.co.saiman.copley.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.copley.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley.simulation;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static uk.co.saiman.comms.copley.CopleyController.HEADER_SIZE;
import static uk.co.saiman.comms.copley.CopleyController.WORD_SIZE;
import static uk.co.saiman.comms.copley.CopleyVariableID.ACTUAL_POSITION;
import static uk.co.saiman.comms.copley.CopleyVariableID.AMPLIFIER_STATE;
import static uk.co.saiman.comms.copley.CopleyVariableID.DRIVE_EVENT_STATUS;
import static uk.co.saiman.comms.copley.CopleyVariableID.LATCHED_EVENT_STATUS;
import static uk.co.saiman.comms.copley.CopleyVariableID.MOTOR_ENCODER_ANGULAR_RESOLUTION;
import static uk.co.saiman.comms.copley.CopleyVariableID.MOTOR_ENCODER_DIRECTION;
import static uk.co.saiman.comms.copley.CopleyVariableID.MOTOR_ENCODER_LINEAR_RESOLUTION;
import static uk.co.saiman.comms.copley.CopleyVariableID.MOTOR_ENCODER_UNITS;
import static uk.co.saiman.comms.copley.CopleyVariableID.TRAJECTORY_POSITION_COUNTS;
import static uk.co.saiman.comms.copley.CopleyVariableID.TRAJECTORY_PROFILE_MODE;
import static uk.co.saiman.comms.copley.VariableBank.ACTIVE;
import static uk.co.saiman.comms.copley.VariableBank.STORED;
import static uk.co.saiman.comms.copley.impl.CopleyNodeImpl.NODE_ID_MASK;
import static uk.co.saiman.log.Log.Level.ERROR;
import static uk.co.saiman.observable.Observer.onFailure;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.bytes.conversion.ByteConverterService;
import uk.co.saiman.comms.copley.CopleyOperationID;
import uk.co.saiman.comms.copley.CopleyVariableID;
import uk.co.saiman.comms.copley.ErrorCode;
import uk.co.saiman.comms.copley.Int32;
import uk.co.saiman.comms.copley.VariableIdentifier;
import uk.co.saiman.comms.copley.impl.CopleyControllerImpl;
import uk.co.saiman.comms.copley.simulation.CopleyHardwareSimulation.CopleyHardwareSimulationConfiguration;
import uk.co.saiman.log.Log;
import uk.co.saiman.messaging.DataReceiver;
import uk.co.saiman.messaging.DataSender;
import uk.co.saiman.observable.Disposable;

@Designate(ocd = CopleyHardwareSimulationConfiguration.class, factory = true)
@Component(name = CopleyHardwareSimulation.CONFIGURATION_PID, configurationPid = CopleyHardwareSimulation.CONFIGURATION_PID, configurationPolicy = REQUIRE)
public class CopleyHardwareSimulation {
  static final String CONFIGURATION_PID = "uk.co.saiman.comms.copley.simulation";

  private static final double MOTOR_SPEED_UNITS_PER_MILLISECOND = 10;

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(id = CONFIGURATION_PID, name = "Copley Comms Hardware Simulation Configuration", description = "A configuration for a simulation of the Copley motor control interface")
  public @interface CopleyHardwareSimulationConfiguration {
    @AttributeDefinition(name = "Serial Port", description = "The serial port for the hardware simulation")
    String port_target();

    @AttributeDefinition(name = "Node Count", description = "The number of nodes for multi-drop mode dispatch, or 0 for one direct connection")
    int nodes() default 0;

    @AttributeDefinition(name = "Axis Count", description = "The number of axes supported by the drive")
    int axes() default 1;
  }

  @Reference
  private Log log;

  @Reference
  private ByteConverterService converters;

  @Reference
  private DataSender response;
  @Reference
  private DataReceiver command;
  private Disposable observation;

  private final ByteBuffer header = ByteBuffer.allocate(HEADER_SIZE);
  private byte currentNode;
  private byte checksum;
  private CopleyOperationID operation;
  private ByteBuffer message;

  private int nodes;
  private int axes;

  private Map<CopleyVariableID, SimulatedVariable> variables = new HashMap<>();

  @Activate
  synchronized void activate(CopleyHardwareSimulationConfiguration configuration)
      throws IOException {
    configure(configuration);
    openObservation();
  }

  @Modified
  synchronized void configure(CopleyHardwareSimulationConfiguration configuration)
      throws IOException {
    try {
      nodes = configuration.nodes();
      axes = configuration.axes();
      variables.clear();

      variables.put(DRIVE_EVENT_STATUS, new ByteVariable(axes, 2));
      variables.put(LATCHED_EVENT_STATUS, new ByteVariable(axes, 2));
      variables.put(AMPLIFIER_STATE, new ByteVariable(axes, 1));
      variables.put(TRAJECTORY_PROFILE_MODE, new ByteVariable(axes, 1));

      ReferenceVariable<Int32> requestedPosition = new ReferenceVariable<>(
          axes,
          converters.getConverter(Int32.class),
          new Int32(0));
      variables.put(TRAJECTORY_POSITION_COUNTS, requestedPosition);
      variables
          .put(
              ACTUAL_POSITION,
              new InterpolatedVariable<>(
                  axes,
                  requestedPosition,
                  MOTOR_SPEED_UNITS_PER_MILLISECOND,
                  i -> (double) i.value,
                  d -> new Int32(d.intValue())));

      variables.put(MOTOR_ENCODER_UNITS, new ByteVariable(axes, 1));
      variables.put(MOTOR_ENCODER_ANGULAR_RESOLUTION, new ByteVariable(axes, 2));
      variables.put(MOTOR_ENCODER_LINEAR_RESOLUTION, new ByteVariable(axes, 1));
      variables.put(MOTOR_ENCODER_DIRECTION, new ByteVariable(axes, 1));
    } catch (Exception e) {
      log.log(ERROR, e);
      e.printStackTrace();
    }
  }

  @Deactivate
  void deactivate() throws IOException {
    closeObservation();
  }

  private synchronized void openObservation() {
    if (observation == null) {
      try {
        observation = command
            .receiveData()
            .then(onFailure(f -> closeObservation()))
            .observe(buffer -> {
              do {
                boolean onHeader = message == null;
                ByteBuffer currentBuffer = onHeader ? header : message;

                do {
                  currentBuffer.put(buffer.get());
                } while (currentBuffer.hasRemaining() && buffer.hasRemaining());

                if (!currentBuffer.hasRemaining()) {
                  currentBuffer.flip();

                  if (onHeader) {
                    receiveHeader();
                    if (message.capacity() == 0) {
                      receiveMessage();
                    }
                  } else {
                    receiveMessage();
                  }
                }
              } while (buffer.hasRemaining());
            });
      } catch (Exception e) {
        log.log(ERROR, e);
      }
    }
  }

  private synchronized void closeObservation() {
    if (observation != null) {
      observation.cancel();
      observation = null;
    }
  }

  private synchronized void receiveHeader() {
    currentNode = (byte) (header.get() & NODE_ID_MASK);
    checksum = header.get();
    message = ByteBuffer.allocate(header.get() * WORD_SIZE);
    operation = CopleyOperationID.getCanonicalOperation(header.get());
    header.clear();
  }

  private synchronized void receiveMessage() {
    byte[] result = new byte[] {};
    ErrorCode errorCode = ErrorCode.SUCCESS;

    if (nodes == currentNode) {
      try {
        VariableIdentifier variable;
        CopleyVariableID id;
        switch (operation) {
        case GET_VARIABLE:
          variable = getVariableIdentifier();
          id = CopleyVariableID.forCode(variable.variableID);
          if (variable.axis < 0 || variable.axis >= axes) {
            result = new byte[0];
            errorCode = ErrorCode.ILLEGAL_AXIS_NUMBER;
          } else {
            result = getVariable(id).get(variable.axis, variable.bank ? STORED : ACTIVE);
          }
          break;
        case SET_VARIABLE:
          variable = getVariableIdentifier();
          id = CopleyVariableID.forCode(variable.variableID);
          byte[] value = new byte[message.remaining()];
          message.get(value);
          if (variable.axis < 0 || variable.axis >= axes) {
            errorCode = ErrorCode.ILLEGAL_AXIS_NUMBER;
          } else {
            getVariable(id).set(variable.axis, variable.bank ? STORED : ACTIVE, value);
          }
          break;
        case COPY_VARIABLE:
          variable = getVariableIdentifier();
          id = CopleyVariableID.forCode(variable.variableID);
          if (variable.axis < 0 || variable.axis >= axes) {
            errorCode = ErrorCode.ILLEGAL_AXIS_NUMBER;
          } else {
            getVariable(id).copy(variable.axis, variable.bank ? STORED : ACTIVE);
          }
          break;
        case COPLEY_VIRTUAL_MACHINE:
          break;
        case DYNAMIC_FILE_INTERFACE:
          break;
        case ENCODER:
          break;
        case ERROR_LOG:
          break;
        case GET_CAN_OBJECT:
          break;
        case GET_FLASH_CRC:
          break;
        case GET_OPERATING_MODE:
          break;
        case NO_OP:
          break;
        case RESET:
          break;
        case SET_CAN_OBJECT:
          break;
        case SWITCH_OPERATING_MODE:
          break;
        case TRACE_VARIABLE:
          break;
        case TRAJECTORY:
          break;
        default:
          throw new IllegalArgumentException("Unexpected operation " + operation);
        }

        byte checksum = (byte) (CopleyControllerImpl.CHECKSUM ^ result.length);
        for (byte item : result)
          checksum ^= item;

        ByteBuffer response = ByteBuffer.allocate(result.length + HEADER_SIZE);
        response.put((byte) 0);
        response.put(checksum);
        response.put((byte) (result.length / WORD_SIZE));
        response.put((byte) errorCode.ordinal());
        response.put(result);
        response.flip();

        this.response.sendData(response);
      } catch (Exception e) {
        log.log(ERROR, "Unable to send simulated hardware response: " + e.getMessage(), e);
      }
    }

    message = null;
  }

  private SimulatedVariable getVariable(CopleyVariableID id) {
    if (!variables.containsKey(id))
      throw new IllegalArgumentException();
    return variables.get(id);
  }

  private VariableIdentifier getVariableIdentifier() {
    byte[] bytes = new byte[WORD_SIZE];
    message.get(bytes);
    return converters.getConverter(VariableIdentifier.class).toObject(bytes);
  }
}
