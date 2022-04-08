package mongellaz.application;

import com.google.inject.Guice;
import com.google.inject.Injector;
import mongellaz.application.modules.*;
import mongellaz.communication.manager.ScheduledQueuedCommandSender;
import mongellaz.userinterface.GraphicalUserInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Application {
    public static void main(String[] args) {
        // Get main UI components
        Injector wifiConfigurationInjector = Guice.createInjector(
                new WifiConfigurationModule(),
                new SerialPortModule(),
                new ScheduledExecutorQueuedCommandSenderModule()
        );
        Injector bookPuzzleInjector = Guice.createInjector(
                new BookPuzzleModule(),
                new SocketModule(),
                new ScheduledExecutorQueuedCommandSenderModule()
        );

        // Setup UI
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Configuration", wifiConfigurationInjector.getInstance(Container.class));
        tabbedPane.add("BONFILS - Livres", bookPuzzleInjector.getInstance(Container.class));
        GraphicalUserInterface userInterface = new GraphicalUserInterface(tabbedPane, "Enquête Sensorielle");

        // Hande window closing
        userInterface.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                wifiConfigurationInjector.getInstance(ScheduledQueuedCommandSender.class).shutdown();
                bookPuzzleInjector.getInstance(ScheduledQueuedCommandSender.class).shutdown();
            }
        });

        // Start UI
        userInterface.start();
    }
}
