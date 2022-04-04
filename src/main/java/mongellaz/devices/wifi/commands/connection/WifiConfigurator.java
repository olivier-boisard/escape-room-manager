package mongellaz.devices.wifi.commands.connection;

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
        return new byte[]{configWifiCommand};
    }

    private byte[] extractSsid(WifiConfiguration wifiConfiguration) {
        byte[] ssid = wifiConfiguration.ssid().getBytes(StandardCharsets.US_ASCII);
        byte[] outputBuffer = new byte[ssid.length + 1];
        System.arraycopy(ssid, 0, outputBuffer, 0, ssid.length);
        outputBuffer[outputBuffer.length - 1] = END_MESSAGE;
        return outputBuffer;
    }

    private byte[] extractPassword(WifiConfiguration wifiConfiguration) {
        char[] password = wifiConfiguration.password();
        byte[] passwordBytes = new byte[password.length + 1];
        for (int i = 0; i < password.length; i++) {
            passwordBytes[i] = (byte) password[i];
        }
        passwordBytes[passwordBytes.length - 1] = END_MESSAGE;
        return passwordBytes;
    }

    private final QueuedCommands queuedCommands;
    private static final byte END_MESSAGE = 0x00;
}
