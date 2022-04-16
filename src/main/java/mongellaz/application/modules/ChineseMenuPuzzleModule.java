package mongellaz.application.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import mongellaz.communication.ByteArrayObserver;
import mongellaz.communication.DeviceController;
import mongellaz.devices.chinesemenupuzzle.ChineseMenuPuzzleUi;
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
    private static Iterable<ByteArrayObserver> provideByteArrayObservers() {
        return new ArrayList<>(); //TODO
    }
}
