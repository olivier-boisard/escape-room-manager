package mongellaz.communication;

@FunctionalInterface
public interface ByteArrayReader {
    byte[] read() throws CommunicationException;
}
