package mongellaz.bookpuzzle.commands.togglelock;

@FunctionalInterface
public interface LockStateObserver {
    void update(LockState lockState);
}
