package mongellaz.application;

import mongellaz.commands.handshake.HandshakeFactory;
import mongellaz.commands.handshake.HandshakeResponseProcessor;
import mongellaz.commands.statusrequest.StatusRequestFactory;
import mongellaz.commands.statusrequest.StatusRequestResponseProcessor;
import mongellaz.communication.CommunicationException;
import mongellaz.communication.SerialCommunicationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Application {
    public static void main(String[] args) {
        ScheduledExecutorService statusRequestExecutorService = Executors.newSingleThreadScheduledExecutor();
        try (SerialCommunicationManager communicationManager = new SerialCommunicationManager()) {
            Thread.sleep(3000);

            CommandCycle handShakeCycle = new CommandCycle(
                    communicationManager,
                    new HandshakeFactory(),
                    new HandshakeResponseProcessor()
            );

            CommandCycle statusRequestCycle = new CommandCycle(
                    communicationManager,
                    new StatusRequestFactory(),
                    new StatusRequestResponseProcessor()
            );
            Runnable statusRequestCycleRunner = () -> {
                try {
                    statusRequestCycle.run();
                } catch (CommunicationException e) {
                    logger.error(e.getMessage());
                }
            };
            handShakeCycle.run();
            statusRequestExecutorService.scheduleAtFixedRate(statusRequestCycleRunner, 0, 1, TimeUnit.SECONDS);

            //noinspection ResultOfMethodCallIgnored
            statusRequestExecutorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (CommunicationException e) {
            logger.error(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.fatal("Could not run Thread.sleep(): {}", e.getMessage());
        } finally {
            statusRequestExecutorService.shutdown();
        }
    }

    private static final Logger logger = LogManager.getLogger();

}
