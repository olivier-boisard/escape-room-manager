package mongellaz.commands;

import mongellaz.commands.togglelock.ToggleLockResponseProcessor;

@FunctionalInterface
public interface MagnetStateObserver {
    void update(ToggleLockResponseProcessor.MagnetState magnetState);
}
