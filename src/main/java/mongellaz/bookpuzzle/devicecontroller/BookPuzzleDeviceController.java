package mongellaz.bookpuzzle.devicecontroller;


import mongellaz.devicecontroller.PuzzleDeviceController;

public interface BookPuzzleDeviceController extends PuzzleDeviceController {
    void sendToggleLockCommand();
    void sendToggleConfigurationModeCommand();
}
