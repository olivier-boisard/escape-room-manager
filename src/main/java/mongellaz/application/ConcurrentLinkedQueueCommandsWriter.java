package mongellaz.application;

import mongellaz.communication.ByteArrayWriter;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentLinkedQueueCommandsWriter extends CommandsWriter {

    public ConcurrentLinkedQueueCommandsWriter(ByteArrayWriter commandWriter) {
        super(new ConcurrentLinkedQueue<>(), commandWriter);
    }
}
