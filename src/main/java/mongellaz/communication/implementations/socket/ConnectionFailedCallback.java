package mongellaz.communication.implementations.socket;

public interface ConnectionFailedCallback {
    void handleFailedConnection(String message);
}
