package mongellaz.application;

import javax.swing.*;

public class Ui {
    public Ui(Controller controller) {
        toggleLockButton.addActionListener(e -> controller.sendToggleLockCommand());
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private JPanel mainPanel;
    private JButton toggleLockButton;

}
