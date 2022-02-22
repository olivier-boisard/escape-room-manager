package mongellaz.application;

import mongellaz.commands.handshake.HandshakeFactory;
import mongellaz.commands.handshake.HandshakeResponseProcessor;
import mongellaz.communication.CommunicationException;
import mongellaz.communication.SerialCommunicationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Application {
    public static void main(String[] args) {
        try (SerialCommunicationManager communicationManager = new SerialCommunicationManager()) {
            Thread.sleep(3000);

            CommandCycle handShakeCycle = new CommandCycle(
                    communicationManager,
                    new HandshakeFactory(),
                    new HandshakeResponseProcessor()
            );
            handShakeCycle.run();
        } catch (CommunicationException e) {
            logger.error(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.fatal("Could not run Thread.sleep(): {}", e.getMessage());
        }
    }

    private static final Logger logger = LogManager.getLogger();

}
