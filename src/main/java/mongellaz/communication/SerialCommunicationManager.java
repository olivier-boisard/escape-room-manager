package mongellaz.communication;

import com.fazecast.jSerialComm.SerialPort;

import java.util.Arrays;

public class SerialCommunicationManager implements AutoCloseable, ByteArrayWriter {

    public SerialCommunicationManager() throws SerialCommunicationException {
        serialPort = SerialPort.getCommPorts()[0];
        if (!serialPort.openPort()) {
            throw new SerialCommunicationException("Could not open serial port. Aborting");
        }
    }

    @Override
    public void close() {
        serialPort.closePort();
    }

    @Override
    public void write(byte[] data) {
        serialPort.writeBytes(data, data.length);
    }

    public byte[] read() throws CommunicationException {
        // Variable declarations
        byte[] responseBuffer = new byte[INPUT_BUFFER_SIZE];
        int totalReadBytes = 0;
        byte lastReadByte = 0x00;

        // Read data from serial port
        do {
            final int bufferSize = 128;
            byte[] readBuffer = new byte[bufferSize];
            int readBytesInOnce = serialPort.readBytes(readBuffer, serialPort.bytesAvailable());

            if (readBytesInOnce > 0) {
                lastReadByte = readBuffer[readBytesInOnce - 1];
                System.arraycopy(readBuffer, 0, responseBuffer, totalReadBytes, readBytesInOnce);
                totalReadBytes += readBytesInOnce;
            }
        } while (continueRead(totalReadBytes, lastReadByte));
        checkReadSuccessful(lastReadByte);

        // Return
        return Arrays.copyOfRange(responseBuffer, 0, totalReadBytes - 1);
    }

    private boolean continueRead(int totalReadBytes, byte lastReadByte) {
        final boolean bufferNotFilled = totalReadBytes < INPUT_BUFFER_SIZE;
        final boolean readZeroBytes = totalReadBytes == 0;
        return bufferNotFilled && (isNotTerminationByte(lastReadByte) || readZeroBytes);
    }

    private void checkReadSuccessful(byte lastReadByte) throws CommunicationException {
        if (isNotTerminationByte(lastReadByte)) {
            throw new CommunicationException();
        }
    }

    private boolean isNotTerminationByte(byte b) {
        final byte terminationByte = 0x00;
        return b != terminationByte;
    }

    private final SerialPort serialPort;

    private static final int INPUT_BUFFER_SIZE = 16;

}
