package mongellaz.communication.implementations.socket;

import com.google.inject.Inject;
import mongellaz.communication.DeviceController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SocketConnector implements SocketObserver {

    @Inject
    SocketConnector(SocketCommunicationManager socketCommunicationManager, DeviceController deviceController) {
        this.socketCommunicationManager = socketCommunicationManager;
        this.deviceController = deviceController;
        socketQueuedCommandSender = new SocketQueuedCommandSender();
        isRunning = false;
    }

    @Override
    public void update(Socket socket) {
        if (!isRunning) {
            logger.info("Starting socket connector");
            startReader(socket);
            updateCommandSender(socket);
            startDeviceController();
            isRunning = true;
        } else {
            logger.info("Updating socket");
            socketQueuedCommandSender.update(socket);
        }
    }

    public void shutdown() {
        dataReaderExecutorService.shutdown();
    }

    private void startReader(Socket socket) {
        int initialDelayMs = 0;
        int rateMs = 100;
        dataReaderExecutorService.scheduleAtFixedRate(
                (new SocketDataRetriever(socket, socketCommunicationManager.receivedMessageObserver))::loop,
                initialDelayMs,
                rateMs,
                TimeUnit.MILLISECONDS
        );
    }

    private void updateCommandSender(Socket socket) {
        socketQueuedCommandSender.update(socket);
        socketCommunicationManager.scheduledQueuedCommandSender.updateQueuedCommandSender(socketQueuedCommandSender);
    }

    private void startDeviceController() {
        deviceController.start();
    }

    private final SocketQueuedCommandSender socketQueuedCommandSender;
    private final SocketCommunicationManager socketCommunicationManager;
    private final DeviceController deviceController;
    private boolean isRunning;
    private final Logger logger = LogManager.getLogger();
    private final ScheduledExecutorService dataReaderExecutorService = Executors.newSingleThreadScheduledExecutor();

}
