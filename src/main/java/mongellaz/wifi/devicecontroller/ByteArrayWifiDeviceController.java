package mongellaz.wifi.devicecontroller;

import com.google.inject.Inject;
import mongellaz.communication.manager.QueuedCommands;
import mongellaz.devicecontroller.DeviceController;
import mongellaz.wifi.commands.handshake.WifiHandshakeFactory;

public class ByteArrayWifiDeviceController implements DeviceController {

    @Inject
    public ByteArrayWifiDeviceController(QueuedCommands queuedCommands) {
        this.queuedCommands = queuedCommands;
    }

    @Override
    public void start() {
        queuedCommands.queueCommand(wifiHandshakeFactory.generate());
    }

    private final WifiHandshakeFactory wifiHandshakeFactory = new WifiHandshakeFactory();
    private final QueuedCommands queuedCommands;
}
