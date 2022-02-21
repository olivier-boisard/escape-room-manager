package mongellaz.communication.tests;

import mongellaz.commands.HandshakeFactory;

public class HandshakeTest {
    public static void main(String[] args) {
        HandshakeFactory handshakeFactory = new HandshakeFactory();
        new SerialCommunicationTest(handshakeFactory).run();
    }
}
