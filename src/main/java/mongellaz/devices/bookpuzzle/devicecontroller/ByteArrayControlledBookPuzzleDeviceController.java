package mongellaz.devices.bookpuzzle.devicecontroller;

import com.google.inject.Inject;
import mongellaz.devices.bookpuzzle.commands.handshake.BookPuzzleHandshakeFactory;
import mongellaz.devices.bookpuzzle.commands.statusrequest.StatusRequestFactory;
import mongellaz.devices.bookpuzzle.commands.toggleconfigurationmode.ToggleConfigurationModeCommandFactory;
import mongellaz.devices.bookpuzzle.commands.togglelock.ToggleLockCommandFactory;
import mongellaz.communication.manager.QueuedCommands;

public class ByteArrayControlledBookPuzzleDeviceController implements BookPuzzleDeviceController {

    @Inject
    ByteArrayControlledBookPuzzleDeviceController(QueuedCommands queuedCommands) {
        this.queuedCommands = queuedCommands;
    }

    @Override
    public void start() {
        queuedCommands.queueCommand(bookPuzzleHandshakeFactory.generate());
        queuedCommands.queueCommand(statusRequestFactory.generate());
    }

    @Override
    public void sendToggleLockCommand() {
        queuedCommands.queueCommand(toggleLockCommandFactory.generate());
    }

    @Override
    public void sendToggleConfigurationModeCommand() {
        queuedCommands.queueCommand(toggleConfigurationModeCommandFactory.generate());
    }

    private final BookPuzzleHandshakeFactory bookPuzzleHandshakeFactory = new BookPuzzleHandshakeFactory();
    private final StatusRequestFactory statusRequestFactory = new StatusRequestFactory();
    private final ToggleLockCommandFactory toggleLockCommandFactory = new ToggleLockCommandFactory();
    private final ToggleConfigurationModeCommandFactory toggleConfigurationModeCommandFactory = new ToggleConfigurationModeCommandFactory();
    private final QueuedCommands queuedCommands;
}
