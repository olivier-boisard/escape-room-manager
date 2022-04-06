package mongellaz.application.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import mongellaz.communication.handshake.HandshakeResultObserver;
import mongellaz.communication.implementations.socket.SocketConnectionUi;
import mongellaz.userinterface.ComponentHandler;

public class SocketModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SocketConnectionUi.class).in(Singleton.class);
        bind(ComponentHandler.class).annotatedWith(Names.named("ConnectionUi")).to(SocketConnectionUi.class);
        bind(HandshakeResultObserver.class).to(SocketConnectionUi.class);
    }
}
