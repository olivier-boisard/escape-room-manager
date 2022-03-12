package mongellaz.modules;

import com.google.inject.Guice;
import com.google.inject.Injector;

import java.awt.*;

public class Application {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new BookPuzzleModule());
        Container bookPuzzleUiContainer = injector.getInstance(Container.class);
        GraphicalUserInterface userInterface = new GraphicalUserInterface(bookPuzzleUiContainer, "Puzzle des livres");
        userInterface.start();
    }
}
