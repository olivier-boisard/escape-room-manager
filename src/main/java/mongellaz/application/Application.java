package mongellaz.application;

import mongellaz.commands.handshake.HandshakeFactory;
import mongellaz.commands.statusrequest.StatusRequestFactory;
import mongellaz.communication.CommunicationException;
import mongellaz.communication.SerialCommunicationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Application {
    public static void main(String[] args) {
        HandshakeFactory handshakeFactory = new HandshakeFactory();
        StatusRequestFactory statusRequestFactory = new StatusRequestFactory();
        ScheduledExecutorService statusRequestExecutorService = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService commandWriterExecutorService = Executors.newSingleThreadScheduledExecutor();

        try (SerialCommunicationManager communicationManager = new SerialCommunicationManager()) {
            Thread.sleep(3000);

            // Start command writer thread
            CommandsWriter commandsWriter = new ConcurrentLinkedQueueCommandsWriter(communicationManager);
            commandWriterExecutorService.scheduleAtFixedRate(
                    commandsWriter::runNextCommand,
                    0,
                    100,
                    TimeUnit.MILLISECONDS
            );

            // Run handshake
            commandsWriter.addCommand(handshakeFactory.generate());

            // Start status request thread
            statusRequestExecutorService.scheduleAtFixedRate(
                    () -> commandsWriter.addCommand(statusRequestFactory.generate()),
                    0,
                    1,
                    TimeUnit.SECONDS
            );

            // TODO start thread for reading serial port

            //noinspection ResultOfMethodCallIgnored
            commandWriterExecutorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (CommunicationException e) {
            logger.error(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.fatal("Could not run Thread.sleep(): {}", e.getMessage());
        } finally {
            statusRequestExecutorService.shutdown();
            commandWriterExecutorService.shutdown();
        }
    }

    private static final Logger logger = LogManager.getLogger();

}
