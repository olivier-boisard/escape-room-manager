package mongellaz.devices.bookpuzzle.commands.togglelock;

@FunctionalInterface
public interface LockStateObserver {
    void update(LockState lockState);
}
