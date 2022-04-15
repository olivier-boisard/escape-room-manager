package mongellaz.devices.chinesemenupuzzle;

import mongellaz.userinterface.ComponentHandler;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("unused")
public class ChineseMenuPuzzleUi implements ComponentHandler {
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
