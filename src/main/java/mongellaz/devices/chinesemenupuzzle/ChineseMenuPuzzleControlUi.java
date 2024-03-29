package mongellaz.devices.chinesemenupuzzle;

import com.google.inject.Inject;
import mongellaz.devices.chinesemenupuzzle.commands.configure.ChineseMenuConfigurationObserver;
import mongellaz.devices.chinesemenupuzzle.commands.statusrequest.ChineseMenuWeightObserver;
import mongellaz.devices.chinesemenupuzzle.commands.configure.ChineseMenuConfiguration;
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
        configureToggleLockButton(chineseMenuDeviceController);
        configureSendConfigurationButton(chineseMenuDeviceController);
    }

    @Override
    public void update(LockState lockState) {
        logger.debug("Update lock state: {}", lockState);
        updateLockButton(lockState);
        updateLockStatus(lockState);
    }

    @Override
    public void update(ChineseMenuConfiguration chineseMenuConfiguration) {
        logger.debug("Update configuration");
        updateCurrentMinWeightInGrams(chineseMenuConfiguration.minWeightInGrams());
        updateCurrentMaxWeightInGrams(chineseMenuConfiguration.maxWeightInGrams());
        updateMinTimeIntervalMs(chineseMenuConfiguration.holdingTimeMs());
        sendConfigurationButton.setEnabled(true);
    }

    private void configureToggleLockButton(ChineseMenuDeviceController chineseMenuDeviceController) {
        toggleLockButton.addActionListener(e -> {
            chineseMenuDeviceController.sendToggleLockCommand();
            toggleLockButton.setEnabled(false);
        });
    }

    private void configureSendConfigurationButton(ChineseMenuDeviceController chineseMenuDeviceController) {
        sendConfigurationButton.addActionListener(e -> chineseMenuDeviceController.sendConfiguration(getConfiguration()));
    }

    private ChineseMenuConfiguration getConfiguration() {
        return new ChineseMenuConfiguration(
                (Integer) minWeightSpinner.getValue(),
                (Integer) maxWeightSpinner.getValue(),
                (Integer) minTimeIntervalSpinner.getValue()
        );
    }

    private void updateCurrentMinWeightInGrams(int currentMinWeightInGrams) {
        logger.debug("Update current min weight in grams:{}", currentMinWeightInGrams);
        minWeightValueLabel.setText(String.valueOf(currentMinWeightInGrams));
    }

    private void updateCurrentMaxWeightInGrams(int currentMaxWeightInGrams) {
        logger.debug("Update current max weight in grams:{}", currentMaxWeightInGrams);
        maxWeightValueLabel.setText(String.valueOf(currentMaxWeightInGrams));
    }

    private void updateMinTimeIntervalMs(int currentMinTimeIntervalInMs) {
        logger.debug("Update current min interval in ms: {}", currentMinTimeIntervalInMs);
        minTimeIntervalValueLabel.setText(String.valueOf(currentMinTimeIntervalInMs));
    }

    @Override
    public void update(int weightInGrams) {
        logger.debug("Update weight in grams: {}", weightInGrams);
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
