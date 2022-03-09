package mongellaz.modules;

import com.google.inject.Inject;

import javax.swing.*;

public class PuzzleUiImpl implements PuzzleUi {
    @Inject
    public PuzzleUiImpl(PuzzleConnectionUi puzzleConnectionUi, PuzzleControlUi puzzleControlUi, PuzzleUiPanel puzzleUiPanel) {
        this.puzzleConnectionUi = puzzleConnectionUi;
        this.puzzleControlUi = puzzleControlUi;
        this.puzzleUiPanel = puzzleUiPanel;
    }

    @Override
    public void start() {
        puzzleUiPanel.add(puzzleConnectionUi.getComponent());
        puzzleUiPanel.add(puzzleControlUi.getComponent());
        frame.setContentPane(puzzleUiPanel.getContentPane());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private final PuzzleConnectionUi puzzleConnectionUi;
    private final PuzzleControlUi puzzleControlUi;
    private final PuzzleUiPanel puzzleUiPanel;
    private final JFrame frame = new JFrame("Puzzle des livres");
}
