package mongellaz.tests;

import mongellaz.commands.ResponseProcessor;
import mongellaz.commands.handshake.HandshakeFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class HandshakeTest {
    public static void main(String[] args) {
        int nChecks = 10;
        new SerialCommunicationTest(new HandshakeFactory(), new HandshakeResponseProcessor()).run(nChecks);
    }

    private static class HandshakeResponseProcessor implements ResponseProcessor {
        @Override
        public void process(final byte[] response) {
            final byte[] expectedResponse = {16, -123, -14, -98, -29, 67, 25, -22, -10};
            if (Arrays.equals(response, expectedResponse)) {
                logger.info("OK");
            } else {
                logger.error("Not OK");
            }
        }

        private final Logger logger = LogManager.getLogger();
    }
}
