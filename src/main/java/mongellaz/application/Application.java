package mongellaz.application;

import com.fazecast.jSerialComm.SerialPort;
import mongellaz.commands.handshake.HandshakeResponseProcessor;
import mongellaz.commands.statusrequest.StatusRequestResponseProcessor;
import mongellaz.commands.toggleconfigurationmode.ToggleConfigurationModeResponseProcessor;
import mongellaz.commands.togglelock.ToggleLockResponseProcessor;
import mongellaz.communication.serial.ByteArrayObserversStackSerialPortMessageListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Application {
    public static void main(String[] args) {
        //Set up logger
        Logger logger = LogManager.getLogger();
        logger.info("Application start");

        // Open serial port
        SerialPort serialPort = SerialPort.getCommPorts()[0];
        if (!serialPort.openPort()) {
            logger.fatal("Could not open serial port");
            return;
        }

        final int commandReadRateTimeMs = 100;
        ScheduledExecutorService commandWriterExecutorService = Executors.newSingleThreadScheduledExecutor();
        SerialPortByteArrayConsumer serialPortCommandHandler = new SerialPortByteArrayConsumer(serialPort);
        commandWriterExecutorService.scheduleAtFixedRate(
                serialPortCommandHandler::writeNextCommandInSerialPort,
                0,
                commandReadRateTimeMs,
                TimeUnit.MILLISECONDS
        );

        // Set up basic resources handles
        SerialBookPuzzleDeviceController controller = new SerialBookPuzzleDeviceController(serialPortCommandHandler);
        ResourcesCloser resourcesCloser = new ResourcesCloser();
        resourcesCloser.addCloseable(commandWriterExecutorService::shutdown);

        // Set up communication with device
        logger.info("Initialization successful");
        ByteArrayObserversStackSerialPortMessageListener byteArrayObserversStackSerialPortMessageListener = new ByteArrayObserversStackSerialPortMessageListener();
        HandshakeResponseProcessor handshakeResponseProcessor = new HandshakeResponseProcessor();
        StatusRequestResponseProcessor statusRequestResponseProcessor = new StatusRequestResponseProcessor();
        ToggleLockResponseProcessor toggleLockResponseProcessor = new ToggleLockResponseProcessor();
        ToggleConfigurationModeResponseProcessor toggleConfigurationModeResponseProcessor = new ToggleConfigurationModeResponseProcessor();
        byteArrayObserversStackSerialPortMessageListener.addByteArrayObserver(handshakeResponseProcessor);
        byteArrayObserversStackSerialPortMessageListener.addByteArrayObserver(statusRequestResponseProcessor);
        byteArrayObserversStackSerialPortMessageListener.addByteArrayObserver(toggleLockResponseProcessor);
        byteArrayObserversStackSerialPortMessageListener.addByteArrayObserver(toggleConfigurationModeResponseProcessor);
        serialPort.addDataListener(byteArrayObserversStackSerialPortMessageListener);
        controller.setHandshakeResponseProcessor(handshakeResponseProcessor);
        controller.setStatusRequestResponseProcessor(statusRequestResponseProcessor);
        controller.setToggleLockResponseProcessor(toggleLockResponseProcessor);
        controller.setToggleConfigurationModeResponseProcessor(toggleConfigurationModeResponseProcessor);

        // Create UI
        Ui ui = new Ui(controller);
        ui.setConnectionStateToConnecting();
        controller.addBookPuzzleDeviceStateObserver(ui);

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
        try {
            final int initialDelayTimeMs = 5000;
            Thread.sleep(initialDelayTimeMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.fatal("Interrupted thread");
            return;
        }
        controller.start();
    }

}
