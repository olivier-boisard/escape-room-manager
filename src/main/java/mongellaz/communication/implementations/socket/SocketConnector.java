package mongellaz.communication.implementations.socket;

import java.net.Socket;

public class SocketConnector implements SocketObserver{
    @Override
    public void update(Socket socket) {
        //TODO start thread for listening to socket incoming data
        //TODO start thread for managing queued commands
    }
}
