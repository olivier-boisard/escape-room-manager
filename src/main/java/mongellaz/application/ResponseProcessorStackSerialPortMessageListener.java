package mongellaz.application;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import mongellaz.commands.ResponseProcessor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ResponseProcessorStackSerialPortMessageListener implements SerialPortMessageListener {

    void addResponseProcessor(ResponseProcessor responseProcessor) {
        responseProcessors.add(responseProcessor);
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
        final byte[] message = event.getReceivedData();
        final int messageDelimiterSize = getMessageDelimiter().length;
        final byte[] trimmedMessage = Arrays.copyOfRange(message, 0, message.length - messageDelimiterSize);
        for (final ResponseProcessor responseProcessor : responseProcessors) {
            responseProcessor.process(trimmedMessage);
        }
    }

    private final List<ResponseProcessor> responseProcessors = new LinkedList<>();
}
