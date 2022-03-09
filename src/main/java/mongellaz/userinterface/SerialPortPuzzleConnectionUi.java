package mongellaz.userinterface;

import com.fazecast.jSerialComm.SerialPort;
import mongellaz.commands.HandshakeResultObserver;
import mongellaz.commands.handshake.HandshakeResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

@SuppressWarnings("unused")
public class SerialPortPuzzleConnectionUi implements HandshakeResultObserver {

    public SerialPortPuzzleConnectionUi() {
        for (SerialPort serialPort : SerialPort.getCommPorts()) {
            connectionOptionsComboBox.addItem(serialPort.getDescriptivePortName());
        }
        connectionButton.addActionListener(e -> {
            Object selectedItem = getSelectedItem();
            updateSelectedSerialPort(selectedItem);
            establishConnection(selectedItem);
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

    private void establishConnection(Object selectedItem) {
        if (selectedSerialPort == null) {
            logger.error("Invalid serial port {}", selectedItem);
        } else {
            logger.info("Establishing connection with serial port: {}", selectedItem);
            if (!selectedSerialPort.openPort()) {
                logger.error("Could not connect to {}", selectedItem);
                update(HandshakeResult.FAILURE);
            }
        }
    }

    private Object getSelectedItem() {
        return connectionOptionsComboBox.getSelectedItem();
    }

    private void updateSelectedSerialPort(Object selectedItem) {
        selectedSerialPort = null;
        for (SerialPort serialPort : SerialPort.getCommPorts()) {
            if (Objects.equals(serialPort.getDescriptivePortName(), selectedItem)) {
                selectedSerialPort = serialPort;
                break;
            }
        }
    }

    private JComboBox<String> connectionOptionsComboBox;
    private JLabel connectionOptionLabel;
    private JButton connectionButton;
    private JPanel mainPanel;
    private JLabel connectionStatus;
    private SerialPort selectedSerialPort;
    private final Logger logger = LogManager.getLogger();
}
