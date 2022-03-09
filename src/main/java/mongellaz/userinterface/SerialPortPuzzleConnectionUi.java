package mongellaz.userinterface;

import mongellaz.commands.HandshakeResultObserver;
import mongellaz.commands.handshake.HandshakeResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

@SuppressWarnings("unused")
public class SerialPortPuzzleConnectionUi implements HandshakeResultObserver {

    public Component getMainPanel() {
        return mainPanel;
    }

    public void setConnectionOptions(Iterable<String> connectionOptions) {
        connectionOptionsComboBox.removeAllItems();
        for (String connectionOption : connectionOptions) {
            connectionOptionsComboBox.addItem(connectionOption);
        }
    }

    public void addConnectionButtonActionListener(ActionListener actionListener) {
        connectionButton.addActionListener(actionListener);
    }

    public String getSelectedConnectionOption() {
        return (String) connectionOptionsComboBox.getSelectedItem();
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
}
