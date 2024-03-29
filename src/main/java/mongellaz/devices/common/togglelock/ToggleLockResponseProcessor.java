package mongellaz.devices.common.togglelock;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.CommunicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class ToggleLockResponseProcessor implements ByteArrayObserver {

    @Inject
    ToggleLockResponseProcessor(LockStateObserver lockStateObserver, @Named("ToggleLockResponseProcessorExpectedCommandCode") Byte expectedCommandCode) {
        this.lockStateObserver = lockStateObserver;
        this.expectedCommandCode = expectedCommandCode;
    }

    @Override
    public void update(final byte[] response) {
        if (response[0] == expectedCommandCode) {
            logger.debug("Processing response: {}", response);
            try {
                checkResponse(response);
                runExpectedResponseProcess(response);
            } catch (CommunicationException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.debug("Ignoring command: {}", response);
        }
    }

    private void checkResponse(byte[] response) throws CommunicationException {
        final byte[] expectedResponse = {expectedCommandCode, 0x03, (byte) 0xF1};
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
    private final byte expectedCommandCode;
}
