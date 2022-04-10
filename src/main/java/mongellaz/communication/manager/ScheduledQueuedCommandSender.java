package mongellaz.communication.manager;

import java.io.Closeable;

public interface ScheduledQueuedCommandSender extends QueuedCommands, Closeable {
    void updateQueuedCommandSender(QueuedCommandSender queuedCommandSender);
    void start();
}
