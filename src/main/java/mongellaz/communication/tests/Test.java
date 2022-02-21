package mongellaz.communication.tests;

import mongellaz.commands.ByteArrayFactory;
import mongellaz.communication.CommunicationException;
import mongellaz.communication.CommunicationManager;

public record Test(ByteArrayFactory commandFactory, CommunicationManager communicationManager) {

    public void run() throws CommunicationException {
        ResponseProcessor responseProcessor = new ResponseProcessor();
        final int nChecks = 10;
        for (int i = 0; i < nChecks; i++) {
            communicationManager.write(commandFactory.generate());
            byte[] response = communicationManager.read();
            responseProcessor.process(response);
        }
    }

}
