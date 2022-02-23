package mongellaz.commands.statusrequest;

import mongellaz.commands.ConfigurationModeStateObserver;
import mongellaz.commands.LockStateObserver;
import mongellaz.commands.ResponseProcessor;
import mongellaz.commands.toggleconfigurationmode.ConfigurationModeState;
import mongellaz.commands.togglelock.LockState;
import mongellaz.communication.CommunicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

public class StatusRequestResponseProcessor implements ResponseProcessor {
    //TODO this method is too big
    @Override
    public void process(final byte[] response) {
        final byte commandCode = 0x20;
        final byte piccReadersStatusCode = 0x01;
        final byte configurationModeStatusCode = 0x02;
        final byte lockStatusCode = 0x03;
        final byte errorCode = (byte) 0xFF;
        final byte enabledCode = 0x03;
        final byte disabledCode = 0x04;
        final byte noPicc = 0x01;
        final byte wrongPicc = 0x02;
        final byte correctPicc = 0x03;
        final byte newPicc = 0x04;
        int index = 0;

        if (response[0] == commandCode) {
            try {
                while (index < response.length) {
                    byte responseByte = response[index++];
                    switch (responseByte) {
                        case commandCode -> logger.info("Command is 'status request'");
                        case piccReadersStatusCode -> {
                            int nPiccReaders = response[index++];
                            for (int i = 0; i < nPiccReaders; i++) {
                                byte status = response[index++];
                                String piccReaderStatusMsg = "PICC reader " + i + ": " + switch (status) {
                                    case noPicc -> "No Picc";
                                    case wrongPicc -> "Wrong PICC";
                                    case correctPicc -> "Correct PICC";
                                    case newPicc -> "New PICC";
                                    default -> throw new CommunicationException("Unknown status: " + status);
                                };
                                logger.info(piccReaderStatusMsg);
                            }
                        }
                        case configurationModeStatusCode -> {
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
                        }
                        case lockStatusCode -> {
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
                        }
                        case errorCode -> logger.error("Error");
                        default -> throw new CommunicationException("Unexpected byte: " + responseByte);
                    }
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

    private void notifyAllLockStateObserver(LockState lockState) {
        for (LockStateObserver lockStateObserver : lockStateObservers) {
            lockStateObserver.update(lockState);
        }
    }

    private void notifyAllConfigurationModeStateObservers(ConfigurationModeState newConfigurationModeState) {
        for (ConfigurationModeStateObserver configurationModeStateObserver : configurationModeStateObservers) {
            configurationModeStateObserver.update(newConfigurationModeState);
        }
    }


    private final Logger logger = LogManager.getLogger();
    private final List<LockStateObserver> lockStateObservers = new LinkedList<>();
    private final List<ConfigurationModeStateObserver> configurationModeStateObservers = new LinkedList<>();
}
