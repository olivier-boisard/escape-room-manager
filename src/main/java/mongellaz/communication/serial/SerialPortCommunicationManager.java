package mongellaz.communication.serial;

import com.fazecast.jSerialComm.SerialPort;
import mongellaz.communication.CommunicationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SerialPortCommunicationManager implements CommunicationManager {

    public SerialPortCommunicationManager(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    @Override
    public void sendNextCommand() {
        byte[] command = commands.poll();
        if (command != null) {
            serialPort.writeBytes(command, command.length);
        }
    }

    @Override
    public void queueCommand(byte[] data) {
        commands.add(data);
    }

    @Override
    public void shutdown() {
        if (!serialPort.closePort()) {
            logger.error("Could not close serial port");
        } else {
            logger.info("Closed serial port");
        }
    }

    private final Queue<byte[]> commands = new ConcurrentLinkedQueue<>();
    private final SerialPort serialPort;
    private final Logger logger = LogManager.getLogger();

}
