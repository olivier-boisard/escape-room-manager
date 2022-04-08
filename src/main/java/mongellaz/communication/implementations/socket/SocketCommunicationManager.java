package mongellaz.communication.implementations.socket;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.manager.ScheduledQueuedCommandSender;

@SuppressWarnings("ClassCanBeRecord")
public class SocketCommunicationManager {
    @Inject
    SocketCommunicationManager(
            ScheduledQueuedCommandSender scheduledQueuedCommandSender,
            @Named("SocketCommunicationManagerReceivedMessageObserver") ByteArrayObserver receivedMessageObserver
    ) {
        this.scheduledQueuedCommandSender = scheduledQueuedCommandSender;
        this.receivedMessageObserver = receivedMessageObserver;
    }

    public final ScheduledQueuedCommandSender scheduledQueuedCommandSender;
    public final ByteArrayObserver receivedMessageObserver;
}
