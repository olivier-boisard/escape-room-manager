package mongellaz.communication.manager;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorQueuedCommandSender implements ScheduledQueuedCommandSender {

    @Inject
    ScheduledExecutorQueuedCommandSender(
            @Named("CommunicationManagerInitialDelayMs") int initialDelayMs,
            @Named("CommunicationManagerDelayMs") int delayMs
    ) {
        this.initialDelayMs = initialDelayMs;
        this.delayMs = delayMs;
    }

    @Override
    public void queueCommand(byte[] command) {
        logger.debug("Queueing command: {}", command);
        queuedCommandSender.queueCommand(command);
    }

    @Override
    public void start() {
        logger.info("Starting execution service");
        commandWriterExecutorService.scheduleWithFixedDelay(
                queuedCommandSender::sendNextCommand,
                initialDelayMs,
                delayMs,
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public void close() {
        logger.info("Shutting down resources");
        commandWriterExecutorService.shutdown();
        if (queuedCommandSender != null) {
            queuedCommandSender.shutdown();
        }
    }

    @Override
    public void updateQueuedCommandSender(QueuedCommandSender queuedCommandSender) {
        logger.debug("Received new queued command sender");
        this.queuedCommandSender = queuedCommandSender;
        start();
    }

    final int initialDelayMs;
    final int delayMs;
    private QueuedCommandSender queuedCommandSender;
    private final ScheduledExecutorService commandWriterExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final Logger logger = LogManager.getLogger();
}
