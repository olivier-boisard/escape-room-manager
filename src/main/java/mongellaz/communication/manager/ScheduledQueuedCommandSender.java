package mongellaz.communication.manager;

public interface ScheduledQueuedCommandSender {
    void updateQueuedCommandSender(QueuedCommandSender queuedCommandSender);
    void queueCommand(byte[] command);
    void start();
    void shutdown();
}
