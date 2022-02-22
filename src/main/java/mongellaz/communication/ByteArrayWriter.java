package mongellaz.communication;

@FunctionalInterface
public interface ByteArrayWriter {
    void write(byte[] data) throws CommunicationException;
}
