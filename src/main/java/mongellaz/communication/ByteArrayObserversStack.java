package mongellaz.communication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

public class ByteArrayObserversStack implements ByteArrayObserver {
    public void addByteArrayObserver(ByteArrayObserver byteArrayObserver) {
        byteArrayObservers.add(byteArrayObserver);
    }

    @Override
    public void update(byte[] data) {
        logger.debug("Received data: {}", data);
        for (ByteArrayObserver byteArrayObserver : byteArrayObservers) {
            byteArrayObserver.update(data);
        }
    }

    private final Logger logger= LogManager.getLogger();
    private final List<ByteArrayObserver> byteArrayObservers = new LinkedList<>();
}
