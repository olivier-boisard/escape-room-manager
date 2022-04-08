package mongellaz.devices.wifi.devicecontroller;

import com.google.inject.Inject;
import mongellaz.communication.manager.QueuedCommands;
import mongellaz.communication.DeviceController;
import mongellaz.devices.wifi.commands.handshake.WifiHandshakeFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ByteArrayWifiDeviceController implements DeviceController {

    @Inject
    public ByteArrayWifiDeviceController(QueuedCommands queuedCommands) {
        this.queuedCommands = queuedCommands;
    }

    @Override
    public void start() {
        logger.info("Start wifi device controller");
        queuedCommands.queueCommand(wifiHandshakeFactory.generate());
    }

    private final WifiHandshakeFactory wifiHandshakeFactory = new WifiHandshakeFactory();
    private final QueuedCommands queuedCommands;
    private final Logger logger = LogManager.getLogger();
}
