package mongellaz.application;

import com.fazecast.jSerialComm.SerialPort;
import mongellaz.commands.handshake.HandshakeResponseProcessor;
import mongellaz.commands.statusrequest.StatusRequestResponseProcessor;
import mongellaz.commands.toggleconfigurationmode.ToggleConfigurationModeResponseProcessor;
import mongellaz.commands.togglelock.ToggleLockResponseProcessor;
import mongellaz.communication.serial.ByteArrayObserversStackSerialPortMessageListener;
import mongellaz.communication.serial.SerialPortByteArrayObserver;
import mongellaz.communication.serial.SerialPortCommunicationRuntimeException;
import mongellaz.userinterface.BookPuzzleControlUi;
import mongellaz.userinterface.SerialPortPuzzleConnectionUi;
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
        Logger logger = LogManager.getLogger();
        logger.info("Starting application");

        // Create objects
        BookPuzzleControlUi bookPuzzleUi = new BookPuzzleControlUi();
        SerialPortByteArrayObserver serialPortCommandHandler = new SerialPortByteArrayObserver();
        ByteArrayControlledBookPuzzleDeviceController controller = new ByteArrayControlledBookPuzzleDeviceController(serialPortCommandHandler);
        ByteArrayObserversStackSerialPortMessageListener byteArrayObserversStackSerialPortMessageListener = new ByteArrayObserversStackSerialPortMessageListener();
        HandshakeResponseProcessor handshakeResponseProcessor = new HandshakeResponseProcessor();
        StatusRequestResponseProcessor statusRequestResponseProcessor = new StatusRequestResponseProcessor();
        ToggleLockResponseProcessor toggleLockResponseProcessor = new ToggleLockResponseProcessor();
        ToggleConfigurationModeResponseProcessor toggleConfigurationModeResponseProcessor = new ToggleConfigurationModeResponseProcessor();
        ResourcesCloser resourcesCloser = new ResourcesCloser();

        SerialPortPuzzleConnectionUi serialPortConnectionUi = new SerialPortPuzzleConnectionUi();

        // Wiring
        byteArrayObserversStackSerialPortMessageListener.addByteArrayObserver(handshakeResponseProcessor);
        byteArrayObserversStackSerialPortMessageListener.addByteArrayObserver(statusRequestResponseProcessor);
        byteArrayObserversStackSerialPortMessageListener.addByteArrayObserver(toggleLockResponseProcessor);
        byteArrayObserversStackSerialPortMessageListener.addByteArrayObserver(toggleConfigurationModeResponseProcessor);
        handshakeResponseProcessor.addHandshakeResultObserver(serialPortConnectionUi);
        toggleLockResponseProcessor.addLockStateObserver(bookPuzzleUi);
        toggleConfigurationModeResponseProcessor.addConfigurationModeStateObserver(bookPuzzleUi);
        statusRequestResponseProcessor.addPiccReaderStatusesObserver(bookPuzzleUi);
        statusRequestResponseProcessor.addLockStateObserver(bookPuzzleUi);
        statusRequestResponseProcessor.addConfigurationModeStateObserver(bookPuzzleUi);
        bookPuzzleUi.setBookPuzzleDeviceController(controller);
        ArrayList<String> connectionOptions = new ArrayList<>();
        for (SerialPort serialPort1 : SerialPort.getCommPorts()) {
            connectionOptions.add(serialPort1.getDescriptivePortName());
        }
        serialPortConnectionUi.setConnectionOptions(connectionOptions);
        serialPortConnectionUi.addConnectionButtonActionListener(e -> {
            String selectedConnectionOption = serialPortConnectionUi.getSelectedConnectionOption();
            SerialPort selectedSerialPort1 = null;
            for (SerialPort serialPort : SerialPort.getCommPorts()) {
                if (Objects.equals(serialPort.getDescriptivePortName(), selectedConnectionOption)) {
                    selectedSerialPort1 = serialPort;
                    break;
                }
            }
            if (selectedSerialPort1 == null) {
                throw new SerialPortCommunicationRuntimeException("Unknown serial port " + selectedConnectionOption);
            }
            if (!selectedSerialPort1.openPort()) {
                throw new SerialPortCommunicationRuntimeException("Could not open serial port");
            }
            SerialPort selectedSerialPort = selectedSerialPort1;
            serialPortCommandHandler.setSerialPort(selectedSerialPort);
            selectedSerialPort.addDataListener(byteArrayObserversStackSerialPortMessageListener);
            ScheduledExecutorService commandWriterExecutorService = Executors.newSingleThreadScheduledExecutor();
            final int initialDelayMs = 5000;
            final int commandReadRateTimeMs = 100;
            commandWriterExecutorService.scheduleAtFixedRate(
                    serialPortCommandHandler::writeNextCommandInSerialPort,
                    initialDelayMs,
                    commandReadRateTimeMs,
                    TimeUnit.MILLISECONDS
            );
            resourcesCloser.addCloseable(selectedSerialPort::closePort);
            resourcesCloser.addCloseable(commandWriterExecutorService::shutdown);
        });

        // Build UI
        JFrame frame = new JFrame("Puzzle des livres");
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(serialPortConnectionUi.getMainPanel());
        panel.add(bookPuzzleUi.getMainPanel());
        frame.setContentPane(panel);
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

        // Start
        frame.setVisible(true);
        controller.start();

        logger.info("Application started");
    }

}
