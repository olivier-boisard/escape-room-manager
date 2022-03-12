package mongellaz.modules;

import com.fazecast.jSerialComm.SerialPortMessageListener;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import mongellaz.bookpuzzle.devicecontroller.ByteArrayControlledBookPuzzleDeviceController;
import mongellaz.bookpuzzle.devicecontroller.BookPuzzleDeviceController;
import mongellaz.bookpuzzle.commands.toggleconfigurationmode.ConfigurationModeStateObserver;
import mongellaz.bookpuzzle.commands.handshake.HandshakeResultObserver;
import mongellaz.bookpuzzle.commands.togglelock.LockStateObserver;
import mongellaz.bookpuzzle.commands.statusrequest.PiccReaderStatusesObserver;
import mongellaz.bookpuzzle.commands.handshake.HandshakeResponseProcessor;
import mongellaz.bookpuzzle.commands.statusrequest.StatusRequestResponseProcessor;
import mongellaz.bookpuzzle.commands.toggleconfigurationmode.ToggleConfigurationModeResponseProcessor;
import mongellaz.bookpuzzle.commands.togglelock.ToggleLockResponseProcessor;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.manager.ScheduledCommunicationManager;
import mongellaz.communication.manager.ScheduledExecutorCommunicationManager;
import mongellaz.communication.serial.ByteArrayObserversStackSerialPortMessageListener;
import mongellaz.userinterface.BookPuzzleControlUi;
import mongellaz.userinterface.ComponentHandler;
import mongellaz.userinterface.SerialPortPuzzleConnectionUi;
import mongellaz.userinterface.VerticalLayoutContainerProvider;

import java.awt.*;
import java.util.ArrayList;

public class BookPuzzleModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SerialPortPuzzleConnectionUi.class).in(Singleton.class);
        bind(BookPuzzleControlUi.class).in(Singleton.class);
        bind(ScheduledExecutorCommunicationManager.class).in(Singleton.class);
        bind(Container.class).toProvider(VerticalLayoutContainerProvider.class);
        bind(ComponentHandler.class).annotatedWith(Names.named("PuzzleConnectionUi")).to(SerialPortPuzzleConnectionUi.class);
        bind(ComponentHandler.class).annotatedWith(Names.named("PuzzleControlUi")).to(BookPuzzleControlUi.class);
        bind(HandshakeResultObserver.class).to(SerialPortPuzzleConnectionUi.class);
        bind(ConfigurationModeStateObserver.class).to(BookPuzzleControlUi.class);
        bind(LockStateObserver.class).to(BookPuzzleControlUi.class);
        bind(PiccReaderStatusesObserver.class).to(BookPuzzleControlUi.class);
        bind(BookPuzzleDeviceController.class).to(ByteArrayControlledBookPuzzleDeviceController.class);
        bind(ScheduledCommunicationManager.class).to(ScheduledExecutorCommunicationManager.class);
        bindConstant()
                .annotatedWith(Names.named("MainFrameName"))
                .to("Puzzle des livres");
        bindConstant().annotatedWith(Names.named("CommunicationManagerInitialDelayMs")).to(5000);
        bindConstant().annotatedWith(Names.named("CommunicationManagerRateMs")).to(100);
        bind(ByteArrayObserver.class).annotatedWith(Names.named("HandshakeResponseProcessor")).to(HandshakeResponseProcessor.class);
        bind(ByteArrayObserver.class).annotatedWith(Names.named("StatusRequestResponseProcessor")).to(StatusRequestResponseProcessor.class);
        bind(ByteArrayObserver.class).annotatedWith(Names.named("ToggleLockResponseProcessor")).to(ToggleLockResponseProcessor.class);
        bind(ByteArrayObserver.class).annotatedWith(Names.named("ToggleConfigurationModeResponseProcessor")).to(ToggleConfigurationModeResponseProcessor.class);
    }

    @SuppressWarnings("unused")
    @Provides
    private static Iterable<Component> provideComponents(
            @Named("PuzzleConnect" +
                    "ionUi") ComponentHandler puzzleConnectionUi,
            @Named("PuzzleControlUi") ComponentHandler puzzleControlUi
    ) {
        ArrayList<Component> components = new ArrayList<>();
        components.add(puzzleConnectionUi.getMainPanel());
        components.add(puzzleControlUi.getMainPanel());
        return components;
    }

    @Provides
    private static SerialPortMessageListener provideSerialPortMessageListener(Iterable<ByteArrayObserver> responseObservers) {
        ByteArrayObserversStackSerialPortMessageListener serialPortMessageListener = new ByteArrayObserversStackSerialPortMessageListener();
        for (ByteArrayObserver responseObserver : responseObservers) {
            serialPortMessageListener.addByteArrayObserver(responseObserver);
        }
        return serialPortMessageListener;
    }

    @Provides
    private static Iterable<ByteArrayObserver> provideResponseObservers(
            @Named("HandshakeResponseProcessor") ByteArrayObserver handshakeResponseProcessor,
            @Named("StatusRequestResponseProcessor") ByteArrayObserver statusRequestResponseProcessor,
            @Named("ToggleLockResponseProcessor") ByteArrayObserver toggleLockResponseProcessor,
            @Named("ToggleConfigurationModeResponseProcessor") ByteArrayObserver toggleConfigurationModeResponseProcessor
    ) {
        ArrayList<ByteArrayObserver> byteArrayObservers = new ArrayList<>();
        byteArrayObservers.add(handshakeResponseProcessor);
        byteArrayObservers.add(statusRequestResponseProcessor);
        byteArrayObservers.add(toggleLockResponseProcessor);
        byteArrayObservers.add(toggleConfigurationModeResponseProcessor);
        return byteArrayObservers;
    }
}