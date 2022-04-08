package mongellaz.application.modules;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import mongellaz.communication.manager.QueuedCommands;
import mongellaz.communication.manager.ScheduledExecutorQueuedCommandSender;
import mongellaz.communication.manager.ScheduledQueuedCommandSender;

public class ScheduledExecutorQueuedCommandSenderModule extends AbstractModule  {
    @Override
    protected void configure() {
        bindConstant().annotatedWith(Names.named("CommunicationManagerInitialDelayMs")).to(5000);
        bindConstant().annotatedWith(Names.named("CommunicationManagerRateMs")).to(100);
        bind(ScheduledQueuedCommandSender.class).to(ScheduledExecutorQueuedCommandSender.class);
        bind(QueuedCommands.class).to(ScheduledExecutorQueuedCommandSender.class);
    }
}
