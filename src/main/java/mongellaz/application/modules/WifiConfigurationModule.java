package mongellaz.application.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.implementations.wifi.WifiConfigurationObserver;
import mongellaz.communication.implementations.wifi.WifiConfigurationUi;
import mongellaz.communication.implementations.wifi.WifiConfigurator;
import mongellaz.communication.DeviceController;
import mongellaz.userinterface.ComponentHandler;
import mongellaz.userinterface.VerticalLayoutContainerProvider;
import mongellaz.devices.wifi.commands.handshake.WifiHandshakeResponseProcessor;
import mongellaz.devices.wifi.devicecontroller.ByteArrayWifiDeviceController;

import java.awt.*;
import java.util.ArrayList;

public class WifiConfigurationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(WifiConfigurationUi.class).in(Singleton.class);
        bind(Container.class).toProvider(VerticalLayoutContainerProvider.class);
        bind(ComponentHandler.class).annotatedWith(Names.named("WifiConfigurationUi")).to(WifiConfigurationUi.class);
        bind(ByteArrayObserver.class)
                .annotatedWith(Names.named("HandshakeResponseProcessor"))
                .to(WifiHandshakeResponseProcessor.class);
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
            @Named("HandshakeResponseProcessor") ByteArrayObserver handshakeResponseProcessor
    ) {
        ArrayList<ByteArrayObserver> byteArrayObservers = new ArrayList<>();
        byteArrayObservers.add(handshakeResponseProcessor);
        return byteArrayObservers;
    }
}
