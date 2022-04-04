package mongellaz.application.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import mongellaz.devices.bookpuzzle.BookPuzzleControlUi;
import mongellaz.devices.bookpuzzle.commands.statusrequest.PiccReaderStatusesObserver;
import mongellaz.devices.bookpuzzle.commands.statusrequest.StatusRequestResponseProcessor;
import mongellaz.devices.bookpuzzle.commands.toggleconfigurationmode.ConfigurationModeStateObserver;
import mongellaz.devices.bookpuzzle.commands.toggleconfigurationmode.ToggleConfigurationModeResponseProcessor;
import mongellaz.devices.bookpuzzle.commands.togglelock.LockStateObserver;
import mongellaz.devices.bookpuzzle.commands.togglelock.ToggleLockResponseProcessor;
import mongellaz.devices.bookpuzzle.devicecontroller.BookPuzzleDeviceController;
import mongellaz.devices.bookpuzzle.devicecontroller.ByteArrayControlledBookPuzzleDeviceController;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.DeviceController;
import mongellaz.communication.handshake.HandshakeResponseProcessor;
import mongellaz.userinterface.ComponentHandler;
import mongellaz.userinterface.VerticalLayoutContainerProvider;

import java.awt.*;
import java.util.ArrayList;

public class BookPuzzleModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(BookPuzzleControlUi.class).in(Singleton.class);
        bind(ByteArrayControlledBookPuzzleDeviceController.class).in(Singleton.class);
        bind(Container.class).toProvider(VerticalLayoutContainerProvider.class);
        bind(ComponentHandler.class).annotatedWith(Names.named("ControlUi")).to(BookPuzzleControlUi.class);
        bind(DeviceController.class).to(ByteArrayControlledBookPuzzleDeviceController.class);
        bind(ConfigurationModeStateObserver.class).to(BookPuzzleControlUi.class);
        bind(LockStateObserver.class).to(BookPuzzleControlUi.class);
        bind(PiccReaderStatusesObserver.class).to(BookPuzzleControlUi.class);
        bind(BookPuzzleDeviceController.class).to(ByteArrayControlledBookPuzzleDeviceController.class);
        bindConstant()
                .annotatedWith(Names.named("MainFrameName"))
                .to("Puzzle des livres");
        bind(ByteArrayObserver.class).annotatedWith(Names.named("HandshakeResponseProcessor")).to(HandshakeResponseProcessor.class);
        bind(ByteArrayObserver.class).annotatedWith(Names.named("StatusRequestResponseProcessor")).to(StatusRequestResponseProcessor.class);
        bind(ByteArrayObserver.class).annotatedWith(Names.named("ToggleLockResponseProcessor")).to(ToggleLockResponseProcessor.class);
        bind(ByteArrayObserver.class).annotatedWith(Names.named("ToggleConfigurationModeResponseProcessor")).to(ToggleConfigurationModeResponseProcessor.class);
        bind(new TypeLiteral<byte[]>() {})
                .annotatedWith(Names.named("ExpectedDeviceFirmwareId"))
                .toInstance(new byte[]{-123, -14, -98, -29, 67, 25, -22, -10});
    }

    @SuppressWarnings("unused")
    @Provides
    private static Iterable<Component> provideComponents(
            @Named("ConnectionUi") ComponentHandler connectionUi,
            @Named("ControlUi") ComponentHandler controlUi
    ) {
        ArrayList<Component> components = new ArrayList<>();
        components.add(connectionUi.getMainPanel());
        components.add(controlUi.getMainPanel());
        return components;
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