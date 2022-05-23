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
            @Named("HeartBeatByteArrayGenerator") ByteArrayGenerator byteArrayGenerator,
            @Named("HeartBeatNewSocketObserver") SocketObserver timeoutCallback,
            ConnectionFailedCallback connectionFailedCallback
    ) {
        this.queuedCommands = queuedCommands;
        this.byteArrayGenerator = byteArrayGenerator;
        this.timeoutCallback = timeoutCallback;
        this.connectionFailedCallback = connectionFailedCallback;
        lastReceivedMessageTimeMs = -1;
    }

    @Override
    public void update(Socket socket) {
        logger.debug("Update socket");
        hostName = socket.getInetAddress().getHostName();
        port = socket.getPort();
        startCommandSendingThread();
        startWatchdogThread();
    }

    public void shutdown() {
        logger.info("Shut down");
        messageSendingExecutorService.shutdown();
        watchDogExecutorService.shutdown();
    }

    @Override
    public void update(byte[] data) {
        logger.debug("Received data");
        lastReceivedMessageTimeMs = System.currentTimeMillis();
    }

    private void startCommandSendingThread() {
        logger.info("Start command sending thread");
        messageSendingExecutorService.scheduleAtFixedRate(
                () -> queuedCommands.queueCommand(byteArrayGenerator.generate()),
                INITIAL_DELAY_MS,
                RATE_MS,
                TimeUnit.MILLISECONDS
        );
    }

    private void startWatchdogThread() {
        logger.info("Start watchdog thread");
        watchDogExecutorService.scheduleAtFixedRate(
                () -> {
                    try {
                        if (System.currentTimeMillis() - lastReceivedMessageTimeMs >= TIME_OUT_MS) {
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
                INITIAL_DELAY_MS * 3L,
                RATE_MS * 3L,
                TimeUnit.MILLISECONDS
        );
    }


    private String hostName;
    private int port;
    private final SocketObserver timeoutCallback;
    private final QueuedCommands queuedCommands;
    private final ConnectionFailedCallback connectionFailedCallback;
    private final ByteArrayGenerator byteArrayGenerator;
    private static final int INITIAL_DELAY_MS = 5000;
    private static final int RATE_MS = 1000;
    private static final int TIME_OUT_MS = 3000;
    private long lastReceivedMessageTimeMs;
    private final ScheduledExecutorService messageSendingExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService watchDogExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final Logger logger = LogManager.getLogger();
}
