package mongellaz.bookpuzzle.commands.toggleconfigurationmode;

@FunctionalInterface
public interface ConfigurationModeStateObserver {
    void update(ConfigurationModeState configurationModeState);
}
