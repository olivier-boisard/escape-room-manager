package mongellaz.devices.chinesemenupuzzle.commands.statusrequest;

import com.google.inject.Inject;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.CommunicationException;
import mongellaz.devices.chinesemenupuzzle.devicecontroller.ChineseMenuConfiguration;
import mongellaz.devices.common.togglelock.LockState;
import mongellaz.devices.common.togglelock.LockStateObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
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
        final int nDigits = 10;
        chineseMenuWeightObserver.update(Integer.parseInt(new String(Arrays.copyOfRange(response, 0, nDigits), StandardCharsets.US_ASCII)));
        return nDigits;
    }

    private int processParameters(byte[] response) {
        final int nDigitsPerValue = 10;
        final int minWeight = Integer.parseInt(new String(Arrays.copyOfRange(response, 0, nDigitsPerValue), StandardCharsets.US_ASCII));
        final int maxWeight = Integer.parseInt(new String(Arrays.copyOfRange(response, nDigitsPerValue, 2 * nDigitsPerValue), StandardCharsets.US_ASCII));
        final int minIntervalInMs = Integer.parseInt(new String(Arrays.copyOfRange(response, 2 * nDigitsPerValue, 3 * nDigitsPerValue), StandardCharsets.US_ASCII));
        chineseMenuConfigurationObserver.update(new ChineseMenuConfiguration(minWeight, maxWeight, minIntervalInMs));
        return 3 * nDigitsPerValue;
    }

    private void notifyLockStateObserver(LockState lockState) {
        lockStateObserver.update(lockState);
    }

    private final Logger logger = LogManager.getLogger();
    private final LockStateObserver lockStateObserver;
    private final ChineseMenuConfigurationObserver chineseMenuConfigurationObserver;
    private final ChineseMenuWeightObserver chineseMenuWeightObserver;
}
