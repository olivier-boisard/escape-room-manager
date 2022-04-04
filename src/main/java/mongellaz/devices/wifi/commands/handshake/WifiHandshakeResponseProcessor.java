package mongellaz.devices.wifi.commands.handshake;

import com.google.inject.Inject;
import mongellaz.communication.handshake.HandshakeResult;
import mongellaz.communication.handshake.HandshakeResultObserver;
import mongellaz.communication.ByteArrayObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class WifiHandshakeResponseProcessor implements ByteArrayObserver {

    @Inject
    WifiHandshakeResponseProcessor(HandshakeResultObserver handshakeResultObserver) {
        this.handshakeResultObserver = handshakeResultObserver;
    }

    //TODO refactor with BookPuzzleHandshakeResponseProcessor
    @Override
    public void update(final byte[] response) {
        final byte commandCode = 0x10;
        byte receivedCommandCode = response[0];
        if (receivedCommandCode == commandCode) {
            final byte[] expectedResponse = {0x7F, (byte) 0xE0, 0x04, (byte) 0xB2, 0x7C, (byte) 0xE1, 0x0A, 0x2A};
            HandshakeResult handshakeResult;
            final byte[] receivedFirmwareId = Arrays.copyOfRange(response, 1, response.length);
            if (Arrays.equals(receivedFirmwareId, expectedResponse)) {
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
            logger.debug("Ignoring command with code {}", receivedCommandCode);
        }
    }

    private void notifyHandshakeResultObserver(HandshakeResult wifiHandshakeResult) {
        handshakeResultObserver.update(wifiHandshakeResult);
    }

    private final Logger logger = LogManager.getLogger();
    private final HandshakeResultObserver handshakeResultObserver;
}
