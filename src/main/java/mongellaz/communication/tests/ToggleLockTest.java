package mongellaz.communication.tests;

import mongellaz.commands.ToggleLockCommandFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class ToggleLockTest {
    public static void main(String[] args) {
        new SerialCommunicationTest(new ToggleLockCommandFactory(), new ToggleLockResponseProcessor()).run();
    }

    private static class ToggleLockResponseProcessor implements ResponseProcessor {
        @Override
        public void process(final byte[] response) {
            try {
                final byte[] expectedResponse = {0x30, 0x03, 0};
                if (Arrays.equals(response, expectedResponse)) {
                    logger.info("OK");
                } else {
                    logger.error("Not OK");
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }

        private final Logger logger = LogManager.getLogger();
    }
}
