package mongellaz.communication.handshake;

@FunctionalInterface
public interface HandshakeResultObserver {
    void update(HandshakeResult handshakeResult);
}
