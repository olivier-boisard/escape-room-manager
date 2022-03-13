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

    public final ScheduledQueuedCommandSender scheduledQueuedCommandSender;
    public final SerialPortMessageListener serialPortMessageListener;
}
