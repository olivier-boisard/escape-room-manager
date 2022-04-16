package mongellaz.application.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.DeviceController;
import mongellaz.communication.handshake.HandshakeResponseProcessor;
import mongellaz.devices.chinesemenupuzzle.ChineseMenuPuzzleUi;
import mongellaz.devices.chinesemenupuzzle.commands.statusrequest.ChineseMenuStatusRequestResponseProcessor;
import mongellaz.devices.chinesemenupuzzle.devicecontroller.ByteArrayControlledChineseMenuDeviceController;
import mongellaz.devices.chinesemenupuzzle.devicecontroller.ChineseMenuDeviceController;
import mongellaz.userinterface.ComponentHandler;
import mongellaz.userinterface.VerticalLayoutContainerProvider;

import java.awt.*;
import java.util.ArrayList;

public class ChineseMenuPuzzleModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ChineseMenuPuzzleUi.class).in(Singleton.class);
        bind(ByteArrayControlledChineseMenuDeviceController.class).in(Singleton.class);
        bind(ComponentHandler.class).to(ChineseMenuPuzzleUi.class);
        bind(Container.class).toProvider(VerticalLayoutContainerProvider.class);
        bind(ComponentHandler.class).annotatedWith(Names.named("ControlUi")).to(ChineseMenuPuzzleUi.class);
        bind(DeviceController.class).to(ByteArrayControlledChineseMenuDeviceController.class);
        bind(ChineseMenuDeviceController.class).to(ByteArrayControlledChineseMenuDeviceController.class);
        bind(ByteArrayObserver.class).annotatedWith(Names.named("HandshakeResponseProcessor")).to(HandshakeResponseProcessor.class);
        bind(ByteArrayObserver.class).annotatedWith(Names.named("StatusRequestResponseProcessor")).to(ChineseMenuStatusRequestResponseProcessor.class);
        bind(new TypeLiteral<byte[]>() {})
                .annotatedWith(Names.named("ExpectedDeviceFirmwareId"))
                .toInstance(new byte[]{0x30, 0x2B, 0x2A, 0x74, (byte) 0xCC, (byte) 0xAF, 0x59, (byte) 0x86});
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

    @SuppressWarnings("unused")
    @Provides
    private static Iterable<ByteArrayObserver> provideByteArrayObservers(
            @Named("HandshakeResponseProcessor") ByteArrayObserver handshakeResponseProcessor,
            @Named("StatusRequestResponseProcessor") ByteArrayObserver statusRequestResponseProcessor
    ) {
        ArrayList<ByteArrayObserver> byteArrayObservers = new ArrayList<>();
        byteArrayObservers.add(handshakeResponseProcessor);
        byteArrayObservers.add(statusRequestResponseProcessor);
        return byteArrayObservers;
    }
}
