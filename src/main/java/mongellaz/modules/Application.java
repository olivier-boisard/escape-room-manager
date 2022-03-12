package mongellaz.modules;

import com.google.inject.Guice;
import com.google.inject.Injector;
import mongellaz.communication.ScheduledCommunicationManager;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Application {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new BookPuzzleModule());
        Container bookPuzzleUiContainer = injector.getInstance(Container.class);
        GraphicalUserInterface userInterface = new GraphicalUserInterface(bookPuzzleUiContainer, "Puzzle des livres");
        userInterface.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                injector.getInstance(ScheduledCommunicationManager.class).shutdown();
            }
        });
        userInterface.start();
    }
}
