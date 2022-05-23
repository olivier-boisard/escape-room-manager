package mongellaz.devices.bookpuzzle.commands.toggleconfigurationmode;

import com.google.inject.Inject;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.CommunicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class ToggleConfigurationModeResponseProcessor implements ByteArrayObserver {

    @Inject
    ToggleConfigurationModeResponseProcessor(ConfigurationModeStateObserver configurationModeStateObserver) {
        this.configurationModeStateObserver = configurationModeStateObserver;
    }

    @Override
    public void update(final byte[] response) {
        if (response[0] == EXPECTED_COMMAND_CODE) {
            logger.debug("Processing command: {}", response);
            try {
                checkResponse(response);
                runResponseIsValidProcess(response[3]);
            } catch (CommunicationException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.debug("Ignoring command: {}", response);
        }
    }

    private void checkResponse(byte[] response) throws CommunicationException {
        final byte[] expectedResponse = {EXPECTED_COMMAND_CODE, 0x03, (byte) 0xF1};
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
        notifyConfigurationModeStateObserver(configurationModeState);
    }

    private void notifyConfigurationModeStateObserver(ConfigurationModeState newConfigurationModeState) {
        configurationModeStateObserver.update(newConfigurationModeState);
    }


    public static final byte EXPECTED_COMMAND_CODE = 0x40;
    private final ConfigurationModeStateObserver configurationModeStateObserver;
    private final Logger logger = LogManager.getLogger();
}
