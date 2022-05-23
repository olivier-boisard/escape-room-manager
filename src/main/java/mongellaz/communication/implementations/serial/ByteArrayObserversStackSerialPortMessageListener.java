package mongellaz.communication.implementations.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import mongellaz.communication.ByteArrayObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ByteArrayObserversStackSerialPortMessageListener implements SerialPortMessageListener {

    public void addByteArrayObserver(ByteArrayObserver byteArrayObserver) {
        byteArrayObservers.add(byteArrayObserver);
    }

    @Override
    public byte[] getMessageDelimiter() {
        return new byte[]{0x00};
    }

    @Override
    public boolean delimiterIndicatesEndOfMessage() {
        return true;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        logger.debug("Received serial event");
        final byte[] message = event.getReceivedData();
        final int messageDelimiterSize = getMessageDelimiter().length;
        final byte[] trimmedMessage = Arrays.copyOfRange(message, 0, message.length - messageDelimiterSize);
        for (final ByteArrayObserver byteArrayObserver : byteArrayObservers) {
            byteArrayObserver.update(trimmedMessage);
        }
    }

    private final List<ByteArrayObserver> byteArrayObservers = new LinkedList<>();
    private final Logger logger= LogManager.getLogger();
}
