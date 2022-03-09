package mongellaz.modules;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import mongellaz.userinterface.BookPuzzleControlUi;
import mongellaz.userinterface.SerialPortPuzzleConnectionUi;

import java.awt.*;

public class BookPuzzleModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PuzzleUi.class).to(PuzzleUiImpl.class);
        bind(Component.class)
                .annotatedWith(Names.named("PuzzleConnectionUi"))
                .toProvider(SerialPortPuzzleConnectionUi.class);
        bind(Component.class)
                .annotatedWith(Names.named("PuzzleControlUi"))
                .toProvider(BookPuzzleControlUi.class);
        bind(PuzzleUiPanel.class).to(VerticalLayoutPuzzleUi.class);
        bind(String.class)
                .annotatedWith(Names.named("PuzzleName"))
                .toInstance("Puzzle des livres");
    }
}
