package mongellaz.communication;

public interface CommunicationManager extends AutoCloseable {
    void write(byte[] data) throws CommunicationException;

    byte[] read() throws CommunicationException;
}
