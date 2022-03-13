package mongellaz.communication.manager;

public interface QueuedCommandSender {
    void sendNextCommand();
    void queueCommand(byte[] command);
    void shutdown();
}
