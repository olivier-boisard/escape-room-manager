package mongellaz.application.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import mongellaz.communication.manager.QueuedCommands;
import mongellaz.communication.manager.ScheduledExecutorQueuedCommandSender;
import mongellaz.communication.manager.ScheduledQueuedCommandSender;

public class ScheduledExecutorQueuedCommandSenderModule extends AbstractModule  {
    @Override
    protected void configure() {
        bind(ScheduledExecutorQueuedCommandSender.class).in(Singleton.class);
        bind(ScheduledQueuedCommandSender.class).to(ScheduledExecutorQueuedCommandSender.class);
        bind(QueuedCommands.class).to(ScheduledExecutorQueuedCommandSender.class);
    }
}
