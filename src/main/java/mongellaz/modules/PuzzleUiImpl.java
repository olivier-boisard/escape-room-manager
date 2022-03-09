package mongellaz.modules;

import com.google.inject.Inject;

import javax.swing.*;

public class PuzzleUiImpl implements PuzzleUi {
    @Inject
    public PuzzleUiImpl(PuzzleConnectionUi puzzleConnectionUi, PuzzleControlUi puzzleControlUi) {
        this.puzzleConnectionUi = puzzleConnectionUi;
        this.puzzleControlUi = puzzleControlUi;
    }

    @Override
    public void start() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(puzzleConnectionUi.getComponent());
        panel.add(puzzleControlUi.getComponent());
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private final PuzzleConnectionUi puzzleConnectionUi;
    private final PuzzleControlUi puzzleControlUi;
    private final JFrame frame = new JFrame("Puzzle des livres");
}
