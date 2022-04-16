package mongellaz.devices.chinesemenupuzzle.devicecontroller;

import mongellaz.communication.DeviceController;

public interface ChineseMenuDeviceController extends DeviceController {
    void sendToggleLockCommand();

    void sendConfiguration(ChineseMenuConfiguration chineseMenuConfiguration);
}
