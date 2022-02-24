package mongellaz.application;

import java.util.List;

public interface ArduinoBoardController {
    List<String> getConnectionOptions();
    void sendToggleLockCommand();
    void sendToggleConfigurationModeCommand();
}
