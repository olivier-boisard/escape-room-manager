package mongellaz.application;

import mongellaz.commands.LockStateObserver;
import mongellaz.commands.togglelock.LockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

public class Ui implements LockStateObserver {
    public Ui(Controller controller) {
        toggleLockButton.addActionListener(e -> {
            controller.sendToggleLockCommand();
            toggleLockButton.setEnabled(false);
        });
    }

    @Override
    public void update(LockState lockState) {
        String lockButtonNewText = null;
        switch (lockState) {
            case OPEN -> {
                lockButtonNewText = "Fermer le verrou";
            }
            case CLOSED -> {
                lockButtonNewText = "Ouvrir le verrou";
            }
            default -> {
                logger.error("Unknown status {}", lockState);
            }
        }
        if (lockButtonNewText != null) {
            toggleLockButton.setText(lockButtonNewText);
            toggleLockButton.setEnabled(true);
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private JPanel mainPanel;
    private JButton toggleLockButton;
    private final Logger logger = LogManager.getLogger();
}
