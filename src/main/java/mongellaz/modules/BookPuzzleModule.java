package mongellaz.modules;

import com.fazecast.jSerialComm.SerialPortMessageListener;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import mongellaz.commands.handshake.HandshakeResponseProcessor;
import mongellaz.commands.statusrequest.StatusRequestResponseProcessor;
import mongellaz.commands.toggleconfigurationmode.ToggleConfigurationModeResponseProcessor;
import mongellaz.commands.togglelock.ToggleLockResponseProcessor;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.ScheduledCommunicationManager;
import mongellaz.communication.ScheduledCommunicationManagerImpl;
import mongellaz.communication.serial.ByteArrayObserversStackSerialPortMessageListener;
import mongellaz.userinterface.BookPuzzleControlUi;
import mongellaz.userinterface.PuzzleConnectionUi;
import mongellaz.userinterface.SerialPortPuzzleConnectionUi;

import java.awt.*;
import java.util.ArrayList;

public class BookPuzzleModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(UserInterface.class).to(GraphicalUserInterface.class);
        bind(Container.class).toProvider(VerticalLayoutContainerProvider.class);
        bind(PuzzleConnectionUi.class).to(SerialPortPuzzleConnectionUi.class);
        bind(ScheduledCommunicationManager.class).to(ScheduledCommunicationManagerImpl.class);
        bindConstant()
                .annotatedWith(Names.named("MainFrameName"))
                .to("Puzzle des livres");
        bindConstant().annotatedWith(Names.named("CommunicationManagerInitialDelayMs")).to(5000);
        bindConstant().annotatedWith(Names.named("CommunicationManagerRateMs")).to(100);
    }

    @SuppressWarnings("unused")
    @Provides
    private static Iterable<Component> provideComponents(PuzzleConnectionUi puzzleConnectionUi) {
        ArrayList<Component> components = new ArrayList<>();
        components.add(puzzleConnectionUi.getMainPanel());
        components.add(new BookPuzzleControlUi().getMainPanel());
        return components;
    }

    @Provides
    private static SerialPortMessageListener provideSerialPortMessageListener(Iterable<ByteArrayObserver> responseObservers) {
        ByteArrayObserversStackSerialPortMessageListener serialPortMessageListener=new ByteArrayObserversStackSerialPortMessageListener();
        for (ByteArrayObserver responseObserver : responseObservers) {
            serialPortMessageListener.addByteArrayObserver(responseObserver);
        }
        return serialPortMessageListener;
    }

    @Provides
    private static Iterable<ByteArrayObserver> provideResponseObservers() {
        ArrayList<ByteArrayObserver> byteArrayObservers = new ArrayList<>();
        byteArrayObservers.add(new HandshakeResponseProcessor());
        byteArrayObservers.add(new StatusRequestResponseProcessor());
        byteArrayObservers.add(new ToggleLockResponseProcessor());
        byteArrayObservers.add(new ToggleConfigurationModeResponseProcessor());
        return byteArrayObservers;
    }
}
