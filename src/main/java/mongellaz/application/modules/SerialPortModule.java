package mongellaz.application.modules;

import com.fazecast.jSerialComm.SerialPortMessageListener;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.handshake.HandshakeResultObserver;
import mongellaz.communication.implementations.serial.ByteArrayObserversStackSerialPortMessageListener;
import mongellaz.communication.implementations.serial.SerialPortConnectionUi;
import mongellaz.communication.implementations.serial.SerialPortConnector;
import mongellaz.communication.implementations.serial.SerialPortObserver;
import mongellaz.userinterface.ComponentHandler;

public class SerialPortModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SerialPortConnectionUi.class).in(Singleton.class);
        bind(ComponentHandler.class).annotatedWith(Names.named("ConnectionUi")).to(SerialPortConnectionUi.class);
        bindConstant().annotatedWith(Names.named("CommunicationManagerInitialDelayMs")).to(5000);
        bindConstant().annotatedWith(Names.named("CommunicationManagerRateMs")).to(100);
        bind(HandshakeResultObserver.class).to(SerialPortConnectionUi.class);
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
