package mongellaz;

import com.fazecast.jSerialComm.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class SerialCommunication {
    public static void main(String[] args) {
        SerialPort comPort = SerialPort.getCommPorts()[0];
        if (!comPort.openPort()) {
            System.err.println("Could not open serial port. Aborting");
            return;
        }

        try {
            // Wait for port to be ready
            Thread.sleep(10000);

            // Write handshake
            byte handshakeCode = 0x10;
            byte[] softwareId = {0x01, (byte) 0xEE, 0x35, (byte) 0xD7, 0x2A, (byte) 0x80, 0x58, (byte) 0xEA};
            byte endCode = 0x00;
            byte[] writeBuffer = new byte[softwareId.length + 1];
            writeBuffer[0] = handshakeCode;
            System.arraycopy(softwareId, 0, writeBuffer, 1, softwareId.length);
            writeBuffer[writeBuffer.length - 1] = endCode;
            comPort.writeBytes(writeBuffer, writeBuffer.length);

            // Read response
            int responseSize = 9;
            byte[] responseBuffer = new byte[responseSize];
            int readBytes = 0;
            while (readBytes < responseSize) {
                byte[] readBuffer = new byte[comPort.bytesAvailable()];
                int readBytesInOnce = comPort.readBytes(readBuffer, comPort.bytesAvailable());
                System.arraycopy(readBuffer, 0, responseBuffer, readBytes, readBytesInOnce);
                readBytes += readBytesInOnce;
            }
            while (comPort.bytesAvailable() == 0) {
                Thread.sleep(10);
            }

            System.out.println(Arrays.toString(Arrays.copyOfRange(responseBuffer, 0, responseSize)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        comPort.closePort();
    }
}
