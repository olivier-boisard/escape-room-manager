package mongellaz.bookpuzzle.commands.handshake;

import com.google.inject.Inject;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.handshake.HandshakeResult;
import mongellaz.communication.handshake.HandshakeResultObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class BookPuzzleHandshakeResponseProcessor implements ByteArrayObserver {

    @Inject
    BookPuzzleHandshakeResponseProcessor(HandshakeResultObserver handshakeResultObserver) {
        this.handshakeResultObserver = handshakeResultObserver;
    }

    @Override
    public void update(final byte[] response) {
        final byte commandCode = 0x10;
        byte receivedCommandCode = response[0];
        if (receivedCommandCode == commandCode) {
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
            notifyHandshakeResultObserver(handshakeResult);
        } else {
            logger.debug("Ignoring command with code {}", commandCode);
        }
    }

    private void notifyHandshakeResultObserver(HandshakeResult handshakeResult) {
        handshakeResultObserver.update(handshakeResult);
    }

    private final Logger logger = LogManager.getLogger();
    private final HandshakeResultObserver handshakeResultObserver;
}
