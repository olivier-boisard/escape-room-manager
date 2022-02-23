package mongellaz.application;

import mongellaz.commands.ConfigurationModeStateObserver;
import mongellaz.commands.LockStateObserver;

public interface BoardStateObserver {
    void addLockStateObserver(LockStateObserver lockStateObserver);
    void addConfigurationModeStateObserver(ConfigurationModeStateObserver configurationModeStateObserver);
}
