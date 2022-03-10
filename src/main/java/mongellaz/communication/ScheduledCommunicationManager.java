package mongellaz.communication;

public interface ScheduledCommunicationManager {
    void setCommunicationManager(CommunicationManager communicationManager);
    void start();
    void shutdown();
}
