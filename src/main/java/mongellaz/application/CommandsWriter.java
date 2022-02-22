package mongellaz.application;

import mongellaz.communication.ByteArrayWriter;
import mongellaz.communication.CommunicationException;

import java.util.Queue;

public class CommandsWriter {

    public CommandsWriter(Queue<byte[]> commands, ByteArrayWriter commandWriter) {
        this.commands = commands;
        this.commandWriter = commandWriter;
    }

    public void runNextCommand() {
        try {
            byte[] command = commands.poll();
            if (command != null) {
                commandWriter.write(command);
            }
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
    }

    public void addCommand(final byte[] command) {
        commands.add(command);
    }

    private final Queue<byte[]> commands;
    ByteArrayWriter commandWriter;

}
