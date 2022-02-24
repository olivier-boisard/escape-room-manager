package mongellaz.communication;

import com.fazecast.jSerialComm.SerialPort;

@SuppressWarnings("ClassCanBeRecord")
public class SerialCommunicationManager implements AutoCloseable, ByteArrayObserver {

    public SerialCommunicationManager(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    @Override
    public void close() {
        serialPort.closePort();
    }

    @Override
    public void update(byte[] data) {
        serialPort.writeBytes(data, data.length);
    }

    private final SerialPort serialPort;
}
