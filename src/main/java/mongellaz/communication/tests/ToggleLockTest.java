package mongellaz.communication.tests;

import mongellaz.commands.ToggleLockCommandFactory;

public class ToggleLockTest {
    public static void main(String[] args) {
        ToggleLockCommandFactory handshakeFactory = new ToggleLockCommandFactory();
        new SerialCommunicationTest(handshakeFactory).run();
    }
}
