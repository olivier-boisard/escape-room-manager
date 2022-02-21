package mongellaz.communication.tests;

import mongellaz.commands.HandshakeFactory;
import mongellaz.commands.ResponseProcessor;
import mongellaz.communication.CommunicationException;
import mongellaz.communication.SerialCommunicationManager;

public class HandshakeTest {
    public static void main(String[] args) {
        SerialCommunicationManager serialCommunicationManager = new SerialCommunicationManager();
        HandshakeFactory handshakeFactory = new HandshakeFactory();
        ResponseProcessor responseProcessor = new ResponseProcessor();

        try {
            serialCommunicationManager.initialize();

            final int nChecks = 10;
            for (int i = 0; i < nChecks; i++) {
                serialCommunicationManager.write(handshakeFactory.generate());
                byte[] response = serialCommunicationManager.read();
                responseProcessor.process(response);
            }
        } catch (CommunicationException e) {
            e.printStackTrace();
        } finally {
            serialCommunicationManager.close();
        }
    }
}
