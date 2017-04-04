package uk.co.saiman.comms.saint.impl;

import static org.osgi.service.component.annotations.ReferencePolicy.STATIC;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.CommsStream;
import uk.co.saiman.comms.saint.impl.SaintHardwareSimulation.SaintHardwareSimulationConfiguration;
import uk.co.saiman.comms.serial.SerialPort;
import uk.co.saiman.comms.serial.SerialPorts;

@Designate(ocd = SaintHardwareSimulationConfiguration.class, factory = true)
@Component(
		name = SaintHardwareSimulation.CONFIGURATION_PID,
		configurationPid = SaintHardwareSimulation.CONFIGURATION_PID,
		immediate = true)
public class SaintHardwareSimulation {
	static final String CONFIGURATION_PID = "uk.co.saiman.comms.saint.simulation";

	@SuppressWarnings("javadoc")
	@ObjectClassDefinition(
			id = CONFIGURATION_PID,
			name = "SAINT Comms Hardware Simulation Configuration",
			description = "A configuration for a simulation of the SAINT instrument comms board")
	public @interface SaintHardwareSimulationConfiguration {
		@AttributeDefinition(
				name = "Serial Port",
				description = "The serial port for the hardware simulation")
		String serialPort();
	}

	@Reference(policy = STATIC, policyOption = GREEDY)
	SerialPorts serialPorts;
	private SerialPort port;
	private CommsStream stream;
	private ByteBuffer buffer;

	@Activate
	void activate(SaintHardwareSimulationConfiguration configuration) throws IOException {
		buffer = ByteBuffer.allocate(4);
		configure(configuration);
	}

	@Modified
	void configure(SaintHardwareSimulationConfiguration configuration) throws IOException {
		setPort(configuration.serialPort());
	}

	@Deactivate
	void deactivate() throws IOException {
		closePort();
	}

	private synchronized void setPort(String serialPort) throws IOException {
		closePort();
		port = serialPorts.getPort(serialPort);
		openPort();
	}

	private synchronized void openPort() {
		stream = port.openStream(SaintCommsImpl.MESSAGE_SIZE);
		stream.addObserver(buffer -> {
			do {
				boolean filled = false;
				do {
					this.buffer.put(buffer.get());
					filled = !this.buffer.hasRemaining();
				} while (!filled && buffer.hasRemaining());

				if (filled) {
					this.buffer.flip();
					receiveMessage();
					this.buffer.clear();
				}
			} while (buffer.hasRemaining());
		});
	}

	private synchronized void closePort() throws IOException {
		if (stream != null) {
			stream.close();
			stream = null;
		}
	}

	private void receiveMessage() {
		try {
			stream.write(buffer);
		} catch (IOException e) {
			throw port.setFault(new CommsException("Unable to send simulated hardware response", e));
		}
	}
}