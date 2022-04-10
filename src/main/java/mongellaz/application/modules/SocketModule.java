package mongellaz.application.modules;

import com.google.inject.*;
import com.google.inject.name.Names;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.ByteArrayObserversStack;
import mongellaz.communication.handshake.HandshakeResultObserver;
import mongellaz.communication.implementations.socket.SocketConnectionUi;
import mongellaz.communication.implementations.socket.SocketConnector;
import mongellaz.communication.implementations.socket.SocketObserver;
import mongellaz.communication.manager.QueuedCommands;
import mongellaz.communication.manager.ScheduledExecutorQueuedCommandSender;
import mongellaz.userinterface.ComponentHandler;

public class SocketModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SocketConnectionUi.class).in(Singleton.class);
        bind(SocketConnector.class);
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
}
