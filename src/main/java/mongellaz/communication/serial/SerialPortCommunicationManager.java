package mongellaz.communication.serial;

import com.fazecast.jSerialComm.SerialPortMessageListener;
import com.google.inject.Inject;
import mongellaz.communication.manager.ScheduledQueuedCommandSender;

@SuppressWarnings("ClassCanBeRecord")
public class SerialPortCommunicationManager {

    @Inject
    SerialPortCommunicationManager(ScheduledQueuedCommandSender scheduledQueuedCommandSender, SerialPortMessageListener serialPortMessageListener) {
        this.scheduledQueuedCommandSender = scheduledQueuedCommandSender;
        this.serialPortMessageListener = serialPortMessageListener;
    }

    public ScheduledQueuedCommandSender getScheduledQueuedCommandSender() {
        return scheduledQueuedCommandSender;
    }

    public SerialPortMessageListener getSerialPortMessageListener() {
        return serialPortMessageListener;
    }

    private final ScheduledQueuedCommandSender scheduledQueuedCommandSender;
    private final SerialPortMessageListener serialPortMessageListener;
}
