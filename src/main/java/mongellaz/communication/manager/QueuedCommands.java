package mongellaz.communication.manager;

public interface QueuedCommands {
    void queueCommand(byte[] command);
}
