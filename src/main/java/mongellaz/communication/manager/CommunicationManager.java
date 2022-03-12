package mongellaz.communication.manager;

public interface CommunicationManager {
    void sendNextCommand();
    void queueCommand(byte[] command);
    void shutdown();
}
