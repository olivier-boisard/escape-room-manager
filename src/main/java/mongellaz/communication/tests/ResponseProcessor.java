package mongellaz.communication.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class ResponseProcessor {
    public void process(final byte[] response) {
        final byte[] expectedResponse = {16, -123, -14, -98, -29, 67, 25, -22, -10, 0};
        if (Arrays.equals(response, expectedResponse)) {
            logger.info("OK");
        } else {
            logger.error("Not OK");
        }
    }

    private final Logger logger = LogManager.getLogger();
}
