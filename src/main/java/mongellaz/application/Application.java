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
        // Set up logger
        logger.info("Application start");

        // Create objects
        Ui ui = new Ui();
        SerialPortByteArrayObserver serialPortCommandHandler = new SerialPortByteArrayObserver();
        ByteArrayControlledBookPuzzleDeviceController controller = new ByteArrayControlledBookPuzzleDeviceController(serialPortCommandHandler);
        ByteArrayObserversStackSerialPortMessageListener byteArrayObserversStackSerialPortMessageListener = new ByteArrayObserversStackSerialPortMessageListener();
        HandshakeResponseProcessor handshakeResponseProcessor = new HandshakeResponseProcessor();
        StatusRequestResponseProcessor statusRequestResponseProcessor = new StatusRequestResponseProcessor();
        ToggleLockResponseProcessor toggleLockResponseProcessor = new ToggleLockResponseProcessor();
        ToggleConfigurationModeResponseProcessor toggleConfigurationModeResponseProcessor = new ToggleConfigurationModeResponseProcessor();
        ResourcesCloser resourcesCloser = new ResourcesCloser();

        // Wiring
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
        ui.setBookPuzzleDeviceController(controller);
        ui.setConnectionOptions(getConnectionOptions());
        ui.addConnectionButtonActionListener(e -> {
            String selectedConnectionOption = ui.getSelectedConnectionOption();
            SerialPort selectedSerialPort = establishSerialPortConnection(selectedConnectionOption);
            serialPortCommandHandler.setSerialPort(selectedSerialPort);
            selectedSerialPort.addDataListener(byteArrayObserversStackSerialPortMessageListener);
            ScheduledExecutorService commandWriterExecutorService = Executors.newSingleThreadScheduledExecutor();
            startScheduledCommandWriter(serialPortCommandHandler, commandWriterExecutorService);
            resourcesCloser.addCloseable(selectedSerialPort::closePort);
            resourcesCloser.addCloseable(commandWriterExecutorService::shutdown);
        });

        // Start
        setupMainFrame(ui, resourcesCloser);
        controller.start();
        logger.info("Application started");
    }

    private static void startScheduledCommandWriter(
            SerialPortByteArrayObserver serialPortCommandHandler,
            ScheduledExecutorService commandWriterExecutorService
    ) {
        final int initialDelayMs = 5000;
        final int commandReadRateTimeMs = 100;
        commandWriterExecutorService.scheduleAtFixedRate(
                serialPortCommandHandler::writeNextCommandInSerialPort,
                initialDelayMs,
                commandReadRateTimeMs,
                TimeUnit.MILLISECONDS
        );
    }

    private static SerialPort establishSerialPortConnection(String selectedConnectionOption) {
        SerialPort selectedSerialPort = null;
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
        return selectedSerialPort;
    }

    private static void setupMainFrame(Ui ui, ResourcesCloser resourcesCloser) {
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
        frame.setVisible(true);
    }

    private static ArrayList<String> getConnectionOptions() {
        ArrayList<String> connectionOptions = new ArrayList<>();
        for (SerialPort serialPort : SerialPort.getCommPorts()) {
            connectionOptions.add(serialPort.getDescriptivePortName());
        }
        return connectionOptions;
    }

    private static final Logger logger = LogManager.getLogger();

}
