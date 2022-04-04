package mongellaz.devices.wifi.commands.connection;

public interface ConnectionStateObserver {
    void update(ConnectionState connectionState);
}
