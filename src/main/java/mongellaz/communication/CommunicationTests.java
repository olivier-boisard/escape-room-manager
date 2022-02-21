package mongellaz.communication;

import mongellaz.commands.HandshakeFactory;
import mongellaz.commands.ResponseProcessor;

public class CommunicationTests {
    public static void main(String[] args) {
        SerialCommunicationManager communicationManager = new SerialCommunicationManager();
        HandshakeFactory handshakeFactory = new HandshakeFactory();
        ResponseProcessor responseProcessor = new ResponseProcessor();

        try {
            communicationManager.initialize();

            final int nChecks = 10;
            for (int i = 0; i < nChecks; i++) {
                communicationManager.write(handshakeFactory.generate());
                byte[] response = communicationManager.read();
                responseProcessor.process(response);
            }
        } catch (CommunicationException e) {
            e.printStackTrace();
        } finally {
            communicationManager.close();
        }
    }
}
