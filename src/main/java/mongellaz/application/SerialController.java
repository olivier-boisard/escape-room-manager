package mongellaz.application;

import com.fazecast.jSerialComm.SerialPort;
import mongellaz.commands.LockStateObserver;
import mongellaz.commands.handshake.HandshakeFactory;
import mongellaz.commands.handshake.HandshakeResponseProcessor;
import mongellaz.commands.statusrequest.StatusRequestFactory;
import mongellaz.commands.statusrequest.StatusRequestResponseProcessor;
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

public class SerialController implements Controller, Closeable {

    //TODO this method is too big
    public void start() throws CommunicationException {
        HandshakeFactory handshakeFactory = new HandshakeFactory();
        commandWriterExecutorService = Executors.newSingleThreadScheduledExecutor();

        ArduinoSerialPortMessageListener arduinoSerialPortMessageListener = new ArduinoSerialPortMessageListener();
        arduinoSerialPortMessageListener.addResponseProcessor(new HandshakeResponseProcessor());
        arduinoSerialPortMessageListener.addResponseProcessor(new StatusRequestResponseProcessor());
        arduinoSerialPortMessageListener.addResponseProcessor(toggleLockResponseProcessor);
        arduinoSerialPortMessageListener.addResponseProcessor(new ToggleConfigurationModeResponseProcessor());

        SerialPort serialPort = SerialPort.getCommPorts()[0];
        if (!serialPort.openPort()) {
            logger.fatal("Could not open serial port. Aborting");
            return;
        }
        serialPort.addDataListener(arduinoSerialPortMessageListener);

        communicationManager = new SerialCommunicationManager(serialPort);
        try {
            //TODO parameterize
            Thread.sleep(5000);

            // Start command writer thread
            commandsWriter = new ConcurrentLinkedQueueCommandsWriter(communicationManager);
            commandWriterExecutorService.scheduleAtFixedRate(
                    commandsWriter::runNextCommand,
                    0,
                    100,//TODO parameterize
                    TimeUnit.MILLISECONDS
            );

            // Run handshake
            commandsWriter.addCommand(handshakeFactory.generate());

            // Get status
            commandsWriter.addCommand(statusRequestFactory.generate());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            commandWriterExecutorService.shutdown();
            throw new CommunicationException(e);
        }
    }

    @Override
    public void sendToggleLockCommand() {
        commandsWriter.addCommand(toggleLockCommandFactory.generate());
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

    public void addLockStateObserver(LockStateObserver lockStateObserver){
        toggleLockResponseProcessor.addLockStateObserver(lockStateObserver);
    }

    private CommandsWriter commandsWriter;
    private final StatusRequestFactory statusRequestFactory = new StatusRequestFactory();
    private final ToggleLockCommandFactory toggleLockCommandFactory = new ToggleLockCommandFactory();
    private SerialCommunicationManager communicationManager;
    private ScheduledExecutorService commandWriterExecutorService;
    private final ToggleLockResponseProcessor toggleLockResponseProcessor = new ToggleLockResponseProcessor();

    private static final Logger logger = LogManager.getLogger();
}
