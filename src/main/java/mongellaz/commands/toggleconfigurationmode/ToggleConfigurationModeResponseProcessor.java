package mongellaz.commands.toggleconfigurationmode;

import mongellaz.commands.ConfigurationModeStateObserver;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.CommunicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ToggleConfigurationModeResponseProcessor implements ByteArrayObserver {

    public static final byte COMMAND_CODE = 0x40;

    @Override
    public void update(final byte[] response) {
        if (response[0] == COMMAND_CODE) {
            try {
                checkResponse(response);
                runResponseIsValidProcess(response[3]);
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
            throw new CommunicationException("Unexpected Response");
        }
    }

    private void runResponseIsValidProcess(byte status) throws CommunicationException {
        final byte enabledStatus = 0x03;
        final byte disabledStatus = 0x04;
        ConfigurationModeState configurationModeState;
        String statusStr;
        switch (status) {
            case enabledStatus -> {
                configurationModeState = ConfigurationModeState.ENABLED;
                statusStr = " - Enabled";
            }
            case disabledStatus -> {
                configurationModeState = ConfigurationModeState.DISABLED;
                statusStr = " - Disabled";
            }
            default -> throw new CommunicationException("Unknown status " + status);
        }
        logger.info("OK - {}", statusStr);
        notifyAllConfigurationModeStateObservers(configurationModeState);
    }

    public void addConfigurationModeStateObserver(ConfigurationModeStateObserver configurationModeStateObserver) {
        configurationModeStateObservers.add(configurationModeStateObserver);
    }

    private void notifyAllConfigurationModeStateObservers(ConfigurationModeState newConfigurationModeState) {
        for (ConfigurationModeStateObserver configurationModeStateObserver : configurationModeStateObservers) {
            configurationModeStateObserver.update(newConfigurationModeState);
        }
    }

    private final List<ConfigurationModeStateObserver> configurationModeStateObservers = new LinkedList<>();
    private final Logger logger = LogManager.getLogger();
}
