package mongellaz.communication;

public interface CommunicationManager {
    void sendNextCommand();

    void queueCommand(byte[] command);
}
