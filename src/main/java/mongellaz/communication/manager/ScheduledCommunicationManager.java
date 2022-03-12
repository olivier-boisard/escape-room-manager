package mongellaz.communication.manager;

public interface ScheduledCommunicationManager {
    void updateCommunicationManager(CommunicationManager communicationManager);
    void queueCommand(byte[] command);
    void start();
    void shutdown();
}