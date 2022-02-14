package mongellaz;

import com.fazecast.jSerialComm.*;

import java.util.Arrays;

public class SerialCommunication {
    public static void main(String[] args) {
        SerialPort comPort = SerialPort.getCommPorts()[0];
        if (!comPort.openPort()) {
            System.err.println("Could not open serial port. Aborting");
            return;
        }

        try {
            // Wait for serial port to be ready
            Thread.sleep(3000);

            // Write handshake
            byte handshakeCode = 0x10;
            byte[] softwareId = {0x01, (byte) 0xEE, 0x35, (byte) 0xD7, 0x2A, (byte) 0x80, 0x58, (byte) 0xEA};
            byte endCode = 0x00;
            byte[] writeBuffer = new byte[softwareId.length + 2];
            writeBuffer[0] = handshakeCode;
            System.arraycopy(softwareId, 0, writeBuffer, 1, softwareId.length);
            writeBuffer[writeBuffer.length - 1] = endCode;
            comPort.writeBytes(writeBuffer, writeBuffer.length);

            // Read response
            int responseSize = 9;
            byte[] responseBuffer = new byte[responseSize];
            int readBytes = 0;
            while (readBytes < responseSize) {
                int bufferSize = 128;
                byte[] readBuffer = new byte[bufferSize];
                int readBytesInOnce = comPort.readBytes(readBuffer, comPort.bytesAvailable());
                if (readBytesInOnce > 0) {
                    System.arraycopy(readBuffer, 0, responseBuffer, readBytes, readBytesInOnce);
                    readBytes += readBytesInOnce;
                    if (responseBuffer[readBytes - 1] == -1) {
                        throw new ArduinoErrorException();
                    }
                }
            }

            System.out.println(Arrays.toString(Arrays.copyOfRange(responseBuffer, 0, responseSize)));
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
