package mongellaz.communication.manager;

public interface ScheduledQueuedCommandSender extends QueuedCommands {
    void updateQueuedCommandSender(QueuedCommandSender queuedCommandSender);
    void start();
    void shutdown();
}
