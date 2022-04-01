package mongellaz.communication.wifi;

import mongellaz.userinterface.ComponentHandler;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("unused")
public class WifiConfigurationUi implements ComponentHandler {
    @Override
    public Component getMainPanel() {
        return mainPanel;
    }

    private JLabel ssidLabel;
    private JLabel passwordLabel;
    private JTextField ssidTextField;
    private JPasswordField passwordTextField;
    private JButton wifiConfigurationButton;
    private JPanel mainPanel;
}
