package mongellaz.application;

import mongellaz.commands.HandshakeResultObserver;
import mongellaz.commands.LockStateObserver;
import mongellaz.commands.ConfigurationModeStateObserver;
import mongellaz.commands.handshake.HandshakeResult;
import mongellaz.commands.toggleconfigurationmode.ConfigurationModeState;
import mongellaz.commands.togglelock.LockState;

import javax.swing.*;
import java.awt.*;

public class Ui implements LockStateObserver, ConfigurationModeStateObserver, HandshakeResultObserver {
    public Ui(Controller controller) {
        toggleLockButton.addActionListener(e -> {
            controller.sendToggleLockCommand();
            toggleLockButton.setEnabled(false);
        });

        toggleConfigurationModeButton.addActionListener(e -> {
            controller.sendToggleConfigurationModeCommand();
            toggleConfigurationModeButton.setEnabled(false);
        });
    }

    @Override
    public void update(LockState lockState) {
        String lockButtonNewText = switch (lockState) {
            case OPEN -> "Fermer le verrou";
            case CLOSED -> "Ouvrir le verrou";
        };
        toggleLockButton.setText(lockButtonNewText);
        toggleLockButton.setEnabled(true);
    }

    @Override
    public void update(ConfigurationModeState configurationModeState) {
        String toggleConfigurationModeButtonNewText = switch (configurationModeState) {
            case ENABLED -> "Désactiver le mode configuration";
            case DISABLED -> "Activer le mode configuration";
        };
        toggleConfigurationModeButton.setText(toggleConfigurationModeButtonNewText);
        toggleConfigurationModeButton.setEnabled(true);
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
            connectionStateTextLabel.setText(connectionStatusString);
        }
        if (textColor != null) {
            connectionStateTextLabel.setForeground(textColor);
        }
    }

    public void setConnectionStateToConnecting() {
        connectionStateTextLabel.setText("Connection...");
        connectionStateTextLabel.setForeground(Color.GRAY);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private JPanel mainPanel;
    private JButton toggleLockButton;
    private JButton toggleConfigurationModeButton;
    @SuppressWarnings("unused")
    private JLabel connectionStateText;
    private JLabel connectionStateTextLabel;
}
