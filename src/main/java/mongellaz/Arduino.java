package mongellaz;

import com.fazecast.jSerialComm.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class Arduino {
    public static void main(String[] args) {
        SerialPort comPort = SerialPort.getCommPorts()[0];
        Logger logger = LogManager.getLogger();
        if (!comPort.openPort()) {
            logger.fatal("Could not open serial port. Aborting");
            return;
        }

        try {
            // Wait for serial port to be ready
            Thread.sleep(3000);

            int nChecks = 10;
            for (int i = 0; i < nChecks; i++) {
                // Write handshake
                final byte handshakeCode = 0x10;
                final byte[] softwareId = {0x01, (byte) 0xEE, 0x35, (byte) 0xD7, 0x2A, (byte) 0x80, 0x58, (byte) 0xEA};
                final byte[] expectedResponse = {16, -123, -14, -98, -29, 67, 25, -22, -10, 0};
                final byte endCode = 0x00;
                byte[] writeBuffer = new byte[softwareId.length + 2];
                writeBuffer[0] = handshakeCode;
                System.arraycopy(softwareId, 0, writeBuffer, 1, softwareId.length);
                writeBuffer[writeBuffer.length - 1] = endCode;
                comPort.writeBytes(writeBuffer, writeBuffer.length);

                // Read response
                final int inputBufferSize = 16;
                byte[] responseBuffer = new byte[inputBufferSize];
                int readBytes = 0;
                byte lastReadByte = (byte) 0xFF;
                int totalReadBytes = 0;
                while (lastReadByte != endCode && totalReadBytes < inputBufferSize) {
                    final int bufferSize = 128;
                    byte[] readBuffer = new byte[bufferSize];
                    int readBytesInOnce = comPort.readBytes(readBuffer, comPort.bytesAvailable());
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
                final byte[] response = Arrays.copyOfRange(responseBuffer, 0, expectedResponse.length);
                if (Arrays.equals(response, expectedResponse)) {
                    logger.info("OK");
                } else {
                    logger.error("Not OK");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } catch (ArduinoErrorException e) {
            e.printStackTrace();
        } finally {
            comPort.closePort();
        }
    }
}
