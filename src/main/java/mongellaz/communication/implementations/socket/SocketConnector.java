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
    }

    @Override
    public void update(Socket socket) {
        logger.info("Starting socket connector");
        startReader(socket);
        updateCommandSender(socket);
        startDeviceController();
    }

    public void shutdown() {
        dataReaderExecutorService.shutdown();
    }

    private void startReader(Socket socket) {
        int initialDelayMs=0;
        int rateMs = 100;
        dataReaderExecutorService.scheduleAtFixedRate(
                (new SocketDataRetriever(socket, socketCommunicationManager.receivedMessageObserver))::loop,
                initialDelayMs,
                rateMs,
                TimeUnit.MILLISECONDS
        );
    }

    private void updateCommandSender(Socket socket) {
        socketCommunicationManager.scheduledQueuedCommandSender.updateQueuedCommandSender(new SocketQueuedCommandSender(socket));
    }

    private void startDeviceController() {
        deviceController.start();
    }

    private final SocketCommunicationManager socketCommunicationManager;
    private final DeviceController deviceController;
    private final Logger logger = LogManager.getLogger();
    private final ScheduledExecutorService dataReaderExecutorService = Executors.newSingleThreadScheduledExecutor();

}
