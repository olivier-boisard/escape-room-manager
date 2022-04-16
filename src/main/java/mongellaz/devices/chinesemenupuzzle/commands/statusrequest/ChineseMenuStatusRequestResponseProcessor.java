package mongellaz.devices.chinesemenupuzzle.commands.statusrequest;

import com.google.inject.Inject;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.CommunicationException;
import mongellaz.devices.chinesemenupuzzle.devicecontroller.ChineseMenuConfiguration;
import mongellaz.devices.common.togglelock.LockState;
import mongellaz.devices.common.togglelock.LockStateObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ChineseMenuStatusRequestResponseProcessor implements ByteArrayObserver {
    @Inject
    ChineseMenuStatusRequestResponseProcessor(LockStateObserver lockStateObserver, ChineseMenuConfigurationObserver chineseMenuConfigurationObserver, ChineseMenuWeightObserver chineseMenuWeightObserver) {
        this.lockStateObserver = lockStateObserver;
        this.chineseMenuConfigurationObserver = chineseMenuConfigurationObserver;
        this.chineseMenuWeightObserver = chineseMenuWeightObserver;
    }

    @Override
    public void update(byte[] response) {
        final byte expectedCommandCode = 0x20;
        final byte currentWeightCode = 0x13;
        final byte parametersCode = 0x23;
        final byte lockStatusCode = 0x03;
        final byte errorCode = (byte) 0xFF;
        int index = 0;
        if (response[0] == expectedCommandCode) {
            try {
                while (index < response.length) {
                    final byte responseByte = response[index++];
                    final byte[] unprocessedResponse = Arrays.copyOfRange(response, index, response.length);
                    index += switch (responseByte) {
                        case expectedCommandCode -> {
                            logger.info("Command is 'status request'");
                            yield 0;
                        }
                        case lockStatusCode -> processLockStatus(unprocessedResponse);
                        case currentWeightCode -> processCurrentWeight(unprocessedResponse);
                        case parametersCode -> processParameters(unprocessedResponse);
                        case errorCode -> {
                            logger.error("Received error code");
                            yield 0;
                        }
                        default -> throw new CommunicationException("Unexpected byte: " + responseByte);
                    };
                }
            } catch (CommunicationException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.debug("Ignoring command: {}", response);
        }
    }

    private int processLockStatus(byte[] response) throws CommunicationException {
        int index = 0;
        final byte enabledCode = 0x03;
        final byte disabledCode = 0x04;
        byte lockStatus = response[index++];
        LockState lockState;
        String lockStatusMsg = "Lock is ";
        switch (lockStatus) {
            case enabledCode -> {
                lockState = LockState.CLOSED;
                lockStatusMsg += "locked";
            }
            case disabledCode -> {
                lockState = LockState.OPEN;
                lockStatusMsg += "unlocked";
            }
            default -> throw new CommunicationException("Unknown status " + lockStatus);
        }
        notifyLockStateObserver(lockState);
        logger.info(lockStatusMsg);
        return index;
    }

    private int processCurrentWeight(byte[] response) {
        final int intSizeInBytes = 4;
        final int weight = ByteBuffer.wrap(response).getInt();
        chineseMenuWeightObserver.update(weight);
        return intSizeInBytes;
    }

    private int processParameters(byte[] response) {
        final int intSizeInBytes = 4;
        final int minWeight = ByteBuffer.wrap(response).getInt();
        final int maxWeight = ByteBuffer.wrap(response).getInt(intSizeInBytes);
        final int minIntervalInMs = ByteBuffer.wrap(response).getInt(2 * intSizeInBytes);
        chineseMenuConfigurationObserver.update(new ChineseMenuConfiguration(minWeight, maxWeight, minIntervalInMs));
        return 3 * intSizeInBytes;
    }

    private void notifyLockStateObserver(LockState lockState) {
        lockStateObserver.update(lockState);
    }

    private final Logger logger = LogManager.getLogger();
    private final LockStateObserver lockStateObserver;
    private final ChineseMenuConfigurationObserver chineseMenuConfigurationObserver;
    private final ChineseMenuWeightObserver chineseMenuWeightObserver;
}
