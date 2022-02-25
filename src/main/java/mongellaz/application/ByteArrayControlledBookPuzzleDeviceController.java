package mongellaz.application;

import mongellaz.bookpuzzle.BookPuzzleDeviceController;
import mongellaz.commands.handshake.HandshakeFactory;
import mongellaz.commands.statusrequest.StatusRequestFactory;
import mongellaz.commands.toggleconfigurationmode.ToggleConfigurationModeCommandFactory;
import mongellaz.commands.togglelock.ToggleLockCommandFactory;
import mongellaz.communication.ByteArrayObserver;

public class ByteArrayControlledBookPuzzleDeviceController implements BookPuzzleDeviceController {

    public ByteArrayControlledBookPuzzleDeviceController(ByteArrayObserver commandHandler) {
        this.commandHandler = commandHandler;
    }

    public void start() {
        commandHandler.update(handshakeFactory.generate());
        commandHandler.update(statusRequestFactory.generate());
    }

    @Override
    public void sendToggleLockCommand() {
        commandHandler.update(toggleLockCommandFactory.generate());
    }

    @Override
    public void sendToggleConfigurationModeCommand() {
        commandHandler.update(toggleConfigurationModeCommandFactory.generate());
    }

    private final HandshakeFactory handshakeFactory = new HandshakeFactory();
    private final StatusRequestFactory statusRequestFactory = new StatusRequestFactory();
    private final ToggleLockCommandFactory toggleLockCommandFactory = new ToggleLockCommandFactory();
    private final ToggleConfigurationModeCommandFactory toggleConfigurationModeCommandFactory = new ToggleConfigurationModeCommandFactory();
    private final ByteArrayObserver commandHandler;
}
