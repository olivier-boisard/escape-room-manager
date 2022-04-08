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
    public SocketQueuedCommandSender(Socket socket) {
        this.socket = socket;
    }

    private final Socket socket;

    @Override
    public void sendNextCommand() {
        byte[] command = commands.poll();
        if (command != null) {
            try {
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.write(command);
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
        if (!socket.isClosed()) {
            try {
                socket.close();
                logger.info("Closed socket");
            } catch (IOException e) {
                logger.error("Could not close socket: {}", e.getMessage());
            }
        }
    }

    private final Queue<byte[]> commands = new ConcurrentLinkedQueue<>();
    private final Logger logger = LogManager.getLogger();
}
