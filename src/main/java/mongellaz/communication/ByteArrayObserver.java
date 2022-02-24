package mongellaz.communication;

@FunctionalInterface
public interface ByteArrayObserver {
    void update(byte[] data);
}
