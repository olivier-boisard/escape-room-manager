package mongellaz.devices.bookpuzzle.devicecontroller;


import mongellaz.communication.DeviceController;

public interface BookPuzzleDeviceController extends DeviceController {
    void sendToggleLockCommand();
    void sendToggleConfigurationModeCommand();
}
