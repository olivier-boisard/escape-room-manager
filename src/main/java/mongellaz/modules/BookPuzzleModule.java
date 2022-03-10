package mongellaz.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import mongellaz.communication.NewCommunicationManagerObserver;
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
        bindConstant()
                .annotatedWith(Names.named("MainFrameName"))
                .to("Puzzle des livres");
    }

    @SuppressWarnings("unused")
    @Provides
    private static Iterable<Component> provideComponents(PuzzleConnectionUi puzzleConnectionUi) {
        ArrayList<Component> components = new ArrayList<>();
        components.add(puzzleConnectionUi.getMainPanel());
        components.add(new BookPuzzleControlUi().getMainPanel());
        return components;
    }

}
