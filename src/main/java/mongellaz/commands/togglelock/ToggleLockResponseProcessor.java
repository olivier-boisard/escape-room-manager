package mongellaz.commands.togglelock;

import mongellaz.commands.ResponseProcessor;
import mongellaz.communication.CommunicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class ToggleLockResponseProcessor implements ResponseProcessor {
    @Override
    public void process(final byte[] response) {
        processResponse(response);
        waitOneSecond();
    }

    private void processResponse(byte[] response) {
        final byte commandCode = 0x30;

        if (response[0] == commandCode) {
            try {
                final byte[] expectedResponse = {commandCode, 0x03, (byte) 0xF1};
                if (Arrays.equals(Arrays.copyOfRange(response, 0, expectedResponse.length), expectedResponse)) {
                    final byte openStatus = 0x01;
                    final byte closedStatus = 0x02;
                    final byte status = response[3];
                    String statusStr = switch (status) {
                        case openStatus -> " - Open";
                        case closedStatus -> " - Closed";
                        default -> throw new CommunicationException("Unknown status " + status);
                    };
                    logger.info("OK - {}", statusStr);
                } else {
                    logger.error("Not OK");
                }
            } catch (CommunicationException e) {
                e.printStackTrace();
            }
        } else {
            logger.debug("Ignoring command with code {}", commandCode);
        }
    }

    private static void waitOneSecond() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    private final Logger logger = LogManager.getLogger();
}
