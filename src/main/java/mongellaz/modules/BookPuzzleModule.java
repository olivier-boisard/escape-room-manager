package mongellaz.modules;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import mongellaz.userinterface.BookPuzzleControlUi;
import mongellaz.userinterface.SerialPortPuzzleConnectionUi;

public class BookPuzzleModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PuzzleUiPanel.class).to(VerticalLayoutPuzzleUi.class);
        bind(PuzzleUi.class).to(PuzzleUiImpl.class);
        bind(PuzzleControlUi.class).to(BookPuzzleControlUi.class);
        bind(PuzzleConnectionUi.class).to(SerialPortPuzzleConnectionUi.class);
        bind(String.class).annotatedWith(Names.named("PuzzleName")).toInstance("Puzzle des livres");
    }
}
