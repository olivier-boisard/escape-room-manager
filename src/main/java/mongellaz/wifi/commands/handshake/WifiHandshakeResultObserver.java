package mongellaz.wifi.commands.handshake;

@FunctionalInterface
public interface WifiHandshakeResultObserver {
    void update(WifiHandshakeResult wifiHandshakeResult);
}
