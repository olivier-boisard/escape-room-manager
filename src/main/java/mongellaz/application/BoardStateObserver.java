package mongellaz.application;

import mongellaz.commands.ConfigurationModeStateObserver;
import mongellaz.commands.HandshakeResultObserver;
import mongellaz.commands.LockStateObserver;
import mongellaz.commands.PiccReaderStatusesObserver;

public interface BoardStateObserver {
    void addHandshakeResultObserver(HandshakeResultObserver handshakeResultObserver);
    void addLockStateObserver(LockStateObserver lockStateObserver);
    void addConfigurationModeStateObserver(ConfigurationModeStateObserver configurationModeStateObserver);
    void addPiccReaderStatusesObserver(PiccReaderStatusesObserver piccReaderStatusesObserver);
}
