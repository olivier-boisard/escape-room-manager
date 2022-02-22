package mongellaz.commands.statusrequest;

import mongellaz.commands.ResponseProcessor;
import mongellaz.communication.CommunicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatusRequestResponseProcessor implements ResponseProcessor {
    @Override
    public void process(final byte[] response) {
        try {
            final byte commandCode = 0x20;
            final byte piccReadersStatusCode = 0x01;
            final byte configurationModeStatusCode = 0x02;
            final byte magnetStatusCode = 0x03;
            final byte errorCode = (byte) 0xFF;
            final byte enabledCode = 0x03;
            final byte disabledCode = 0x04;
            final byte noPicc = 0x01;
            final byte wrongPicc = 0x02;
            final byte correctPicc = 0x03;
            int index = 0;

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
                                default -> throw new CommunicationException("Unknown status: " + status);
                            };
                            logger.info(piccReaderStatusMsg);
                        }
                    }
                    case configurationModeStatusCode -> {
                        byte configurationStatus = response[index++];
                        String configurationModeStatusMsg = "Configuration mode " + switch (configurationStatus) {
                            case enabledCode -> "enabled";
                            case disabledCode -> "disabled";
                            default -> throw new CommunicationException("Unknown status: " + configurationStatus);
                        };
                        logger.info(configurationModeStatusMsg);
                    }
                    case magnetStatusCode -> {
                        byte magnetStatus = response[index++];
                        String magnetStatusMsg = "Magnet is " + switch (magnetStatus) {
                            case enabledCode -> "locked";
                            case disabledCode -> "unlocked";
                            default -> throw new CommunicationException("Unknown status " + magnetStatus);
                        };
                        logger.info(magnetStatusMsg);
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
    }

    private final Logger logger = LogManager.getLogger();
}
