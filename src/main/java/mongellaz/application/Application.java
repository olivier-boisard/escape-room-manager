package mongellaz.application;

import com.google.inject.Guice;
import com.google.inject.Injector;
import mongellaz.communication.manager.ScheduledQueuedCommandSender;
import mongellaz.modules.BookPuzzleModule;
import mongellaz.modules.SerialPortModule;
import mongellaz.userinterface.GraphicalUserInterface;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Application {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new BookPuzzleModule(), new SerialPortModule());
        Container bookPuzzleUiContainer = injector.getInstance(Container.class);
        GraphicalUserInterface userInterface = new GraphicalUserInterface(bookPuzzleUiContainer, "Puzzle des livres");
        userInterface.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                injector.getInstance(ScheduledQueuedCommandSender.class).shutdown();
            }
        });
        userInterface.start();
    }
}
