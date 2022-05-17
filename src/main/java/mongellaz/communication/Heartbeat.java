package mongellaz.communication;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import mongellaz.communication.implementations.socket.ConnectionFailedCallback;
import mongellaz.communication.implementations.socket.SocketObserver;
import mongellaz.communication.manager.QueuedCommands;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Heartbeat implements ByteArrayObserver, SocketObserver {

    @Inject
    Heartbeat(
            QueuedCommands queuedCommands,
            ByteArrayGenerator byteArrayGenerator,
            @Named("HeartbeatTimeoutCallback") SocketObserver timeoutCallback,
            ConnectionFailedCallback connectionFailedCallback
    ) {
        this.queuedCommands = queuedCommands;
        this.byteArrayGenerator = byteArrayGenerator;
        this.timeoutCallback = timeoutCallback;
        this.connectionFailedCallback = connectionFailedCallback;
        initialDelayMs = 3000;
        rateMs = 1000;
        timeOutMs = 3000;
        lastReceivedMessageTimeMs = -1;
    }

    @Override
    public void update(Socket socket) {
        hostName = socket.getInetAddress().getHostName();
        port = socket.getPort();
        startCommandSendingThread();
        startResultsListeningThread();
    }

    public void shutdown() {
        messageSendingExecutorService.shutdown();
        watchDogExecutorService.shutdown();
    }

    @Override
    public void update(byte[] data) {
        lastReceivedMessageTimeMs = System.currentTimeMillis();
    }

    private void startCommandSendingThread() {
        messageSendingExecutorService.scheduleAtFixedRate(
                () -> queuedCommands.queueCommand(byteArrayGenerator.generate()),
                initialDelayMs,
                rateMs,
                TimeUnit.MILLISECONDS
        );
    }

    private void startResultsListeningThread() {
        watchDogExecutorService.scheduleAtFixedRate(
                () -> {
                    try {
                        if (System.currentTimeMillis() - lastReceivedMessageTimeMs >= timeOutMs) {
                            if (hostName == null || port == 0) {
                                logger.warn("No socket address set");
                            } else {
                                logger.info("Resetting connection");
                                timeoutCallback.update(new Socket(hostName, port));
                            }
                        }
                    } catch (IOException e) {
                        connectionFailedCallback.handleFailedConnection(e.getMessage());
                    }
                },
                initialDelayMs * 3L,
                rateMs * 3L,
                TimeUnit.MILLISECONDS
        );
    }


    private String hostName;
    private int port;
    private final SocketObserver timeoutCallback;
    private final QueuedCommands queuedCommands;
    private final ConnectionFailedCallback connectionFailedCallback;
    private final ByteArrayGenerator byteArrayGenerator;
    private final int initialDelayMs;
    private final int rateMs;
    private final int timeOutMs;
    private long lastReceivedMessageTimeMs;
    private final ScheduledExecutorService messageSendingExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService watchDogExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final Logger logger = LogManager.getLogger();
}
