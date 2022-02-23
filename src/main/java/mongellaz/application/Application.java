package mongellaz.application;

import mongellaz.communication.CommunicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Application {
    public static void main(String[] args) {
        Logger logger = LogManager.getLogger();
        SerialController controller = new SerialController();
        ResourcesCloser resourcesCloser = new ResourcesCloser();
        resourcesCloser.addCloseable(controller);
        logger.info("Application start");
        try {
            controller.start();
            JFrame frame = new JFrame("Ui");

            Ui ui = new Ui(controller);
            controller.addLockStateObserver(ui);

            frame.setContentPane(ui.getMainPanel());
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
            frame.addWindowListener(resourcesCloser);
        } catch (CommunicationException e) {
            logger.fatal(e.getMessage());
            resourcesCloser.closeResources();
        }
        // We shouldn't use the "finally" block here to call controller.stop() because that block would be reached
        // before the end of the application

        logger.info("Application end");
    }

    private static class ResourcesCloser extends WindowAdapter {

        public ResourcesCloser() {
            this.closeables = new LinkedList<>();
        }

        @Override
        public void windowClosing(WindowEvent e) {
            super.windowClosing(e);
            closeResources();
        }

        public void closeResources() {
            for (Closeable closeable : closeables) {
                try {
                    closeable.close();
                } catch (IOException ex) {
                    logger.fatal("Error while closing resource: {}", ex.getMessage());
                }
            }
        }

        public void addCloseable(Closeable closeable) {
            closeables.add(closeable);
        }

        private final List<Closeable> closeables;
        private final Logger logger = LogManager.getLogger();
    }
}
