package mongellaz.communication;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledCommunicationManagerImpl implements ScheduledCommunicationManager {

    @Inject
    ScheduledCommunicationManagerImpl(
            @Named("CommunicationManagerInitialDelayMs") int initialDelayMs,
            @Named("CommunicationManagerRateMs") int rateMs
    ) {
        this.initialDelayMs = initialDelayMs;
        this.rateMs = rateMs;
    }

    @Override
    public void start() {
        commandWriterExecutorService.scheduleAtFixedRate(
                communicationManager::sendNextCommand,
                initialDelayMs,
                rateMs,
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public void shutdown() {
        commandWriterExecutorService.shutdown();
    }

    @Override
    public void setCommunicationManager(CommunicationManager newCommunicationManager) {
        communicationManager = newCommunicationManager;
    }

    final int initialDelayMs;
    final int rateMs;
    private CommunicationManager communicationManager;
    private final ScheduledExecutorService commandWriterExecutorService = Executors.newSingleThreadScheduledExecutor();
}
