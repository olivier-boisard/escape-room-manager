package mongellaz.commands.togglelock;

import mongellaz.commands.LockStateObserver;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.CommunicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ToggleLockResponseProcessor implements ByteArrayObserver {
    @Override
    public void update(final byte[] response) {
        final byte commandCode = 0x30;

        if (response[0] == commandCode) {
            try {
                final byte[] expectedResponse = {commandCode, 0x03, (byte) 0xF1};
                if (Arrays.equals(Arrays.copyOfRange(response, 0, expectedResponse.length), expectedResponse)) {
                    final byte openStatus = 0x01;
                    final byte closedStatus = 0x02;
                    final byte status = response[3];
                    LockState lockState;
                    String statusStr = switch (status) {
                        case openStatus -> {
                            lockState = LockState.OPEN;
                            yield " - Open";
                        }
                        case closedStatus -> {
                            lockState = LockState.CLOSED;
                            yield " - Closed";
                        }
                        default -> throw new CommunicationException("Unknown status " + status);
                    };
                    notifyAllLockStateObserver(lockState);
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

    public void addLockStateObserver(LockStateObserver lockStateObserver) {
        lockStateObservers.add(lockStateObserver);
    }

    private void notifyAllLockStateObserver(LockState lockState) {
        for (LockStateObserver lockStateObserver : lockStateObservers) {
            lockStateObserver.update(lockState);
        }
    }

    private final Logger logger = LogManager.getLogger();
    private final List<LockStateObserver> lockStateObservers = new LinkedList<>();
}
