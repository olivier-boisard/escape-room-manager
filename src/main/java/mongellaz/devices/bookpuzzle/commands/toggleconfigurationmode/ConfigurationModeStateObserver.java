package mongellaz.devices.bookpuzzle.commands.toggleconfigurationmode;

@FunctionalInterface
public interface ConfigurationModeStateObserver {
    void update(ConfigurationModeState configurationModeState);
}
