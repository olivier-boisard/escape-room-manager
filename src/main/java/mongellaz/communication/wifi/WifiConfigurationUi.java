package mongellaz.communication.wifi;

import com.google.inject.Inject;
import mongellaz.userinterface.ComponentHandler;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("unused")
public class WifiConfigurationUi implements ComponentHandler {

    @Inject
    WifiConfigurationUi(WifiConfigurationObserver wifiConfigurationObserver) {
        wifiConfigurationButton.addActionListener(e -> wifiConfigurationObserver.update(
                new WifiConfiguration(ssidTextField.getText(), passwordTextField.getPassword())
        ));
    }

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
