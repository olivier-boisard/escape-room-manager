package mongellaz.communication.handshake;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import mongellaz.communication.ByteArrayObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class HandshakeResponseProcessor implements ByteArrayObserver {

    @Inject
    HandshakeResponseProcessor(
            @Named("ExpectedDeviceFirmwareId") byte[] expectedDeviceFirmwareId,
            HandshakeResultObserver handshakeResultObserver
    ) {
        logger.debug("Create instance");
        this.expectedDeviceFirmwareId = expectedDeviceFirmwareId;
        this.handshakeResultObserver = handshakeResultObserver;
    }

    @Override
    public void update(final byte[] response) {
        final byte commandCode = 0x10;
        if (response[0] == commandCode) {
            HandshakeResult handshakeResult;
            final byte[] receivedFirmwareId = Arrays.copyOfRange(response, 1, response.length);
            if (Arrays.equals(receivedFirmwareId, expectedDeviceFirmwareId)) {
                handshakeResult = HandshakeResult.SUCCESS;
                logger.info("Received expected response");
            } else {
                handshakeResult = HandshakeResult.FAILURE;
                logger.error("Invalid response: {}", response);
            }
            notifyHandshakeResultObserver(handshakeResult);
        } else {
            logger.debug("Ignoring command: {}", response);
        }
    }

    private void notifyHandshakeResultObserver(HandshakeResult wifiHandshakeResult) {
        logger.debug("Notify");
        handshakeResultObserver.update(wifiHandshakeResult);
    }

    private final Logger logger = LogManager.getLogger();
    private final byte[] expectedDeviceFirmwareId;
    private final HandshakeResultObserver handshakeResultObserver;
}
