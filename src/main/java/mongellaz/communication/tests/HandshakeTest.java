package mongellaz.communication.tests;

import mongellaz.commands.HandshakeFactory;
import mongellaz.communication.CommunicationException;
import mongellaz.communication.SerialCommunicationManager;

public class HandshakeTest {
    public static void main(String[] args) {
        HandshakeFactory handshakeFactory = new HandshakeFactory();
        SerialCommunicationManager serialCommunicationManager = new SerialCommunicationManager();

        try {
            serialCommunicationManager.initialize();
            new Test(handshakeFactory, serialCommunicationManager).run();
        } catch (CommunicationException e) {
            e.printStackTrace();
        } finally {
            serialCommunicationManager.close();
        }
    }
}
