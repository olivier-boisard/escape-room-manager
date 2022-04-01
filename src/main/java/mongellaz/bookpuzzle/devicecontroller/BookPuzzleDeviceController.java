package mongellaz.bookpuzzle.devicecontroller;


import mongellaz.devicecontroller.DeviceController;

public interface BookPuzzleDeviceController extends DeviceController {
    void sendToggleLockCommand();
    void sendToggleConfigurationModeCommand();
}
