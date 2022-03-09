package mongellaz.communication.serial;

import com.fazecast.jSerialComm.SerialPort;
import mongellaz.communication.ByteArrayObserver;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SerialPortByteArrayObserver implements ByteArrayObserver, Runnable {

    public SerialPortByteArrayObserver(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    @Override
    public void run() {
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
    private final SerialPort serialPort;

}
