package mongellaz.mock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
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
                    final DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    byte[] inputBuffer = new byte[bufferSize];
                    int nReadBytes = inputStream.read(inputBuffer);
                    if (nReadBytes > 0) {
                        logger.debug("Received bytes");
                        final DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                        if (inputBuffer[0] == 16) {
                            outputStream.write(new byte[]{16, -123, -14, -98, -29, 67, 25, -22, -10, 0x00});
                            outputStream.flush();
                            logger.debug("Sent response");
                        } else if (inputBuffer[0] == 32) {
                            outputStream.write(new byte[]{32, 1, 5, 3, 3, 1, 3, 1, 2, 4, 3, 3, 0x00});
                            outputStream.flush();
                            logger.debug("Sent response");
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.fatal("Exception thrown: {}", e.getMessage());
        }
    }
}
