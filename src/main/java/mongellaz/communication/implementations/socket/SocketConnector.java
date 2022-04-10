package mongellaz.communication.implementations.socket;

import com.google.inject.Inject;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.DeviceController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
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

    public static class SocketDataRetriever {

        public SocketDataRetriever(Socket socket, ByteArrayObserver byteArrayObserver) {
            this.socket = socket;
            this.byteArrayObserver = byteArrayObserver;
        }

        public void loop() {
            if (socket != null) {
                try {
                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                    int bufferSize = 256;
                    final byte[] buffer = new byte[bufferSize];
                    int totalReadBytes = 0;
                    do {
                        int readBytes = dataInputStream.read(buffer, totalReadBytes, buffer.length - totalReadBytes);
                        if (readBytes < 0) {
                            logger.warn("Reached end of stream");
                        }
                        totalReadBytes += readBytes;
                        dispatchReadData(buffer, totalReadBytes);
                    } while (buffer[totalReadBytes - 1] != MESSAGE_END_CODE);
                } catch (IOException e) {
                    logger.error("Could not get socket input stream: {}", e.getMessage());
                }
            }
        }

        private void dispatchReadData(byte[] buffer, int totalReadBytes) {
            logger.info("Dispatching socket message");
            if (totalReadBytes > 0 && buffer[totalReadBytes - 1] == MESSAGE_END_CODE) {
                byteArrayObserver.update(Arrays.copyOfRange(buffer, 0, totalReadBytes - 1));
            }
        }


        private final Socket socket;
        private final ByteArrayObserver byteArrayObserver;
        private final Logger logger = LogManager.getLogger();
        private static final byte MESSAGE_END_CODE = 0x00;
    }
}
