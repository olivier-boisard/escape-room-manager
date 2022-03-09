package mongellaz.modules;

import com.google.inject.AbstractModule;
import mongellaz.userinterface.BookPuzzleControlUi;
import mongellaz.userinterface.SerialPortPuzzleConnectionUi;

public class BookPuzzleModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PuzzleUi.class).to(PuzzleUiImpl.class);
        bind(PuzzleControlUi.class).to(BookPuzzleControlUi.class);
        bind(PuzzleConnectionUi.class).to(SerialPortPuzzleConnectionUi.class);
    }
}
