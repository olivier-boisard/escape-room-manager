package mongellaz.devices.chinesemenupuzzle.devicecontroller;

import com.google.inject.Inject;
import mongellaz.communication.manager.QueuedCommands;
import mongellaz.devices.chinesemenupuzzle.commands.configure.ChineseMenuConfiguration;
import mongellaz.devices.chinesemenupuzzle.commands.configure.ChineseMenuConfigureRequestFactory;
import mongellaz.devices.chinesemenupuzzle.commands.handshake.ChineseMenuPuzzleHandshakeFactory;
import mongellaz.devices.chinesemenupuzzle.commands.statusrequest.ChineseMenuStatusRequestFactory;
import mongellaz.devices.common.togglelock.ToggleLockCommandFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ByteArrayControlledChineseMenuDeviceController implements ChineseMenuDeviceController {
    @Inject
    ByteArrayControlledChineseMenuDeviceController(QueuedCommands queuedCommands) {
        this.queuedCommands = queuedCommands;
    }

    @Override
    public void start() {
        logger.debug("Starting byte device controller");
        queuedCommands.queueCommand(chineseMenuPuzzleHandshakeFactory.generate());
        queuedCommands.queueCommand(chineseMenuStatusRequestFactory.generate());
    }

    @Override
    public void sendToggleLockCommand() {
        logger.debug("Send toggle lock command");
        queuedCommands.queueCommand(toggleLockCommandFactory.generate());
    }

    @Override
    public void sendConfiguration(ChineseMenuConfiguration chineseMenuConfiguration) {
        logger.debug("Send configuration command");
        queuedCommands.queueCommand(chineseMenuConfigureRequestFactory.generate(chineseMenuConfiguration));
    }

    private final QueuedCommands queuedCommands;
    private final ChineseMenuPuzzleHandshakeFactory chineseMenuPuzzleHandshakeFactory = new ChineseMenuPuzzleHandshakeFactory();
    private final ChineseMenuStatusRequestFactory chineseMenuStatusRequestFactory = new ChineseMenuStatusRequestFactory();
    private final ChineseMenuConfigureRequestFactory chineseMenuConfigureRequestFactory = new ChineseMenuConfigureRequestFactory();
    private final ToggleLockCommandFactory toggleLockCommandFactory = new ToggleLockCommandFactory((byte) 0x05);
    private final Logger logger = LogManager.getLogger();
}
