package mongellaz.communication;

import mongellaz.commands.HandshakeFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class CommunicationTests {
    public static void main(String[] args) {
        SerialCommunicationManager communicationManager = new SerialCommunicationManager();
        Logger logger = LogManager.getLogger();
        HandshakeFactory handshakeFactory = new HandshakeFactory();

        try {
            communicationManager.initialize();

            final int nChecks = 10;
            for (int i = 0; i < nChecks; i++) {
                communicationManager.write(handshakeFactory.generate());
                byte[] response = communicationManager.read();
                final byte[] expectedResponse = {16, -123, -14, -98, -29, 67, 25, -22, -10, 0};
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
