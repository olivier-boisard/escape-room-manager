package mongellaz.communication.implementations.socket;

import com.google.inject.Inject;
import mongellaz.communication.DeviceController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;

public class SocketConnector implements SocketObserver {

    @Inject
    SocketConnector(SocketCommunicationManager socketCommunicationManager, DeviceController deviceController) {
        this.socketCommunicationManager = socketCommunicationManager;
        this.deviceController = deviceController;
    }

    @Override
    public void update(Socket socket) {
        //TODO start thread for listening to socket incoming data
        logger.info("Starting socket connector");
        socketCommunicationManager.scheduledQueuedCommandSender.updateQueuedCommandSender(new SocketQueuedCommandSender(socket));
        deviceController.start();
    }

    private final SocketCommunicationManager socketCommunicationManager;
    private final DeviceController deviceController;
    private final Logger logger = LogManager.getLogger();
}
