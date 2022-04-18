package mongellaz.devices.chinesemenupuzzle;

import com.google.inject.Inject;
import mongellaz.devices.chinesemenupuzzle.commands.statusrequest.ChineseMenuConfigurationObserver;
import mongellaz.devices.chinesemenupuzzle.commands.statusrequest.ChineseMenuWeightObserver;
import mongellaz.devices.chinesemenupuzzle.devicecontroller.ChineseMenuConfiguration;
import mongellaz.devices.chinesemenupuzzle.devicecontroller.ChineseMenuDeviceController;
import mongellaz.devices.common.togglelock.LockState;
import mongellaz.devices.common.togglelock.LockStateObserver;
import mongellaz.userinterface.ComponentHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("unused")
public class ChineseMenuPuzzleControlUi implements ComponentHandler, LockStateObserver, ChineseMenuConfigurationObserver, ChineseMenuWeightObserver {

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
    public void update(ChineseMenuConfiguration chineseMenuConfiguration) {
        updateCurrentMinWeightInGrams(chineseMenuConfiguration.minWeightInGrams());
        updateCurrentMaxWeightInGrams(chineseMenuConfiguration.maxWeightInGrams());
        updateMinTimeIntervalMs(chineseMenuConfiguration.holdingTimeMs());
        sendConfigurationButton.setEnabled(true);
    }

    private void updateCurrentMinWeightInGrams(int currentMinWeightInGrams) {
        minWeightValueLabel.setText(String.valueOf(currentMinWeightInGrams));
        minWeightSpinner.setModel(createSpinnerNumberModel(currentMinWeightInGrams));
        minWeightSpinner.setEnabled(true);
    }

    private void updateCurrentMaxWeightInGrams(int currentMaxWeightInGrams) {
        maxWeightValueLabel.setText(String.valueOf(currentMaxWeightInGrams));
        maxWeightSpinner.setModel(createSpinnerNumberModel(currentMaxWeightInGrams));
        maxWeightSpinner.setEnabled(true);
    }

    private void updateMinTimeIntervalMs(int currentMinTimeIntervalInMs) {
        minTimeIntervalValueLabel.setText(String.valueOf(currentMinTimeIntervalInMs));
        minTimeIntervalSpinner.setModel(createSpinnerNumberModel(currentMinTimeIntervalInMs));
        minTimeIntervalSpinner.setEnabled(true);
    }

    @Override
    public void update(int weightInGrams) {
        currentWeightValueLabel.setText(weightInGrams + "g");
    }

    @Override
    public Component getMainPanel() {
        return mainPanel;
    }

    private SpinnerNumberModel createSpinnerNumberModel(int currentMinWeightInGrams) {
        int minWeightInGrams = 0;
        int maxWeightInGrams = 20000;
        int stepSize = 1;
        return new SpinnerNumberModel(currentMinWeightInGrams, minWeightInGrams, maxWeightInGrams, stepSize);
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
    private JLabel minTimeIntervalLabel;

    private JLabel minTimeIntervalValueLabel;
    private JSpinner minWeightSpinner;
    private JSpinner maxWeightSpinner;
    private JSpinner minTimeIntervalSpinner;
    private JButton sendConfigurationButton;

    private final Logger logger = LogManager.getLogger();
}
