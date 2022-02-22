package mongellaz.application;

import com.fazecast.jSerialComm.SerialPort;
import mongellaz.commands.ResponseProcessor;
import mongellaz.communication.CommunicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class ArduinoSerialPortMessageProcessor implements Runnable {

    public ArduinoSerialPortMessageProcessor(SerialPort serialPort, List<ResponseProcessor> responseProcessors) {
        this.serialPort = serialPort;
        this.responseProcessors = responseProcessors;
    }

    public void addResponseProcessor(ResponseProcessor responseProcessor) {
        responseProcessors.add(responseProcessor);
    }

    @Override
    public void run() {
        try {
            final byte[] command = readCommand();
            for (final ResponseProcessor responseProcessor : responseProcessors) {
                responseProcessor.process(command);
            }
        } catch (CommunicationException e) {
            logger.error(e.getMessage());
        }
    }

    private byte[] readCommand() throws CommunicationException {
        byte[] responseBuffer = new byte[INPUT_BUFFER_SIZE];
        int totalReadBytes = 0;
        byte lastReadByte = 0x00;

        // Read data from serial port
        do {
            final int bufferSize = 128;
            byte[] readBuffer = new byte[bufferSize];
            int readBytesInOnce = serialPort.readBytes(readBuffer, serialPort.bytesAvailable());

            if (readBytesInOnce > 0) {
                lastReadByte = readBuffer[readBytesInOnce - 1];
                System.arraycopy(readBuffer, 0, responseBuffer, totalReadBytes, readBytesInOnce);
                totalReadBytes += readBytesInOnce;
            }
        } while (continueRead(totalReadBytes, lastReadByte));
        if (!isReadSuccessful(lastReadByte)) {
            throw new CommunicationException("Error while reading message from serial port");
        }

        // Extract command
        return Arrays.copyOfRange(responseBuffer, 0, totalReadBytes - 1);
    }

    private boolean continueRead(int totalReadBytes, byte lastReadByte) {
        final boolean bufferNotFilled = totalReadBytes < INPUT_BUFFER_SIZE;
        final boolean readZeroBytes = totalReadBytes == 0;
        return bufferNotFilled && (isNotTerminationByte(lastReadByte) || readZeroBytes);
    }

    private boolean isReadSuccessful(byte lastReadByte) {
        return !isNotTerminationByte(lastReadByte);
    }

    private boolean isNotTerminationByte(byte b) {
        final byte terminationByte = 0x00;
        return b != terminationByte;
    }

    private final SerialPort serialPort;
    private final List<ResponseProcessor> responseProcessors;
    private final Logger logger = LogManager.getLogger();
    private static final int INPUT_BUFFER_SIZE = 256;
}
