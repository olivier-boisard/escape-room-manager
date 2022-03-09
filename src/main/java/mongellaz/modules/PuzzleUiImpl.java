package mongellaz.modules;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import javax.swing.*;
import java.awt.*;

public class PuzzleUiImpl implements PuzzleUi {
    @Inject
    public PuzzleUiImpl(Iterable<Component> components, PuzzleUiPanel puzzleUiPanel, @Named("PuzzleName") String puzzleName) {
        this.components = components;
        this.puzzleUiPanel = puzzleUiPanel;
        frame = new JFrame(puzzleName);
    }

    @Override
    public void start() {
        for (Component component : components) {
            puzzleUiPanel.add(component);
        }
        frame.setContentPane(puzzleUiPanel.getContentPane());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private final Iterable<Component> components;
    private final PuzzleUiPanel puzzleUiPanel;
    private final JFrame frame;
}
