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
                    ConfigurationModeState configurationModeState;
                    String statusStr = switch (status) {
                        case enabledStatus -> {
                            configurationModeState = ConfigurationModeState.ENABLED;
                            yield " - Enabled";
                        }
                        case disabledStatus -> {
                            configurationModeState = ConfigurationModeState.DISABLED;
                            yield " - Disabled";
                        }
                        default -> throw new CommunicationException("Unknown status " + status);
                    };
                    notifyAllConfigurationModeStateObservers(configurationModeState);
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

    public enum ConfigurationModeState {
        ENABLED,
        DISABLED
    }

    public void addConfigurationModeStateObserver(ConfigurationModeStateObserver configurationModeStateObserver) {
        configurationModeStateObservers.add(configurationModeStateObserver);
    }

    public void notifyAllConfigurationModeStateObservers(ConfigurationModeState newConfigurationModeState) {
        for (ConfigurationModeStateObserver configurationModeStateObserver : configurationModeStateObservers) {
            configurationModeStateObserver.update(newConfigurationModeState);
        }
    }

    @FunctionalInterface
    public interface ConfigurationModeStateObserver {
        void update(ConfigurationModeState configurationModeState);
    }

    private final List<ConfigurationModeStateObserver> configurationModeStateObservers = new LinkedList<>();
    private final Logger logger = LogManager.getLogger();
}
