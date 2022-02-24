package mongellaz.commands.handshake;

import mongellaz.commands.HandshakeResultObserver;
import mongellaz.communication.ByteArrayObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class HandshakeResponseProcessor implements ByteArrayObserver {
    @Override
    public void update(final byte[] response) {
        final byte commandCode = 0x10;
        if (response[0] == commandCode) {
            final byte[] expectedResponse = {commandCode, -123, -14, -98, -29, 67, 25, -22, -10};
            HandshakeResult handshakeResult;
            if (Arrays.equals(response, expectedResponse)) {
                handshakeResult = HandshakeResult.SUCCESS;
                logger.info("Received expected response");
            } else {
                handshakeResult = HandshakeResult.FAILURE;
                if (logger.isErrorEnabled()) {
                    logger.error("Invalid response: {}", Arrays.toString(response));
                }
            }
            notifyAllHandshakeResultObserver(handshakeResult);
        } else {
            logger.debug("Ignoring command with code {}", commandCode);
        }
    }

    public void addHandshakeResultObserver(HandshakeResultObserver handshakeResultObserver) {
        handshakeResultObservers.add(handshakeResultObserver);
    }

    private void notifyAllHandshakeResultObserver(HandshakeResult handshakeResult) {
        for (HandshakeResultObserver handshakeResultObserver : handshakeResultObservers) {
            handshakeResultObserver.update(handshakeResult);
        }
    }

    private final Logger logger = LogManager.getLogger();
    private final List<HandshakeResultObserver> handshakeResultObservers = new LinkedList<>();
}
