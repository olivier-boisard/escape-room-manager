package mongellaz.devices.chinesemenupuzzle;

import com.google.inject.Inject;
import mongellaz.devices.chinesemenupuzzle.devicecontroller.ChineseMenuDeviceController;
import mongellaz.userinterface.ComponentHandler;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("unused")
public class ChineseMenuPuzzleUi implements ComponentHandler {
    @Inject
    ChineseMenuPuzzleUi(ChineseMenuDeviceController chineseMenuDeviceController) {
        //TODO
    }

    @Override
    public Component getMainPanel() {
        return mainPanel;
    }

    private JPanel mainPanel;
    private JLabel currentWeightLabel;
    private JLabel currentWeightValueLabel;
    private JLabel minWeightLabel;
    private JLabel maxWeightLabel;
    private JLabel maxWeightValueLabel;

    private JLabel minWeightValueLabel;
}
