package mongellaz.application;

import java.util.List;

public interface Controller {
    List<String> getConnectionOptions();
    void sendToggleLockCommand();
    void sendToggleConfigurationModeCommand();
}
