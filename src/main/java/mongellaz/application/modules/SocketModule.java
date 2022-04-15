package mongellaz.application.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.ByteArrayObserversStack;
import mongellaz.communication.handshake.HandshakeResultObserver;
import mongellaz.communication.implementations.socket.SocketConfigurationHandler;
import mongellaz.communication.implementations.socket.SocketConnectionUi;
import mongellaz.communication.implementations.socket.SocketConnector;
import mongellaz.communication.implementations.socket.SocketObserver;
import mongellaz.communication.manager.QueuedCommands;
import mongellaz.communication.manager.ScheduledExecutorQueuedCommandSender;
import mongellaz.userinterface.ComponentHandler;
import mongellaz.userinterface.PersistentConfigurationHandler;

public class SocketModule extends AbstractModule {
    public SocketModule(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    @Override
    protected void configure() {
        bind(SocketConnector.class);
        bind(SocketConnectionUi.class).in(Singleton.class);
        bind(String.class).annotatedWith(Names.named("PersistentConfigurationHandlerNameSpace")).toInstance(nameSpace);
        bind(SocketConfigurationHandler.class).to(PersistentConfigurationHandler.class);
        bind(ComponentHandler.class).annotatedWith(Names.named("ConnectionUi")).to(SocketConnectionUi.class);
        bind(HandshakeResultObserver.class).to(SocketConnectionUi.class);
        bind(SocketObserver.class).to(SocketConnector.class);
        bind(QueuedCommands.class).to(ScheduledExecutorQueuedCommandSender.class);
        bind(ByteArrayObserver.class)
                .annotatedWith(Names.named("SocketCommunicationManagerReceivedMessageObserver"))
                .toProvider(ByteArrayObserverProvider.class);
    }

    @SuppressWarnings("ClassCanBeRecord")
    private static class ByteArrayObserverProvider implements Provider<ByteArrayObserver> {

        @Inject
        ByteArrayObserverProvider(Iterable<ByteArrayObserver> responseObservers) {
            this.responseObservers = responseObservers;
        }

        @Override
        public ByteArrayObserver get() {
            ByteArrayObserversStack byteArrayObserversStack = new ByteArrayObserversStack();
            for (ByteArrayObserver responseObserver : responseObservers) {
                byteArrayObserversStack.addByteArrayObserver(responseObserver);
            }
            return byteArrayObserversStack;
        }

        private final Iterable<ByteArrayObserver> responseObservers;
    }

    private final String nameSpace;
}
