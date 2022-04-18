package mongellaz.devices.bookpuzzle.devicecontroller;

import com.google.inject.Inject;
import mongellaz.devices.bookpuzzle.commands.handshake.BookPuzzleHandshakeFactory;
import mongellaz.devices.bookpuzzle.commands.statusrequest.BookPuzzleStatusRequestFactory;
import mongellaz.devices.bookpuzzle.commands.toggleconfigurationmode.ToggleConfigurationModeCommandFactory;
import mongellaz.devices.common.togglelock.ToggleLockCommandFactory;
import mongellaz.communication.manager.QueuedCommands;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ByteArrayControlledBookPuzzleDeviceController implements BookPuzzleDeviceController {

    @Inject
    ByteArrayControlledBookPuzzleDeviceController(QueuedCommands queuedCommands) {
        this.queuedCommands = queuedCommands;
    }

    @Override
    public void start() {
        logger.debug("Starting byte device controller");
        queuedCommands.queueCommand(bookPuzzleHandshakeFactory.generate());
        queuedCommands.queueCommand(bookPuzzleStatusRequestFactory.generate());
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
    private final BookPuzzleStatusRequestFactory bookPuzzleStatusRequestFactory = new BookPuzzleStatusRequestFactory();
    private final ToggleLockCommandFactory toggleLockCommandFactory = new ToggleLockCommandFactory((byte) 0x30);
    private final ToggleConfigurationModeCommandFactory toggleConfigurationModeCommandFactory = new ToggleConfigurationModeCommandFactory();
    private final QueuedCommands queuedCommands;
    private final Logger logger = LogManager.getLogger();
}
