package mongellaz.application;

import mongellaz.communication.ByteArrayObserver;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ByteArrayConsumer {

    public ByteArrayConsumer(ByteArrayObserver commandWriter) {
        this.commandWriter = commandWriter;
    }

    public void runNextCommand() {
        byte[] command = commands.poll();
        if (command != null) {
            commandWriter.update(command);
        }
    }

    public void addCommand(final byte[] command) {
        commands.add(command);
    }

    private final Queue<byte[]> commands = new ConcurrentLinkedQueue<>();
    ByteArrayObserver commandWriter;
}
