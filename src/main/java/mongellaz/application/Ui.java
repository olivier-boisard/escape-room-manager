package mongellaz.application;

import javax.swing.*;

public class Ui {
    public Ui(Controller controller) {
        toggleLockButton.addActionListener(e -> {
            controller.sendToggleLockCommand();
            toggleLockButton.setEnabled(false);
        });
    }

    public void setToggleLockButtonText(String text) {
        toggleLockButton.setText(text);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private JPanel mainPanel;
    private JButton toggleLockButton;

}
