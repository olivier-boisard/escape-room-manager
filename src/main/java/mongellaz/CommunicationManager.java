package mongellaz;

public interface CommunicationManager {
    void write(byte[] data) throws CommunicationException;

    byte[] read() throws CommunicationException;
}
