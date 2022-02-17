package mongellaz;

import com.fazecast.jSerialComm.SerialPort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class ArduinoSerialCommunication implements ArduinoCommunication {

    public void initialize() throws ArduinoErrorException {
        serialPort = SerialPort.getCommPorts()[0];
        if (!serialPort.openPort()) {
            throw new ArduinoErrorException("Could not open serial port. Aborting");
        }
    }

    @Override
    public void write(byte[] data) throws ArduinoErrorException {
        serialPort.writeBytes(data, data.length);
    }

    @Override
    public byte[] read() throws ArduinoErrorException {
        final int inputBufferSize = 16;
        byte[] responseBuffer = new byte[inputBufferSize];
        int readBytes = 0;
        byte lastReadByte = (byte) 0xFF;
        int totalReadBytes = 0;
        final byte endCode = 0x00;
        while (lastReadByte != endCode && totalReadBytes < inputBufferSize) {
            final int bufferSize = 128;
            byte[] readBuffer = new byte[bufferSize];

            //TODO only this bit depend on SerialComm
            int readBytesInOnce = serialPort.readBytes(readBuffer, serialPort.bytesAvailable());

            totalReadBytes += readBytesInOnce;
            if (readBytesInOnce > 0) {
                lastReadByte = readBuffer[readBytesInOnce - 1];
                System.arraycopy(readBuffer, 0, responseBuffer, readBytes, readBytesInOnce);
                readBytes += readBytesInOnce;
            }
        }
        if (lastReadByte != endCode) {
            throw new ArduinoErrorException();
        }

        return Arrays.copyOfRange(responseBuffer, 0, totalReadBytes);
    }

    private SerialPort serialPort;

}
