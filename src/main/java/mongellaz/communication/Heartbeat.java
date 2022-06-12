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
        setLastReceivedMessageTimeMs(-1);
    }

    @Override
    public void update(Socket socket) {
        logger.info("Update socket");
        hostName = socket.getInetAddress().getHostName();
        port = socket.getPort();
        startCommandSendingThread();
        startWatchdogThread();
    }

    public void shutdown() {
        logger.info("Shutdown");
        messageSendingExecutorService.shutdown();
        watchDogExecutorService.shutdown();
    }

    @Override
    public void update(byte[] data) {
        logger.debug("Received data: {}", data);
        setLastReceivedMessageTimeMs(System.currentTimeMillis());
    }

    private void startCommandSendingThread() {
        logger.info("Start command sending thread");
        messageSendingExecutorService.scheduleWithFixedDelay(
                () -> queuedCommands.queueCommand(byteArrayGenerator.generate()),
                COMMAND_SENDER_INITIAL_DELAY_MS,
                COMMAND_SENDER_DELAY_MS,
                TimeUnit.MILLISECONDS
        );
    }

    private void startWatchdogThread() {
        logger.info("Start watchdog thread");
        watchDogExecutorService.scheduleWithFixedDelay(
                () -> {
                    try {
                        long now = System.currentTimeMillis();
                        if (now - getLastReceivedMessageTimeMs() >= TIME_OUT_MS) {
                            if (hostName == null || port == 0) {
                                logger.warn("No socket address set");
                            } else {
                                logger.info("Resetting connection");
                                timeoutCallback.update(new Socket(hostName, port));
                                setLastReceivedMessageTimeMs(now);
                            }
                        }
                    } catch (IOException e) {
                        connectionFailedCallback.handleFailedConnection(e.getMessage());
                    }
                },
                WATCHDOG_INITIAL_DELAY_MS,
                WATCHDOG_DELAY_MS,
                TimeUnit.MILLISECONDS
        );
    }

    private void setLastReceivedMessageTimeMs(long lastReceivedMessageTimeMs) {
        synchronized (mutex) {
            this.lastReceivedMessageTimeMs = lastReceivedMessageTimeMs;
        }
    }

    public long getLastReceivedMessageTimeMs() {
        synchronized (mutex) {
            return lastReceivedMessageTimeMs;
        }
    }

    private String hostName;
    private int port;
    private final SocketObserver timeoutCallback;
    private final QueuedCommands queuedCommands;
    private final ConnectionFailedCallback connectionFailedCallback;
    private final ByteArrayGenerator byteArrayGenerator;
    private static final int COMMAND_SENDER_INITIAL_DELAY_MS = 2000;
    private static final int COMMAND_SENDER_DELAY_MS = 1000;
    private static final int TIME_OUT_MS = 3000;
    private static final int WATCHDOG_INITIAL_DELAY_MS = 5000;
    private static final int WATCHDOG_DELAY_MS = 500;
    private long lastReceivedMessageTimeMs;

    private final Object mutex = new Object();
    private final ScheduledExecutorService messageSendingExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService watchDogExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final Logger logger = LogManager.getLogger();
}
