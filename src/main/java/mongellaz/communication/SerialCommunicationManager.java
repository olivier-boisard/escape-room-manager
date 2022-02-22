package mongellaz.communication;

import com.fazecast.jSerialComm.SerialPort;

@SuppressWarnings("ClassCanBeRecord")
public class SerialCommunicationManager implements AutoCloseable, ByteArrayWriter {

    public SerialCommunicationManager(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    @Override
    public void close() {
        serialPort.closePort();
    }

    @Override
    public void write(byte[] data) {
        serialPort.writeBytes(data, data.length);
    }

    private final SerialPort serialPort;
}
