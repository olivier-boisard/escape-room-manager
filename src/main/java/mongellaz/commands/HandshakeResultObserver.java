package mongellaz.commands;


import mongellaz.commands.handshake.HandshakeResult;

@FunctionalInterface
public interface HandshakeResultObserver {
    void update(HandshakeResult handshakeResult);
}
