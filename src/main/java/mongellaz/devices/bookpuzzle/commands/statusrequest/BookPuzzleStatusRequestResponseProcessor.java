package mongellaz.devices.bookpuzzle.commands.statusrequest;

import com.google.inject.Inject;
import mongellaz.devices.bookpuzzle.commands.toggleconfigurationmode.ConfigurationModeState;
import mongellaz.devices.bookpuzzle.commands.toggleconfigurationmode.ConfigurationModeStateObserver;
import mongellaz.devices.common.togglelock.LockState;
import mongellaz.devices.common.togglelock.LockStateObserver;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.CommunicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BookPuzzleStatusRequestResponseProcessor implements ByteArrayObserver {

    @Inject
    BookPuzzleStatusRequestResponseProcessor(
            LockStateObserver lockStateObserver,
            ConfigurationModeStateObserver configurationModeStateObserver,
            PiccReaderStatusesObserver piccReaderStatusesObserver
    ) {
        this.lockStateObserver = lockStateObserver;
        this.configurationModeStateObserver = configurationModeStateObserver;
        this.piccReaderStatusesObserver = piccReaderStatusesObserver;
    }

    @Override
    public void update(final byte[] response) {
        final byte expectedCommandCode = 0x20;
        final byte piccReadersStatusCode = 0x01;
        final byte configurationModeStatusCode = 0x02;
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
                        case piccReadersStatusCode -> processPiccReaderStatus(unprocessedResponse);
                        case configurationModeStatusCode -> processConfigurationModeStatus(unprocessedResponse);
                        case lockStatusCode -> processLockStatus(unprocessedResponse);
                        case errorCode -> {
                            logger.error("Received error code");
                            yield 0;
                        }
                        default -> throw new CommunicationException("Unexpected byte: " + responseByte);
                    };
                }
                Thread.sleep(1000);
            } catch (CommunicationException e) {
                logger.error(e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.fatal("Thread error");
            }
        } else {
            logger.debug("Ignoring command: {}", response);
        }
    }

    private void notifyLockStateObserver(LockState lockState) {
        lockStateObserver.update(lockState);
    }

    private void notifyPiccReaderStatusesObservers(Iterable<PiccReaderStatus> piccReaderStatuses) {
        piccReaderStatusesObserver.update(piccReaderStatuses);
    }

    private void notifyConfigurationModeStateObservers(ConfigurationModeState newConfigurationModeState) {
        configurationModeStateObserver.update(newConfigurationModeState);
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

    private int processConfigurationModeStatus(byte[] response) throws CommunicationException {
        int index = 0;
        final byte enabledCode = 0x03;
        final byte disabledCode = 0x04;
        byte configurationStatus = response[index++];
        ConfigurationModeState configurationModeState;
        String configurationModeStatusMsg = "Configuration mode ";
        switch (configurationStatus) {
            case enabledCode -> {
                configurationModeState = ConfigurationModeState.ENABLED;
                configurationModeStatusMsg += "enabled";
            }
            case disabledCode -> {
                configurationModeState = ConfigurationModeState.DISABLED;
                configurationModeStatusMsg += "disabled";
            }
            default -> throw new CommunicationException("Unknown status: " + configurationStatus);
        }
        notifyConfigurationModeStateObservers(configurationModeState);
        logger.info(configurationModeStatusMsg);
        return index;
    }

    private int processPiccReaderStatus(byte[] response) throws CommunicationException {
        final byte noPicc = 0x01;
        final byte wrongPicc = 0x02;
        final byte correctPicc = 0x03;
        final byte newPicc = 0x04;

        int index = 0;
        int nPiccReaders = response[index++];
        List<PiccReaderStatus> piccReaderStatuses = new LinkedList<>();
        for (int i = 0; i < nPiccReaders; i++) {
            byte status = response[index++];
            String piccReaderStatusMsg = "PICC reader " + i + ": ";
            switch (status) {
                case noPicc -> {
                    piccReaderStatusMsg += "No Picc";
                    piccReaderStatuses.add(PiccReaderStatus.NO_PICC);
                }
                case wrongPicc -> {
                    piccReaderStatusMsg += "Wrong PICC";
                    piccReaderStatuses.add(PiccReaderStatus.WRONG_PICC);
                }
                case correctPicc -> {
                    piccReaderStatusMsg += "Correct PICC";
                    piccReaderStatuses.add(PiccReaderStatus.CORRECT_PICC);
                }
                case newPicc -> {
                    piccReaderStatusMsg += "New PICC";
                    piccReaderStatuses.add(PiccReaderStatus.NEW_PICC);
                }
                default -> throw new CommunicationException("Unknown status: " + status);
            }
            logger.info(piccReaderStatusMsg);
        }
        notifyPiccReaderStatusesObservers(piccReaderStatuses);
        return index;
    }

    private final Logger logger = LogManager.getLogger();
    private final LockStateObserver lockStateObserver;
    private final ConfigurationModeStateObserver configurationModeStateObserver;
    private final PiccReaderStatusesObserver piccReaderStatusesObserver;
}
