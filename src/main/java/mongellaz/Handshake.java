package mongellaz;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class Handshake {
    public static void main(String[] args) {
        SerialCommunicationManager communicationManager = new SerialCommunicationManager();
        Logger logger = LogManager.getLogger();

        try {
            communicationManager.initialize();

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
                communicationManager.write(writeBuffer);
                byte[] response = communicationManager.read();

                if (Arrays.equals(response, expectedResponse)) {
                    logger.info("OK");
                } else {
                    logger.error("Not OK");
                }
            }
        } catch (CommunicationException e) {
            e.printStackTrace();
        } finally {
            communicationManager.close();
        }
    }
}
