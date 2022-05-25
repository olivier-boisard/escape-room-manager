package mongellaz.mock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BookPuzzleMock {
    public static void main(String[] args) {
        Logger logger = LogManager.getLogger();
        try {
            final int listeningPort = 165;
            final int bufferSize = 256;
            try (final ServerSocket serverSocket = new ServerSocket(listeningPort)) {
                Socket socket = serverSocket.accept();
                //noinspection InfiniteLoopStatement
                while (true) {
                    try (final DataInputStream inputStream = new DataInputStream(socket.getInputStream())) {
                        try (final DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {
                            byte[] inputBuffer = new byte[bufferSize];
                            if (inputStream.read(inputBuffer) > 0) {
                                logger.debug("Received bytes");
                                if (inputBuffer[0] == 16){
                                    outputStream.write(new byte[]{16, -123, -14, -98, -29, 67, 25, -22, -10, 0x00});
                                } else if (inputBuffer[0] == 32) {
                                    outputStream.write(new byte[]{32, 1, 5, 3, 3, 1, 3, 1, 2, 4, 3, 3, 0x00});
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.fatal("Exception thrown: {}", e.getMessage());
        }
    }
}
