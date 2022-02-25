package mongellaz.application;

import com.fazecast.jSerialComm.SerialPort;
import mongellaz.commands.handshake.HandshakeResponseProcessor;
import mongellaz.commands.statusrequest.StatusRequestResponseProcessor;
import mongellaz.commands.toggleconfigurationmode.ToggleConfigurationModeResponseProcessor;
import mongellaz.commands.togglelock.ToggleLockResponseProcessor;
import mongellaz.communication.serial.ByteArrayObserversStackSerialPortMessageListener;
import mongellaz.communication.serial.SerialPortByteArrayObserver;
import mongellaz.communication.serial.SerialPortCommunicationRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Application {
    public static void main(String[] args) {
        //Set up logger
        Logger logger = LogManager.getLogger();
        logger.info("Application start");

        // Open serial port
        ArrayList<String> connectionOptions = new ArrayList<>();
        for (SerialPort serialPort : SerialPort.getCommPorts()) {
            connectionOptions.add(serialPort.getDescriptivePortName());
        }

        // Create UI
        Ui ui = new Ui();

        // Set up basic resources handles
        final int commandReadRateTimeMs = 100;
        SerialPortByteArrayObserver serialPortCommandHandler = new SerialPortByteArrayObserver();
        ByteArrayControlledBookPuzzleDeviceController controller = new ByteArrayControlledBookPuzzleDeviceController(serialPortCommandHandler);

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

        handshakeResponseProcessor.addHandshakeResultObserver(ui);
        toggleLockResponseProcessor.addLockStateObserver(ui);
        toggleConfigurationModeResponseProcessor.addConfigurationModeStateObserver(ui);
        statusRequestResponseProcessor.addPiccReaderStatusesObserver(ui);

        statusRequestResponseProcessor.addLockStateObserver(ui);
        statusRequestResponseProcessor.addConfigurationModeStateObserver(ui);

        // Set up UI
        ui.setBookPuzzleDeviceController(controller);
        ui.setConnectionOptions(connectionOptions);

        ResourcesCloser resourcesCloser = new ResourcesCloser();

        ui.addConnectionButtonActionListener(e -> {
            SerialPort selectedSerialPort = null;
            String selectedConnectionOption = ui.getSelectedConnectionOption();
            for (SerialPort serialPort : SerialPort.getCommPorts()) {
                if (Objects.equals(serialPort.getDescriptivePortName(), selectedConnectionOption)) {
                    selectedSerialPort = serialPort;
                    break;
                }
            }
            if (selectedSerialPort == null) {
                throw new SerialPortCommunicationRuntimeException("Unknown serial port " + selectedConnectionOption);
            }
            if (!selectedSerialPort.openPort()) {
                throw new SerialPortCommunicationRuntimeException("Could not open serial port");
            }

            try {
                final int initialDelayTimeMs = 5000;
                Thread.sleep(initialDelayTimeMs);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                logger.fatal("Interrupted thread");
                return;
            }
            serialPortCommandHandler.setSerialPort(selectedSerialPort);
            selectedSerialPort.addDataListener(byteArrayObserversStackSerialPortMessageListener);
            ScheduledExecutorService commandWriterExecutorService = Executors.newSingleThreadScheduledExecutor();
            commandWriterExecutorService.scheduleAtFixedRate(
                    serialPortCommandHandler::writeNextCommandInSerialPort,
                    0,
                    commandReadRateTimeMs,
                    TimeUnit.MILLISECONDS
            );
            resourcesCloser.addCloseable(selectedSerialPort::closePort);
            resourcesCloser.addCloseable(commandWriterExecutorService::shutdown);
        });

        JFrame frame = new JFrame("Ui");
        frame.setContentPane(ui.getMainPanel());
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                logger.info("Closing resources");
                resourcesCloser.closeResources();
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();

        // Start UI
        frame.setVisible(true);

        // Start controller
        controller.start();
        logger.info("Controller started");
    }

}
