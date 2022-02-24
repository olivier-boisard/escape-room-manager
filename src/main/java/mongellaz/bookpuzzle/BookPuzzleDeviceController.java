package mongellaz.bookpuzzle;

import java.util.List;

public interface BookPuzzleDeviceController {
    List<String> getConnectionOptions();
    void sendToggleLockCommand();
    void sendToggleConfigurationModeCommand();
}
