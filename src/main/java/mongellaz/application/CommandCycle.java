package mongellaz.application;

import mongellaz.commands.ByteArrayFactory;
import mongellaz.communication.CommunicationException;
import mongellaz.communication.CommunicationManager;
import mongellaz.commands.ResponseProcessor;

@SuppressWarnings("ClassCanBeRecord")
public final class CommandCycle {
    public CommandCycle(
            CommunicationManager communicationManager,
            ByteArrayFactory commandFactory,
            ResponseProcessor responseProcessor
    ) {
        this.communicationManager = communicationManager;
        this.commandFactory = commandFactory;
        this.responseProcessor = responseProcessor;
    }

    public void run() throws CommunicationException {
        communicationManager.write(commandFactory.generate());
        final byte[] response = communicationManager.read();
        responseProcessor.process(response);
    }

    private final CommunicationManager communicationManager;
    private final ByteArrayFactory commandFactory;
    private final ResponseProcessor responseProcessor;

}
