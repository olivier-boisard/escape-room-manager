package mongellaz.commands.toggleconfigurationmode;

import mongellaz.commands.ResponseProcessor;
import mongellaz.communication.CommunicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ToggleConfigurationModeResponseProcessor implements ResponseProcessor {

    @Override
    public void process(final byte[] response) {
        final byte commandCode = 0x40;
        if (response[0] == commandCode) {
            try {
                final byte[] expectedResponse = {commandCode, 0x03, (byte) 0xF1};
                if (Arrays.equals(Arrays.copyOfRange(response, 0, expectedResponse.length), expectedResponse)) {
                    final byte enabledStatus = 0x03;
                    final byte disabledStatus = 0x04;
                    final byte status = response[3];
                    MagnetState magnetState;
                    String statusStr = switch (status) {
                        case enabledStatus -> {
                            magnetState = MagnetState.OPEN;
                            yield " - Enabled";
                        }
                        case disabledStatus -> {
                            magnetState = MagnetState.CLOSED;
                            yield " - Disabled";
                        }
                        default -> throw new CommunicationException("Unknown status " + status);
                    };
                    notifyAllMagnetStateObservers(magnetState);
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

    public enum MagnetState {
        OPEN,
        CLOSED
    }

    public void addMagnetStateObserver(MagnetStateObserver magnetStateObserver) {
        magnetStateObservers.add(magnetStateObserver);
    }

    public void notifyAllMagnetStateObservers(MagnetState newMagnetState) {
        for (MagnetStateObserver magnetStateObserver : magnetStateObservers) {
            magnetStateObserver.update(newMagnetState);
        }
    }

    public interface MagnetStateObserver {
        void update(MagnetState magnetState);
    }

    private final List<MagnetStateObserver> magnetStateObservers = new LinkedList<>();
    private final Logger logger = LogManager.getLogger();
}
