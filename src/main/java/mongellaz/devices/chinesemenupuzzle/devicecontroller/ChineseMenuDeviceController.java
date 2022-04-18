package mongellaz.devices.chinesemenupuzzle.devicecontroller;

import mongellaz.communication.DeviceController;
import mongellaz.devices.chinesemenupuzzle.commands.configure.ChineseMenuConfiguration;

public interface ChineseMenuDeviceController extends DeviceController {
    void sendToggleLockCommand();

    void sendConfiguration(ChineseMenuConfiguration chineseMenuConfiguration);
}
