package mongellaz.modules;

import javax.swing.*;
import java.awt.*;

public class VerticalLayoutPuzzleUi implements PuzzleUiPanel {
    public VerticalLayoutPuzzleUi() {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    }

    @Override
    public void add(Component component) {
        panel.add(component);
    }

    @Override
    public Container getContentPane() {
        return panel;
    }

    private final JPanel panel = new JPanel();
}
