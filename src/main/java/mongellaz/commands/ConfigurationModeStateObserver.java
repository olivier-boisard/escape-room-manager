package mongellaz.commands;

import mongellaz.commands.toggleconfigurationmode.ConfigurationModeState;

@FunctionalInterface
public interface ConfigurationModeStateObserver {
    void update(ConfigurationModeState configurationModeState);
}
