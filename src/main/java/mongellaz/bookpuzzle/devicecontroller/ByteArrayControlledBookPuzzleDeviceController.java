package mongellaz.bookpuzzle.devicecontroller;

import com.google.inject.Inject;
import mongellaz.bookpuzzle.commands.handshake.BookPuzzleHandshakeFactory;
import mongellaz.bookpuzzle.commands.statusrequest.StatusRequestFactory;
import mongellaz.bookpuzzle.commands.toggleconfigurationmode.ToggleConfigurationModeCommandFactory;
import mongellaz.bookpuzzle.commands.togglelock.ToggleLockCommandFactory;
import mongellaz.communication.manager.QueuedCommands;

public class ByteArrayControlledBookPuzzleDeviceController implements BookPuzzleDeviceController {

    @Inject
    ByteArrayControlledBookPuzzleDeviceController(QueuedCommands scheduledQueuedCommandSender) {
        this.scheduledQueuedCommandSender = scheduledQueuedCommandSender;
    }

    @Override
    public void start() {
        scheduledQueuedCommandSender.queueCommand(bookPuzzleHandshakeFactory.generate());
        scheduledQueuedCommandSender.queueCommand(statusRequestFactory.generate());
    }

    @Override
    public void sendToggleLockCommand() {
        scheduledQueuedCommandSender.queueCommand(toggleLockCommandFactory.generate());
    }

    @Override
    public void sendToggleConfigurationModeCommand() {
        scheduledQueuedCommandSender.queueCommand(toggleConfigurationModeCommandFactory.generate());
    }

    private final BookPuzzleHandshakeFactory bookPuzzleHandshakeFactory = new BookPuzzleHandshakeFactory();
    private final StatusRequestFactory statusRequestFactory = new StatusRequestFactory();
    private final ToggleLockCommandFactory toggleLockCommandFactory = new ToggleLockCommandFactory();
    private final ToggleConfigurationModeCommandFactory toggleConfigurationModeCommandFactory = new ToggleConfigurationModeCommandFactory();
    private final QueuedCommands scheduledQueuedCommandSender;
}
