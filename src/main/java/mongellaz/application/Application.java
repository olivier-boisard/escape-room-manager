package mongellaz.application;

import mongellaz.communication.CommunicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Application {
    public static void main(String[] args) {
        //Set up logger
        Logger logger = LogManager.getLogger();
        logger.info("Application start");

        // Set up basic resources handles
        SerialController controller = new SerialController();
        ResourcesCloser resourcesCloser = new ResourcesCloser();
        resourcesCloser.addCloseable(controller);

        try {
            // Create UI
            Ui ui = new Ui(controller);
            controller.addHandshakeResultObserver(ui);
            controller.addLockStateObserver(ui);
            controller.addConfigurationModeStateObserver(ui);

            // Set up UI
            JFrame frame = new JFrame("Ui");
            frame.setContentPane(ui.getMainPanel());
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    resourcesCloser.closeResources();
                }
            });
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.pack();

            // Start UI
            frame.setVisible(true);

            // Start controller
            controller.start();
        } catch (CommunicationException e) {
            logger.fatal(e.getMessage());
            resourcesCloser.closeResources();
        }
        // We shouldn't use the "finally" block here to call controller.stop() because that block would be reached
        // before the end of the application
    }

}
