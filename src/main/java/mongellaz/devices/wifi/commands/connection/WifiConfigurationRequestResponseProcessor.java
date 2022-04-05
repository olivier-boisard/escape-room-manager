package mongellaz.devices.wifi.commands.connection;

import com.google.inject.Inject;
import mongellaz.communication.ByteArrayObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WifiConfigurationRequestResponseProcessor implements ByteArrayObserver {

    @Inject
    WifiConfigurationRequestResponseProcessor(ConnectionStateObserver connectionStateObserver) {
        this.connectionStateObserver = connectionStateObserver;
    }

    @Override
    public void update(byte[] response) {
        final byte expectedCommandCode = 0x32;
        final byte connectionSuccessCode = 0x02;
        final byte connectionFailureCode = 0x01;
        int index = 0;
        if (response[index++] == expectedCommandCode) {
            byte connectionStatus = response[index++];
            if (connectionStatus == connectionSuccessCode) {
                final int ipAddressLength = 4;
                int[] ipAddress = new int[ipAddressLength];
                for (int i = 0; i < ipAddressLength; i++) {
                    ipAddress[i] = response[index++];
                }
                connectionStateObserver.update(new ConnectionState(true, ipAddress));
            } else if (connectionStatus == connectionFailureCode) {
                connectionStateObserver.update(new ConnectionState(false, new int[]{}));
            } else {
                logger.error("Invalid code: {}", connectionStatus);
            }
        } else {
            logger.debug("Ignoring command: {}", response);
        }
    }

    private final ConnectionStateObserver connectionStateObserver;
    private final Logger logger = LogManager.getLogger();
}
