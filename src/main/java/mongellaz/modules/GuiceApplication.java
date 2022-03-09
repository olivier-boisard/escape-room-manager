package mongellaz.modules;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceApplication {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new BookPuzzleModule());
        PuzzleUi puzzleUi = injector.getInstance(PuzzleUi.class);
        puzzleUi.start();
    }
}
