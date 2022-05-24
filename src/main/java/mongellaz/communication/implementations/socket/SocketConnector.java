package mongellaz.communication.implementations.socket;

import com.google.inject.Inject;
import mongellaz.communication.DeviceController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SocketConnector implements SocketObserver {

    private SocketDataRetriever socketDataRetriever;

    @Inject
    SocketConnector(SocketCommunicationManager socketCommunicationManager, DeviceController deviceController) {
        this.socketCommunicationManager = socketCommunicationManager;
        this.deviceController = deviceController;
        socketQueuedCommandSender = new SocketQueuedCommandSender(mutex);
    }

    @Override
    public void update(Socket socket) {
        if (currentSocket == null) {
            logger.info("Starting socket connector");
            startReader(socket);
            updateCommandSender(socket);
            startDeviceController();
            currentSocket = socket;
        } else {
            logger.info("Updating socket");
            try {
                currentSocket.close();
            } catch (IOException e) {
                logger.warn("Could not close previous socket: {}. Going forward with connection", e.getMessage());
            }
            currentSocket = socket;
            socketQueuedCommandSender.setSocket(socket);
            socketDataRetriever.setSocket(socket);
        }
    }

    public void shutdown() {
        logger.debug("Shutdown");
        dataReaderExecutorService.shutdown();
    }

    private void startReader(Socket socket) {
        logger.info("Start service for reader");
        int initialDelayMs = 0;
        int delayMs = 100;
        socketDataRetriever = new SocketDataRetriever(socket, socketCommunicationManager.receivedMessageObserver, mutex);
        dataReaderExecutorService.scheduleWithFixedDelay(
                socketDataRetriever::loop,
                initialDelayMs,
                delayMs,
                TimeUnit.MILLISECONDS
        );
    }

    private void updateCommandSender(Socket socket) {
        logger.debug("Update command sender");
        socketQueuedCommandSender.setSocket(socket);
        socketCommunicationManager.scheduledQueuedCommandSender.updateQueuedCommandSender(socketQueuedCommandSender);
    }

    private void startDeviceController() {
        logger.info("Start device controller");
        deviceController.start();
    }

    private Socket currentSocket = null;
    private final SocketQueuedCommandSender socketQueuedCommandSender;
    private final SocketCommunicationManager socketCommunicationManager;
    private final DeviceController deviceController;
    private final Object mutex = new Object();
    private final Logger logger = LogManager.getLogger();
    private final ScheduledExecutorService dataReaderExecutorService = Executors.newSingleThreadScheduledExecutor();

}
