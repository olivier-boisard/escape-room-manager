package mongellaz.application;

import com.fazecast.jSerialComm.SerialPort;
import mongellaz.commands.handshake.HandshakeFactory;
import mongellaz.commands.handshake.HandshakeResponseProcessor;
import mongellaz.commands.statusrequest.StatusRequestFactory;
import mongellaz.commands.statusrequest.StatusRequestResponseProcessor;
import mongellaz.commands.toggleconfigurationmode.ToggleConfigurationModeResponseProcessor;
import mongellaz.commands.togglelock.ToggleLockResponseProcessor;
import mongellaz.communication.SerialCommunicationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Backend {
    public static void main(String[] args) {
        HandshakeFactory handshakeFactory = new HandshakeFactory();
        StatusRequestFactory statusRequestFactory = new StatusRequestFactory();
        ScheduledExecutorService commandWriterExecutorService = Executors.newSingleThreadScheduledExecutor();

        LinkedListArduinoSerialPortMessageListener arduinoSerialPortMessageListener = new LinkedListArduinoSerialPortMessageListener();
        arduinoSerialPortMessageListener.addResponseProcessor(new HandshakeResponseProcessor());
        arduinoSerialPortMessageListener.addResponseProcessor(new StatusRequestResponseProcessor());
        arduinoSerialPortMessageListener.addResponseProcessor(new ToggleLockResponseProcessor());
        arduinoSerialPortMessageListener.addResponseProcessor(new ToggleConfigurationModeResponseProcessor());

        SerialPort serialPort = SerialPort.getCommPorts()[0];
        if (!serialPort.openPort()) {
            logger.fatal("Could not open serial port. Aborting");
            return;
        }
        serialPort.addDataListener(arduinoSerialPortMessageListener);

        try (SerialCommunicationManager communicationManager = new SerialCommunicationManager(serialPort)) {
            //TODO parameterize
            Thread.sleep(3000);

            // Start command writer thread
            CommandsWriter commandsWriter = new ConcurrentLinkedQueueCommandsWriter(communicationManager);
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

            //noinspection ResultOfMethodCallIgnored
            commandWriterExecutorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.fatal("Could not run Thread.sleep(): {}", e.getMessage());
        } finally {
            commandWriterExecutorService.shutdown();
        }
    }

    private static final Logger logger = LogManager.getLogger();

}
