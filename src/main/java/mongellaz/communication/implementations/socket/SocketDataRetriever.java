package mongellaz.communication.implementations.socket;

import mongellaz.communication.ByteArrayObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class SocketDataRetriever {

    public SocketDataRetriever(Socket socket, ByteArrayObserver byteArrayObserver, Object mutex) {
        this.socket = socket;
        this.byteArrayObserver = byteArrayObserver;
        this.mutex = mutex;
    }

    public void loop() {
        synchronized (mutex) {
            if (socket != null) {
                try {
                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                    int bufferSize = 256;
                    final byte[] buffer = new byte[bufferSize];
                    int totalReadBytes = 0;
                    do {
                        int readBytes = dataInputStream.read(buffer, totalReadBytes, buffer.length - totalReadBytes);
                        if (readBytes < 0) {
                            logger.warn("Reached end of stream");
                        }
                        totalReadBytes += readBytes;
                    } while (buffer[totalReadBytes - 1] != MESSAGE_END_CODE);
                    dispatchReadData(buffer, totalReadBytes);
                } catch (IOException e) {
                    logger.error("Could not get socket input stream: {}", e.getMessage());
                }
            }
        }
    }

    public void setSocket(Socket socket) {
        synchronized (mutex) {
            this.socket = socket;
        }
    }

    private void dispatchReadData(byte[] buffer, int totalReadBytes) {
        logger.info("Dispatching socket message");
        if (totalReadBytes > 0 && buffer[totalReadBytes - 1] == MESSAGE_END_CODE) {
            byteArrayObserver.update(Arrays.copyOfRange(buffer, 0, totalReadBytes - 1));
        }
    }

    private Socket socket;
    private final ByteArrayObserver byteArrayObserver;
    private final Object mutex;
    private final Logger logger = LogManager.getLogger();
    private static final byte MESSAGE_END_CODE = 0x00;
}
