package mongellaz.bookpuzzle.commands.handshake;

@FunctionalInterface
public interface HandshakeResultObserver {
    void update(HandshakeResult handshakeResult);
}
