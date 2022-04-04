package mongellaz.devices.bookpuzzle.commands.togglelock;

import com.google.inject.Inject;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.CommunicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class ToggleLockResponseProcessor implements ByteArrayObserver {

    @Inject
    ToggleLockResponseProcessor(LockStateObserver lockStateObserver) {
        this.lockStateObserver = lockStateObserver;
    }

    @Override
    public void update(final byte[] response) {
        byte receivedCommandCode = response[0];
        if (receivedCommandCode == EXPECTED_COMMAND_CODE) {
            try {
                checkResponse(response);
                runExpectedResponseProcess(response);
            } catch (CommunicationException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.debug("Ignoring command with code {}", EXPECTED_COMMAND_CODE);
        }
    }

    private void checkResponse(byte[] response) throws CommunicationException {
        final byte[] expectedResponse = {EXPECTED_COMMAND_CODE, 0x03, (byte) 0xF1};
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

    private void notifyAllLockStateObserver(LockState lockState) {
        lockStateObserver.update(lockState);
    }

    private final Logger logger = LogManager.getLogger();
    private final LockStateObserver lockStateObserver;
    private static final byte EXPECTED_COMMAND_CODE = 0x30;
}
