package mongellaz.commands;

import mongellaz.commands.togglelock.LockState;

@FunctionalInterface
public interface LockStateObserver {
    void update(LockState lockState);
}
