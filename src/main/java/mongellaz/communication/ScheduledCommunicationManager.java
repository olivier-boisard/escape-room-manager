package mongellaz.communication;

public interface ScheduledCommunicationManager {
    void setCommunicationManager(CommunicationManager communicationManager);
    void queueCommand(byte[] command);
    void start();
    void shutdown();
}
