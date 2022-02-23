package mongellaz.application;

import mongellaz.commands.LockStateObserver;
import mongellaz.commands.ConfigurationModeStateObserver;
import mongellaz.commands.toggleconfigurationmode.ConfigurationModeState;
import mongellaz.commands.togglelock.LockState;

import javax.swing.*;

public class Ui implements LockStateObserver, ConfigurationModeStateObserver {
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

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private JPanel mainPanel;
    private JButton toggleLockButton;
    private JButton toggleConfigurationModeButton;
}
