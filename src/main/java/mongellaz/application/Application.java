package mongellaz.application;

import mongellaz.commands.HandshakeFactory;
import mongellaz.communication.CommunicationException;
import mongellaz.communication.SerialCommunicationManager;
import mongellaz.communication.tests.ResponseProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class Application {
    public static void main(String[] args) {
        try (SerialCommunicationManager communicationManager = new SerialCommunicationManager()) {
            Thread.sleep(3000);
            // Handshake
            HandshakeFactory handshakeFactory = new HandshakeFactory();
            communicationManager.write(handshakeFactory.generate());
            byte[] response= communicationManager.read();
            new HandshakeResponseProcessor().process(response);
        } catch (CommunicationException e) {
            logger.error(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.fatal("Could not run Thread.sleep(): {}", e.getMessage());
        }
    }

    private static final Logger logger = LogManager.getLogger();

    private static class HandshakeResponseProcessor implements ResponseProcessor {
        @Override
        public void process(final byte[] response) throws CommunicationException {
            final byte[] expectedResponse = {16, -123, -14, -98, -29, 67, 25, -22, -10};
            if (Arrays.equals(response, expectedResponse)) {
                logger.info("Received expected response");
            } else {
                throw new CommunicationException("Invalid response: " + Arrays.toString(response));
            }
        }

        private final Logger logger = LogManager.getLogger();
    }
}
