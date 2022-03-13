package mongellaz.communication.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.google.inject.Inject;
import mongellaz.devicecontroller.PuzzleDeviceController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SerialPortConnector implements SerialPortObserver {

    @Inject
    SerialPortConnector(
            SerialPortCommunicationManager serialPortCommunicationManager,
            PuzzleDeviceController puzzleDeviceController
    ) {
        this.serialPortCommunicationManager = serialPortCommunicationManager;
        this.puzzleDeviceController = puzzleDeviceController;
    }

    @Override
    public void update(SerialPort serialPort) {
        logger.info("Establishing connection with serial port");
        if (!serialPort.openPort()) {
            logger.error("Could not connect to serial port");
        } else {
            serialPort.addDataListener(serialPortCommunicationManager.serialPortMessageListener);
            serialPortCommunicationManager.scheduledQueuedCommandSender.updateQueuedCommandSender(new SerialPortQueuedCommandSender(serialPort));
            puzzleDeviceController.start();
        }
    }

    private final SerialPortCommunicationManager serialPortCommunicationManager;
    private final PuzzleDeviceController puzzleDeviceController;
    private final Logger logger = LogManager.getLogger();
}
