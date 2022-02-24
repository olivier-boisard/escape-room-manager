package mongellaz.commands;

public interface BookPuzzleDeviceStateObserver extends HandshakeResultObserver,
        LockStateObserver, ConfigurationModeStateObserver, PiccReaderStatusesObserver {
}
