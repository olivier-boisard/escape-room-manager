package mongellaz.devices.chinesemenupuzzle.devicecontroller;

import com.google.inject.Inject;
import mongellaz.communication.manager.QueuedCommands;
import mongellaz.devices.chinesemenupuzzle.commands.handshake.ChineseMenuPuzzleHandshakeFactory;
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
        //TODO send status request
    }

    @Override
    public void sendToggleLockCommand() {
        //TODO
    }

    @Override
    public void sendConfiguration(ChineseMenuConfiguration chineseMenuConfiguration) {
        //TODO
    }

    private final QueuedCommands queuedCommands;
    private final ChineseMenuPuzzleHandshakeFactory chineseMenuPuzzleHandshakeFactory = new ChineseMenuPuzzleHandshakeFactory();
    private final Logger logger = LogManager.getLogger();
}
