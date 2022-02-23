package mongellaz.application;

import com.fazecast.jSerialComm.SerialPort;
import mongellaz.commands.ConfigurationModeStateObserver;
import mongellaz.commands.LockStateObserver;
import mongellaz.commands.handshake.HandshakeFactory;
import mongellaz.commands.handshake.HandshakeResponseProcessor;
import mongellaz.commands.statusrequest.StatusRequestFactory;
import mongellaz.commands.statusrequest.StatusRequestResponseProcessor;
import mongellaz.commands.toggleconfigurationmode.ToggleConfigurationModeCommandFactory;
import mongellaz.commands.toggleconfigurationmode.ToggleConfigurationModeResponseProcessor;
import mongellaz.commands.togglelock.ToggleLockCommandFactory;
import mongellaz.commands.togglelock.ToggleLockResponseProcessor;
import mongellaz.communication.CommunicationException;
import mongellaz.communication.SerialCommunicationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SerialController implements Controller, Closeable, BoardStateObserver {

    public void start() throws CommunicationException {
        try {
            initializeCommunicationManager();
            initializeCommandWriter();
            startCommandWriterExecutorService();
            startCommunicationWithBoard();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            commandWriterExecutorService.shutdown();
            throw new CommunicationException(e);
        }
    }

    @Override
    public void addLockStateObserver(LockStateObserver lockStateObserver) {
        toggleLockResponseProcessor.addLockStateObserver(lockStateObserver);
        statusRequestResponseProcessor.addLockStateObserver(lockStateObserver);
    }

    @Override
    public void addConfigurationModeStateObserver(ConfigurationModeStateObserver configurationModeStateObserver) {
        toggleConfigurationModeResponseProcessor.addConfigurationModeStateObserver(configurationModeStateObserver);
        statusRequestResponseProcessor.addConfigurationModeStateObserver(configurationModeStateObserver);
    }

    @Override
    public void sendToggleLockCommand() {
        commandsWriter.addCommand(toggleLockCommandFactory.generate());
    }

    @Override
    public void sendToggleConfigurationModeCommand() {
        commandsWriter.addCommand(toggleConfigurationModeCommandFactory.generate());
    }

    @Override
    public void close() {
        if (commandWriterExecutorService != null) {
            commandWriterExecutorService.shutdown();
        }
        if (communicationManager != null) {
            communicationManager.close();
        }
    }

    private void initializeCommunicationManager() throws CommunicationException, InterruptedException {
        SerialPort serialPort = createSerialPortHandler();
        serialPort.addDataListener(createArduinoSerialPortMessageListener());
        communicationManager = new SerialCommunicationManager(serialPort);

    }

    private void initializeCommandWriter() {
        commandsWriter = new ConcurrentLinkedQueueCommandsWriter(communicationManager);
    }

    private void startCommandWriterExecutorService() {
        final int commandReadRateTimeMs = 100;
        commandWriterExecutorService = Executors.newSingleThreadScheduledExecutor();
        commandWriterExecutorService.scheduleAtFixedRate(
                commandsWriter::runNextCommand,
                0,
                commandReadRateTimeMs,
                TimeUnit.MILLISECONDS
        );
    }

    private void startCommunicationWithBoard() {
        commandsWriter.addCommand(new HandshakeFactory().generate());
        commandsWriter.addCommand(statusRequestFactory.generate());
    }

    private SerialPort createSerialPortHandler() throws CommunicationException, InterruptedException {
        final int initialDelayTimeMs = 5000;
        SerialPort serialPort = SerialPort.getCommPorts()[0];
        if (!serialPort.openPort()) {
            throw new CommunicationException("Could not open serial port");
        }
        Thread.sleep(initialDelayTimeMs);
        logger.info("Initialization successful");
        return serialPort;
    }

    private ArduinoSerialPortMessageListener createArduinoSerialPortMessageListener() {
        ArduinoSerialPortMessageListener arduinoSerialPortMessageListener = new ArduinoSerialPortMessageListener();
        arduinoSerialPortMessageListener.addResponseProcessor(new HandshakeResponseProcessor());
        arduinoSerialPortMessageListener.addResponseProcessor(statusRequestResponseProcessor);
        arduinoSerialPortMessageListener.addResponseProcessor(toggleLockResponseProcessor);
        arduinoSerialPortMessageListener.addResponseProcessor(toggleConfigurationModeResponseProcessor);
        return arduinoSerialPortMessageListener;
    }

    private final StatusRequestFactory statusRequestFactory = new StatusRequestFactory();
    private final ToggleLockCommandFactory toggleLockCommandFactory = new ToggleLockCommandFactory();
    private final ToggleConfigurationModeCommandFactory toggleConfigurationModeCommandFactory = new ToggleConfigurationModeCommandFactory();
    private final ToggleLockResponseProcessor toggleLockResponseProcessor = new ToggleLockResponseProcessor();
    private final ToggleConfigurationModeResponseProcessor toggleConfigurationModeResponseProcessor = new ToggleConfigurationModeResponseProcessor();
    private final StatusRequestResponseProcessor statusRequestResponseProcessor = new StatusRequestResponseProcessor();
    private CommandsWriter commandsWriter;
    private SerialCommunicationManager communicationManager;
    private ScheduledExecutorService commandWriterExecutorService;

    private static final Logger logger = LogManager.getLogger();
}
