package mongellaz.devices.common.togglelock;

@FunctionalInterface
public interface LockStateObserver {
    void update(LockState lockState);
}
