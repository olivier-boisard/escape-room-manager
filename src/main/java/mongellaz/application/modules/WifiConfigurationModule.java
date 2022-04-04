package mongellaz.application.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.devices.wifi.commands.connection.ConnectionStateObserver;
import mongellaz.devices.wifi.commands.connection.WifiConfigurationObserver;
import mongellaz.communication.implementations.wifi.WifiConfigurationUi;
import mongellaz.devices.wifi.commands.connection.WifiConfigurationRequestResponseProcessor;
import mongellaz.devices.wifi.commands.connection.WifiConfigurator;
import mongellaz.communication.DeviceController;
import mongellaz.userinterface.ComponentHandler;
import mongellaz.userinterface.VerticalLayoutContainerProvider;
import mongellaz.communication.handshake.HandshakeResponseProcessor;
import mongellaz.devices.wifi.devicecontroller.ByteArrayWifiDeviceController;

import java.awt.*;
import java.util.ArrayList;

public class WifiConfigurationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(WifiConfigurationUi.class).in(Singleton.class);
        bind(Container.class).toProvider(VerticalLayoutContainerProvider.class);
        bind(ComponentHandler.class).annotatedWith(Names.named("WifiConfigurationUi")).to(WifiConfigurationUi.class);
        bind(new TypeLiteral<byte[]>() {})
                .annotatedWith(Names.named("ExpectedDeviceFirmwareId"))
                .toInstance(new byte[]{0x7F, (byte) 0xE0, 0x04, (byte) 0xB2, 0x7C, (byte) 0xE1, 0x0A, 0x2A});
        bind(ByteArrayObserver.class)
                .annotatedWith(Names.named("HandshakeResponseProcessor"))
                .to(HandshakeResponseProcessor.class);
        bind(ByteArrayObserver.class)
                .annotatedWith(Names.named("WifiConfigurationRequestResponseProcessor"))
                .to(WifiConfigurationRequestResponseProcessor.class);
        bind(ConnectionStateObserver.class).to(WifiConfigurationUi.class);
        bind(WifiConfigurationObserver.class).to(WifiConfigurator.class);
        bind(DeviceController.class).to(ByteArrayWifiDeviceController.class);
    }

    @SuppressWarnings("unused")
    @Provides
    private static Iterable<Component> provideComponents(
            @Named("ConnectionUi") ComponentHandler connectionUi,
            @Named("WifiConfigurationUi") ComponentHandler wifiConfigurationUi
    ) {
        ArrayList<Component> components = new ArrayList<>();
        components.add(connectionUi.getMainPanel());
        components.add(wifiConfigurationUi.getMainPanel());
        return components;
    }

    @Provides
    private static Iterable<ByteArrayObserver> provideResponseObservers(
            @Named("HandshakeResponseProcessor") ByteArrayObserver handshakeResponseProcessor,
            @Named("WifiConfigurationRequestResponseProcessor") ByteArrayObserver wifiConfigurationRequestResponseProcessor
    ) {
        ArrayList<ByteArrayObserver> byteArrayObservers = new ArrayList<>();
        byteArrayObservers.add(handshakeResponseProcessor);
        byteArrayObservers.add(wifiConfigurationRequestResponseProcessor);
        return byteArrayObservers;
    }
}
