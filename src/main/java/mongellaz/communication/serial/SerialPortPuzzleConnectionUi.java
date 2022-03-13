package mongellaz.communication.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import com.google.inject.Inject;
import mongellaz.bookpuzzle.commands.handshake.HandshakeResultObserver;
import mongellaz.bookpuzzle.commands.handshake.HandshakeResult;
import mongellaz.communication.manager.ScheduledCommunicationManager;
import mongellaz.communication.serial.SerialPortCommunicationManager;
import mongellaz.devicecontroller.PuzzleDeviceController;
import mongellaz.userinterface.ComponentHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

@SuppressWarnings("unused")
public class SerialPortPuzzleConnectionUi implements ComponentHandler, HandshakeResultObserver {

    @Inject
    SerialPortPuzzleConnectionUi(SerialPortObserver serialPortObserver) {
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
                serialPortObserver.update(selectedSerialPort);
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
