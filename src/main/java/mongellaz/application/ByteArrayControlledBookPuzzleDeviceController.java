package mongellaz.application;

import com.google.inject.Inject;
import mongellaz.bookpuzzle.BookPuzzleDeviceController;
import mongellaz.commands.handshake.HandshakeFactory;
import mongellaz.commands.statusrequest.StatusRequestFactory;
import mongellaz.commands.toggleconfigurationmode.ToggleConfigurationModeCommandFactory;
import mongellaz.commands.togglelock.ToggleLockCommandFactory;
import mongellaz.communication.ScheduledCommunicationManager;

public class ByteArrayControlledBookPuzzleDeviceController implements BookPuzzleDeviceController {

    @Inject
    ByteArrayControlledBookPuzzleDeviceController(ScheduledCommunicationManager scheduledCommunicationManager) {
        this.scheduledCommunicationManager = scheduledCommunicationManager;
    }

    public void start() {
        scheduledCommunicationManager.queueCommand(handshakeFactory.generate());
        scheduledCommunicationManager.queueCommand(statusRequestFactory.generate());
    }

    @Override
    public void sendToggleLockCommand() {
        scheduledCommunicationManager.queueCommand(toggleLockCommandFactory.generate());
    }

    @Override
    public void sendToggleConfigurationModeCommand() {
        scheduledCommunicationManager.queueCommand(toggleConfigurationModeCommandFactory.generate());
    }

    private final HandshakeFactory handshakeFactory = new HandshakeFactory();
    private final StatusRequestFactory statusRequestFactory = new StatusRequestFactory();
    private final ToggleLockCommandFactory toggleLockCommandFactory = new ToggleLockCommandFactory();
    private final ToggleConfigurationModeCommandFactory toggleConfigurationModeCommandFactory = new ToggleConfigurationModeCommandFactory();
    private final ScheduledCommunicationManager scheduledCommunicationManager;
}
