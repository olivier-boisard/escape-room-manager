package mongellaz.communication.implementations.wifi.configuration;

import com.google.inject.Inject;
import mongellaz.devices.wifi.commands.connection.ConnectionState;
import mongellaz.devices.wifi.commands.connection.ConnectionStateObserver;
import mongellaz.devices.wifi.commands.connection.WifiConfiguration;
import mongellaz.devices.wifi.commands.connection.WifiConfigurationObserver;
import mongellaz.userinterface.ComponentHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("unused")
public class WifiConfigurationUi implements ComponentHandler, ConnectionStateObserver {

    @Inject
    WifiConfigurationUi(WifiConfigurationObserver wifiConfigurationObserver) {
        wifiConfigurationButton.addActionListener(e -> wifiConfigurationObserver.update(
                new WifiConfiguration(ssidTextField.getText(), passwordTextField.getPassword())
        ));
    }

    @Override
    public void update(ConnectionState connectionState) {
        logger.debug("Received connection state: {}", connectionState);
        if (connectionState.success()) {
            int[] ipAddress = connectionState.ipAddress();
            String ipAddressStr = ipAddress[0] + "." + ipAddress[1] + "." + ipAddress[2] + "." + ipAddress[3];
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "Adresse IP: " + ipAddressStr,
                    "Connexion réussie",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "La connection de la carte au réseau WiFi a échoué",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public Component getMainPanel() {
        return mainPanel;
    }

    private final Logger logger=LogManager.getLogger();
    private JLabel ssidLabel;
    private JLabel passwordLabel;
    private JTextField ssidTextField;
    private JPasswordField passwordTextField;
    private JButton wifiConfigurationButton;
    private JPanel mainPanel;
}
