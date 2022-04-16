package mongellaz.devices.chinesemenupuzzle;

import com.google.inject.Inject;
import mongellaz.devices.chinesemenupuzzle.devicecontroller.ChineseMenuDeviceController;
import mongellaz.devices.common.togglelock.LockState;
import mongellaz.devices.common.togglelock.LockStateObserver;
import mongellaz.userinterface.ComponentHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("unused")
public class ChineseMenuPuzzleControlUi implements ComponentHandler, LockStateObserver {
    @Inject
    ChineseMenuPuzzleControlUi(ChineseMenuDeviceController chineseMenuDeviceController) {
        toggleLockButton.addActionListener(e -> {
            chineseMenuDeviceController.sendToggleLockCommand();
            toggleLockButton.setEnabled(false);
        });
    }

    @Override
    public void update(LockState lockState) {
        updateLockButton(lockState);
        updateLockStatus(lockState);
    }

    @Override
    public Component getMainPanel() {
        return mainPanel;
    }

    private void updateLockButton(LockState lockState) {
        String lockButtonNewText = switch (lockState) {
            case OPEN -> "Fermer le verrou";
            case CLOSED -> "Ouvrir le verrou";
        };
        toggleLockButton.setText(lockButtonNewText);
        toggleLockButton.setEnabled(true);
        logger.info("Enabled lock button");
    }

    private void updateLockStatus(LockState lockState) {
        String lockStateTextValueText;
        Color textColor;
        if (lockState == LockState.OPEN) {
            lockStateTextValueText = "Ouvert";
            textColor = Color.GREEN;
        } else {
            lockStateTextValueText = "Fermé";
            textColor = Color.RED;
        }
        lockStateTextValue.setText(lockStateTextValueText);
        lockStateTextValue.setForeground(textColor);
        logger.info("Set lock status text to '{}'", lockStateTextValueText);
    }

    private JPanel mainPanel;
    private JLabel currentWeightLabel;
    private JLabel currentWeightValueLabel;
    private JLabel minWeightLabel;
    private JLabel maxWeightLabel;
    private JLabel maxWeightValueLabel;
    private JLabel minWeightValueLabel;
    private JButton toggleLockButton;
    private JLabel lockStateText;
    private JLabel lockStateTextValue;
    private final Logger logger = LogManager.getLogger();
}
