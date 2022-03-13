package mongellaz.communication.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import com.google.inject.Inject;
import mongellaz.communication.manager.ScheduledCommunicationManager;
import mongellaz.devicecontroller.PuzzleDeviceController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SerialPortObserverImpl implements SerialPortObserver {

    @Inject
    SerialPortObserverImpl(
            ScheduledCommunicationManager scheduledCommunicationManager,
            SerialPortMessageListener serialPortMessageListener,
            PuzzleDeviceController puzzleDeviceController
    ) {
        this.scheduledCommunicationManager = scheduledCommunicationManager;
        this.serialPortMessageListener = serialPortMessageListener;
        this.puzzleDeviceController = puzzleDeviceController;
    }

    @Override
    public void update(SerialPort serialPort) {
        logger.info("Establishing connection with serial port");
        if (!serialPort.openPort()) {
            logger.error("Could not connect to serial port");
        } else {
            serialPort.addDataListener(serialPortMessageListener);
            scheduledCommunicationManager.updateCommunicationManager(new SerialPortCommunicationManager(serialPort));
            puzzleDeviceController.start();
        }
    }

    private final ScheduledCommunicationManager scheduledCommunicationManager;
    private final SerialPortMessageListener serialPortMessageListener;
    private final PuzzleDeviceController puzzleDeviceController;
    private final Logger logger = LogManager.getLogger();
}
