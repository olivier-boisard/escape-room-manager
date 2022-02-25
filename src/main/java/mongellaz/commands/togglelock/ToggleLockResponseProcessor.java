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
        if (response[0] == COMMAND_CODE) {
            try {
                checkResponse(response);
                runExpectedResponseProcess(response);
            } catch (CommunicationException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.debug("Ignoring command with code {}", COMMAND_CODE);
        }
    }

    private void checkResponse(byte[] response) throws CommunicationException {
        final byte[] expectedResponse = {COMMAND_CODE, 0x03, (byte) 0xF1};
        if (!Arrays.equals(Arrays.copyOfRange(response, 0, expectedResponse.length), expectedResponse)) {
            throw new CommunicationException("Unexpected response");
        }
    }

    private void runExpectedResponseProcess(byte[] response) throws CommunicationException {
        final byte openStatus = 0x01;
        final byte closedStatus = 0x02;
        final byte status = response[3];
        LockState lockState;
        String statusStr;
        switch (status) {
            case openStatus -> {
                lockState = LockState.OPEN;
                statusStr = " - Open";
            }
            case closedStatus -> {
                lockState = LockState.CLOSED;
                statusStr = " - Closed";
            }
            default -> throw new CommunicationException("Unknown status " + status);
        }
        logger.info("OK - {}", statusStr);
        notifyAllLockStateObserver(lockState);
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
    private static final byte COMMAND_CODE = 0x30;
}
