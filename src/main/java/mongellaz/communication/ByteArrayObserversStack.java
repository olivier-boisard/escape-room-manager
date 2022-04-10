package mongellaz.communication;

import java.util.LinkedList;
import java.util.List;

public class ByteArrayObserversStack implements ByteArrayObserver {
    public void addByteArrayObserver(ByteArrayObserver byteArrayObserver) {
        byteArrayObservers.add(byteArrayObserver);
    }

    @Override
    public void update(byte[] data) {
        for (ByteArrayObserver byteArrayObserver : byteArrayObservers) {
            byteArrayObserver.update(data);
        }
    }

    private final List<ByteArrayObserver> byteArrayObservers = new LinkedList<>();
}
