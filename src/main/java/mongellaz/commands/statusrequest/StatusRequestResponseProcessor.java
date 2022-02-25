package mongellaz.commands.statusrequest;

import mongellaz.commands.ConfigurationModeStateObserver;
import mongellaz.commands.LockStateObserver;
import mongellaz.commands.PiccReaderStatusesObserver;
import mongellaz.commands.toggleconfigurationmode.ConfigurationModeState;
import mongellaz.commands.togglelock.LockState;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.CommunicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

//TODO refarctor this class and follow the SRP. There is duplicate code with XXXResponseProcessor.
public class StatusRequestResponseProcessor implements ByteArrayObserver {

    @Override
    public void update(final byte[] response) {
        final byte commandCode = 0x20;
        final byte piccReadersStatusCode = 0x01;
        final byte configurationModeStatusCode = 0x02;
        final byte lockStatusCode = 0x03;
        final byte errorCode = (byte) 0xFF;
        int index = 0;

        if (response[0] == commandCode) {
            try {
                while (index < response.length) {
                    byte responseByte = response[index++];
                    final byte[] unprocessedResponse = Arrays.copyOfRange(response, index, response.length);
                    index += switch (responseByte) {
                        case commandCode -> {
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
                e.printStackTrace();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        } else {
            logger.debug("Ignoring command with code {}", commandCode);
        }
    }

    public void addLockStateObserver(LockStateObserver lockStateObserver) {
        lockStateObservers.add(lockStateObserver);
    }

    public void addConfigurationModeStateObserver(ConfigurationModeStateObserver configurationModeStateObserver) {
        configurationModeStateObservers.add(configurationModeStateObserver);
    }

    public void addPiccReaderStatusesObserver(PiccReaderStatusesObserver piccReaderStatusesObserver) {
        piccReaderStatusesObservers.add(piccReaderStatusesObserver);
    }

    private void notifyAllLockStateObserver(LockState lockState) {
        for (LockStateObserver lockStateObserver : lockStateObservers) {
            lockStateObserver.update(lockState);
        }
    }

    private void notifyAllPiccReaderStatusesObservers(Iterable<PiccReaderStatus> piccReaderStatuses) {
        for (PiccReaderStatusesObserver piccReaderStatusesObserver : piccReaderStatusesObservers) {
            piccReaderStatusesObserver.update(piccReaderStatuses);
        }
    }

    private void notifyAllConfigurationModeStateObservers(ConfigurationModeState newConfigurationModeState) {
        for (ConfigurationModeStateObserver configurationModeStateObserver : configurationModeStateObservers) {
            configurationModeStateObserver.update(newConfigurationModeState);
        }
    }

    private int processLockStatus(byte[] response) throws CommunicationException {
        int index = 0;
        final byte enabledCode = 0x03;
        final byte disabledCode = 0x04;
        byte lockStatus = response[index++];
        LockState lockState;
        String lockStatusMsg = "Lock is " + switch (lockStatus) {
            case enabledCode -> {
                lockState = LockState.CLOSED;
                yield "locked";
            }
            case disabledCode -> {
                lockState = LockState.OPEN;
                yield "unlocked";
            }
            default -> throw new CommunicationException("Unknown status " + lockStatus);
        };
        notifyAllLockStateObserver(lockState);
        logger.info(lockStatusMsg);
        return index;
    }

    private int processConfigurationModeStatus(byte[] response) throws CommunicationException {
        int index = 0;
        final byte enabledCode = 0x03;
        final byte disabledCode = 0x04;
        byte configurationStatus = response[index++];
        ConfigurationModeState configurationModeState;
        String configurationModeStatusMsg = "Configuration mode " + switch (configurationStatus) {
            case enabledCode -> {
                configurationModeState = ConfigurationModeState.ENABLED;
                yield "enabled";
            }
            case disabledCode -> {
                configurationModeState = ConfigurationModeState.DISABLED;
                yield "disabled";
            }
            default -> throw new CommunicationException("Unknown status: " + configurationStatus);
        };
        notifyAllConfigurationModeStateObservers(configurationModeState);
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
        notifyAllPiccReaderStatusesObservers(piccReaderStatuses);
        return index;
    }

    private final Logger logger = LogManager.getLogger();
    private final List<LockStateObserver> lockStateObservers = new LinkedList<>();
    private final List<ConfigurationModeStateObserver> configurationModeStateObservers = new LinkedList<>();
    private final List<PiccReaderStatusesObserver> piccReaderStatusesObservers = new LinkedList<>();
}
