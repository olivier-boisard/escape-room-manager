package mongellaz.modules;

import com.fazecast.jSerialComm.SerialPortMessageListener;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import mongellaz.bookpuzzle.commands.handshake.HandshakeResultObserver;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.manager.ScheduledExecutorQueuedCommandSender;
import mongellaz.communication.manager.ScheduledQueuedCommandSender;
import mongellaz.communication.serial.ByteArrayObserversStackSerialPortMessageListener;
import mongellaz.communication.serial.SerialPortConnector;
import mongellaz.communication.serial.SerialPortObserver;
import mongellaz.communication.serial.SerialPortPuzzleConnectionUi;
import mongellaz.userinterface.ComponentHandler;

public class SerialPortModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SerialPortPuzzleConnectionUi.class).in(Singleton.class);
        bind(ScheduledExecutorQueuedCommandSender.class).in(Singleton.class);
        bind(ComponentHandler.class).annotatedWith(Names.named("ConnectionUi")).to(SerialPortPuzzleConnectionUi.class);
        bindConstant().annotatedWith(Names.named("CommunicationManagerInitialDelayMs")).to(5000);
        bindConstant().annotatedWith(Names.named("CommunicationManagerRateMs")).to(100);
        bind(HandshakeResultObserver.class).to(SerialPortPuzzleConnectionUi.class);
        bind(ScheduledQueuedCommandSender.class).to(ScheduledExecutorQueuedCommandSender.class);
        bind(SerialPortObserver.class).to(SerialPortConnector.class);
    }

    @Provides
    private static SerialPortMessageListener provideSerialPortMessageListener(Iterable<ByteArrayObserver> responseObservers) {
        ByteArrayObserversStackSerialPortMessageListener serialPortMessageListener = new ByteArrayObserversStackSerialPortMessageListener();
        for (ByteArrayObserver responseObserver : responseObservers) {
            serialPortMessageListener.addByteArrayObserver(responseObserver);
        }
        return serialPortMessageListener;
    }
}
