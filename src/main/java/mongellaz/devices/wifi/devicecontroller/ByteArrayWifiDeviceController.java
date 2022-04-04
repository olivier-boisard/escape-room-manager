package mongellaz.devices.wifi.devicecontroller;

import com.google.inject.Inject;
import mongellaz.communication.manager.QueuedCommands;
import mongellaz.communication.DeviceController;
import mongellaz.devices.wifi.commands.handshake.WifiHandshakeFactory;

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
