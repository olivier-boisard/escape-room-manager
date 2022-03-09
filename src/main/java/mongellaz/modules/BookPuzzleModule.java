package mongellaz.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import mongellaz.userinterface.BookPuzzleControlUi;
import mongellaz.userinterface.SerialPortPuzzleConnectionUi;

import java.awt.*;
import java.util.ArrayList;

public class BookPuzzleModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(UserInterface.class).to(GraphicalUserInterface.class);
        bind(Container.class).toProvider(VerticalLayoutContainerProvider.class);
        bindConstant()
                .annotatedWith(Names.named("MainFrameName"))
                .to("Puzzle des livres");
    }

    @SuppressWarnings("unused")
    @Provides
    private static Iterable<Component> provideComponents() {
        ArrayList<Component> components = new ArrayList<>();
        components.add(new SerialPortPuzzleConnectionUi().getMainPanel());
        components.add(new BookPuzzleControlUi().getMainPanel());
        return components;
    }

}
