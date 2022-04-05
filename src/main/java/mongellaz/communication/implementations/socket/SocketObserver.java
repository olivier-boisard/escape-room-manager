package mongellaz.communication.implementations.socket;

import java.net.Socket;

public interface SocketObserver {
    void update(Socket socket);
}
