package mongellaz.application;

import com.google.inject.Guice;
import com.google.inject.Injector;
import mongellaz.application.modules.*;
import mongellaz.communication.Heartbeat;
import mongellaz.communication.implementations.socket.SocketConnector;
import mongellaz.communication.manager.ScheduledQueuedCommandSender;
import mongellaz.userinterface.GraphicalUserInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Application {
    public static void main(String[] args) {
        Logger logger = LogManager.getLogger();
        logger.info("Start - running version v202205241726");

        // Get main UI components
        Injector wifiConfigurationInjector = Guice.createInjector(
                new WifiConfigurationModule(),
                new SerialPortModule(),
                new ScheduledExecutorQueuedCommandSenderModule()
        );
        Injector bookPuzzleInjector = Guice.createInjector(
                new BookPuzzleModule(),
                new SocketModule("BookPuzzle"),
                new BookPuzzleSocketModule(),
                new ScheduledExecutorQueuedCommandSenderModule()
        );
        Injector chineseMenuInjector = Guice.createInjector(
                new ChineseMenuPuzzleModule(),
                new SocketModule("ChineseMenuPuzzle"),
                new ChineseMenuPuzzleSocketModule(),
                new ScheduledExecutorQueuedCommandSenderModule()
        );

        // Setup UI
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Configuration", wifiConfigurationInjector.getInstance(Container.class));
        tabbedPane.add("BONFILS - Livres", bookPuzzleInjector.getInstance(Container.class));
        tabbedPane.add("BONFILS - Menu Chinois", chineseMenuInjector.getInstance(Container.class));
        GraphicalUserInterface userInterface = new GraphicalUserInterface(tabbedPane, "Enquête Sensorielle");

        // Hande window closing
        userInterface.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    wifiConfigurationInjector.getInstance(ScheduledQueuedCommandSender.class).close();
                    bookPuzzleInjector.getInstance(ScheduledQueuedCommandSender.class).close();
                    bookPuzzleInjector.getInstance(SocketConnector.class).shutdown();
                    bookPuzzleInjector.getInstance(Heartbeat.class).shutdown();
                    chineseMenuInjector.getInstance(ScheduledQueuedCommandSender.class).close();
                    chineseMenuInjector.getInstance(SocketConnector.class).shutdown();
                    chineseMenuInjector.getInstance(Heartbeat.class).shutdown();
                } catch (IOException ex) {
                    logger.error("Could not stop resources: {}", ex.getMessage());
                }
            }
        });

        // Start UI
        userInterface.start();
    }
}
