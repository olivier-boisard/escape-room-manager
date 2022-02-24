package mongellaz.application;

import com.fazecast.jSerialComm.SerialPort;
import mongellaz.communication.ByteArrayObserver;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SerialPortByteArrayConsumer implements ByteArrayObserver {

    public SerialPortByteArrayConsumer(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public void writeNextCommandInSerialPort() {
        byte[] command = commands.poll();
        if (command != null) {
            serialPort.writeBytes(command, command.length);
        }
    }

    @Override
    public void update(byte[] data) {
        commands.add(data);
    }

    private final Queue<byte[]> commands = new ConcurrentLinkedQueue<>();
    SerialPort serialPort;
}
