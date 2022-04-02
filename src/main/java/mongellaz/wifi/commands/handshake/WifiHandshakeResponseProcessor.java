package mongellaz.wifi.commands.handshake;

import com.google.inject.Inject;
import mongellaz.communication.ByteArrayObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class WifiHandshakeResponseProcessor implements ByteArrayObserver {

    @Inject
    WifiHandshakeResponseProcessor(WifiHandshakeResultObserver wifiHandshakeResultObserver) {
        this.wifiHandshakeResultObserver = wifiHandshakeResultObserver;
    }

    @Override
    public void update(final byte[] response) {
        final byte commandCode = 0x10;
        if (response[0] == commandCode) {
            final byte[] expectedResponse = {commandCode, 0x7F, (byte) 0xE0, 0x04, (byte) 0xB2, 0x7C, (byte) 0xE1, 0x0A, 0x2A};
            WifiHandshakeResult wifiHandshakeResult;
            if (Arrays.equals(response, expectedResponse)) {
                wifiHandshakeResult = WifiHandshakeResult.SUCCESS;
                logger.info("Received expected response");
            } else {
                wifiHandshakeResult = WifiHandshakeResult.FAILURE;
                if (logger.isErrorEnabled()) {
                    logger.error("Invalid response: {}", Arrays.toString(response));
                }
            }
            notifyHandshakeResultObserver(wifiHandshakeResult);
        } else {
            logger.debug("Ignoring command with code {}", commandCode);
        }
    }

    private void notifyHandshakeResultObserver(WifiHandshakeResult wifiHandshakeResult) {
        wifiHandshakeResultObserver.update(wifiHandshakeResult);
    }

    private final Logger logger = LogManager.getLogger();
    private final WifiHandshakeResultObserver wifiHandshakeResultObserver;
}
