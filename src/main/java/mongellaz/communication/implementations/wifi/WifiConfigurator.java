package mongellaz.communication.implementations.wifi;

import com.google.inject.Inject;
import mongellaz.communication.manager.QueuedCommands;

import java.nio.charset.StandardCharsets;

@SuppressWarnings("ClassCanBeRecord")
public class WifiConfigurator implements WifiConfigurationObserver {

    @Inject
    public WifiConfigurator(QueuedCommands queuedCommands) {
        this.queuedCommands = queuedCommands;
    }

    @Override
    public void update(WifiConfiguration wifiConfiguration) {
        queuedCommands.queueCommand(getStartCommand());
        queuedCommands.queueCommand(extractSsid(wifiConfiguration));
        queuedCommands.queueCommand(extractPassword(wifiConfiguration));
    }

    private byte[] getStartCommand() {
        final byte configWifiCommand = 0x01;
        final byte endMessageCommand = 0x00;
        return new byte[]{configWifiCommand, endMessageCommand};
    }

    private byte[] extractSsid(WifiConfiguration wifiConfiguration) {
        return wifiConfiguration.ssid().getBytes(StandardCharsets.US_ASCII);
    }

    private byte[] extractPassword(WifiConfiguration wifiConfiguration) {
        char[] password = wifiConfiguration.password();
        byte[] passwordBytes = new byte[password.length];
        for (int i = 0; i < password.length; i++) {
            passwordBytes[i] = (byte) password[i];
        }
        return passwordBytes;
    }

    private final QueuedCommands queuedCommands;
}
