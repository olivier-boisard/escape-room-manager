package mongellaz.communication.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.google.inject.Inject;
import mongellaz.devicecontroller.DeviceController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SerialPortConnector implements SerialPortObserver {

    @Inject
    SerialPortConnector(
            SerialPortCommunicationManager serialPortCommunicationManager,
            DeviceController deviceController
    ) {
        this.serialPortCommunicationManager = serialPortCommunicationManager;
        this.deviceController = deviceController;
    }

    @Override
    public void update(SerialPort serialPort) {
        logger.info("Establishing connection with serial port");
        if (!serialPort.openPort()) {
            logger.error("Could not connect to serial port");
        } else {
            serialPort.addDataListener(serialPortCommunicationManager.serialPortMessageListener);
            serialPortCommunicationManager.scheduledQueuedCommandSender.updateQueuedCommandSender(new SerialPortQueuedCommandSender(serialPort));
            deviceController.start();
        }
    }

    private final SerialPortCommunicationManager serialPortCommunicationManager;
    private final DeviceController deviceController;
    private final Logger logger = LogManager.getLogger();
}
