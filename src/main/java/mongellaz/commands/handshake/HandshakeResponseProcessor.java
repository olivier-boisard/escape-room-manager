package mongellaz.commands.handshake;

import mongellaz.commands.ResponseProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class HandshakeResponseProcessor implements ResponseProcessor {
    @Override
    public void process(final byte[] response) {
        final byte[] expectedResponse = {16, -123, -14, -98, -29, 67, 25, -22, -10};
        if (Arrays.equals(response, expectedResponse)) {
            logger.info("Received expected response");
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("Invalid response: {}", Arrays.toString(response));
            }
        }
    }

    private final Logger logger = LogManager.getLogger();
}
