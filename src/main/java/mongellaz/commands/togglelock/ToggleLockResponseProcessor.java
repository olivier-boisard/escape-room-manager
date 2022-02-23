package mongellaz.commands.togglelock;

import mongellaz.commands.MagnetStateObserver;
import mongellaz.commands.ResponseProcessor;
import mongellaz.communication.CommunicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ToggleLockResponseProcessor implements ResponseProcessor {
    @Override
    public void process(final byte[] response) {
        processResponse(response);
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
                    MagnetState magnetState;
                    String statusStr = switch (status) {
                        case openStatus -> {
                            magnetState = MagnetState.OPEN;
                            yield " - Open";
                        }
                        case closedStatus -> {
                            magnetState = MagnetState.CLOSED;
                            yield " - Closed";
                        }
                        default -> throw new CommunicationException("Unknown status " + status);
                    };
                    notifyAllMagnetStateObserver(magnetState);
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

    public enum MagnetState {
        OPEN,
        CLOSED
    }

    public void addMagnetStateObserver(MagnetStateObserver magnetStateObserver) {
        magnetStateObservers.add(magnetStateObserver);
    }

    public void notifyAllMagnetStateObserver(MagnetState magnetState) {
        for (MagnetStateObserver magnetStateObserver : magnetStateObservers) {
            magnetStateObserver.update(magnetState);
        }
    }

    private final Logger logger = LogManager.getLogger();
    private final List<MagnetStateObserver> magnetStateObservers = new LinkedList<>();
}
