package mongellaz.bookpuzzle.commands.handshake;

@FunctionalInterface
public interface BookPuzzleHandshakeResultObserver {
    void update(BookPuzzleHandshakeResult bookPuzzleHandshakeResult);
}
