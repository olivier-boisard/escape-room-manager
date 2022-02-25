package mongellaz.communication.serial;

import com.fazecast.jSerialComm.SerialPort;
import mongellaz.communication.ByteArrayObserver;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SerialPortByteArrayObserver implements ByteArrayObserver {

    public void writeNextCommandInSerialPort() {
        if (serialPort != null) {
            byte[] command = commands.poll();
            if (command != null) {
                serialPort.writeBytes(command, command.length);
            }
        } else {
            throw new SerialPortCommunicationRuntimeException("Serial port must be set with setSerialPort()");
        }

    }

    @Override
    public void update(byte[] data) {
        commands.add(data);
    }

    public void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    private final Queue<byte[]> commands = new ConcurrentLinkedQueue<>();
    private SerialPort serialPort;

}
