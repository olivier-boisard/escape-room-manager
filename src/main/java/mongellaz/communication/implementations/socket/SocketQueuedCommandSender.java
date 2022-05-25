package mongellaz.communication.implementations.socket;

import mongellaz.communication.manager.QueuedCommandSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SocketQueuedCommandSender implements QueuedCommandSender {
    public SocketQueuedCommandSender(Object mutex) {
        this.mutex = mutex;
    }

    @Override
    public void sendNextCommand() {
        byte[] command = commands.poll();
        if (command != null) {
            try {
                logger.info("Send command to socket: {}", command);
                synchronized (mutex) {
                    logger.info("Entered mutex-synchronized block");
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataOutputStream.write(command);
                    logger.info("Leaving mutex-synchronized block");
                }
            } catch (IOException e) {
                logger.error("Could not write data: {}", e.getMessage());
            }
        }
    }

    @Override
    public void queueCommand(byte[] command) {
        commands.add(command);
    }

    @Override
    public void shutdown() {
        synchronized (mutex) {
            logger.info("Entered mutex-synchronized block");
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                    logger.info("Closed socket");
                } else {
                    logger.debug("No socket to close");
                }
            } catch (IOException e) {
                logger.error("Could not close socket: {}", e.getMessage());
            }
            logger.info("Leaving mutex-synchronized block");
        }
    }

    public void setSocket(Socket socket) {
        synchronized (mutex) {
            this.socket = socket;
        }
    }

    private Socket socket;
    private final Queue<byte[]> commands = new ConcurrentLinkedQueue<>();

    private final Logger logger = LogManager.getLogger();

    private final Object mutex;
}
