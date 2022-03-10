package mongellaz.userinterface;

import com.fazecast.jSerialComm.SerialPort;
import com.google.inject.Inject;
import mongellaz.commands.HandshakeResultObserver;
import mongellaz.commands.handshake.HandshakeResult;
import mongellaz.communication.ScheduledCommunicationManager;
import mongellaz.communication.serial.SerialPortCommunicationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

@SuppressWarnings("unused")
public class SerialPortPuzzleConnectionUi implements PuzzleConnectionUi, HandshakeResultObserver {

    @Inject
    SerialPortPuzzleConnectionUi(ScheduledCommunicationManager scheduledCommunicationManager) {
        for (SerialPort serialPort : SerialPort.getCommPorts()) {
            connectionOptionsComboBox.addItem(serialPort.getDescriptivePortName());
        }
        connectionButton.addActionListener(e -> {
            // Inline method
            Object selectedItem = connectionOptionsComboBox.getSelectedItem();

            // Get selected serial port
            SerialPort selectedSerialPort = null;
            for (SerialPort serialPort : SerialPort.getCommPorts()) {
                if (Objects.equals(serialPort.getDescriptivePortName(), selectedItem)) {
                    selectedSerialPort = serialPort;
                    break;
                }
            }

            // Establish connection
            if (selectedSerialPort == null) {
                logger.error("Invalid serial port {}", selectedItem);
            } else {
                logger.info("Establishing connection with serial port: {}", selectedItem);
                if (!selectedSerialPort.openPort()) {
                    logger.error("Could not connect to {}", selectedItem);
                    update(HandshakeResult.FAILURE);
                } else {
                    scheduledCommunicationManager.setCommunicationManager(new SerialPortCommunicationManager(selectedSerialPort));
                    scheduledCommunicationManager.start();
                }
            }
        });
    }

    public Component getMainPanel() {
        return mainPanel;
    }

    @Override
    public void update(HandshakeResult handshakeResult) {
        String connectionStatusString = null;
        Color textColor = null;
        if (handshakeResult == HandshakeResult.SUCCESS) {
            connectionStatusString = "Connecté";
            textColor = Color.GREEN;
        } else if (handshakeResult == HandshakeResult.FAILURE) {
            connectionStatusString = "Non connecté";
            textColor = Color.RED;
        }

        if (connectionStatusString != null) {
            connectionStatus.setText(connectionStatusString);
        }
        if (textColor != null) {
            connectionStatus.setForeground(textColor);
        }
    }

    private JComboBox<String> connectionOptionsComboBox;
    private JLabel connectionOptionLabel;
    private JButton connectionButton;
    private JPanel mainPanel;
    private JLabel connectionStatus;
    private final Logger logger = LogManager.getLogger();
}
