package mongellaz.communication.implementations.socket;

import com.google.inject.Inject;
import mongellaz.communication.ByteArrayObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class SocketDataRetriever implements Runnable {

    @Inject
    SocketDataRetriever(Socket socket, List<ByteArrayObserver> byteArrayObservers) {
        this.socket = socket;
        this.byteArrayObservers = byteArrayObservers;
    }

    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            int bufferSize = 256;
            final byte[] buffer = new byte[bufferSize];
            int totalReadBytes = 0;
            while (run) {
                int readBytes = dataInputStream.read(buffer, totalReadBytes, buffer.length - totalReadBytes);
                if (readBytes < 0) {
                    logger.warn("Reached end of stream");
                }
                totalReadBytes += readBytes;
                dispatchReadData(buffer, totalReadBytes);
            }
        } catch (IOException e) {
            logger.error("Could not get socket input stream: {}", e.getMessage());
        }
    }

    public void stop() {
        run = false;
    }

    private void dispatchReadData(byte[] buffer, int totalReadBytes) {
        final byte messageEndCode = 0x00;
        if (totalReadBytes > 0 && buffer[totalReadBytes - 1] == messageEndCode) {
            for (ByteArrayObserver byteArrayObserver : byteArrayObservers) {
                byteArrayObserver.update(Arrays.copyOfRange(buffer, 0, totalReadBytes - 1));
            }
        }
    }

    private final Socket socket;
    private final List<ByteArrayObserver> byteArrayObservers;
    private volatile boolean run = true;
    private final Logger logger = LogManager.getLogger();
}
