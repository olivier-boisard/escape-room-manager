package mongellaz.modules;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import javax.swing.*;
import java.awt.*;

public class PuzzleUiImpl implements PuzzleUi {
    @Inject
    public PuzzleUiImpl(
            @Named("PuzzleConnectionUi") Component puzzleConnectionUi,
            @Named("PuzzleControlUi") Component puzzleControlUi,
            PuzzleUiPanel puzzleUiPanel,
            @Named("PuzzleName") String puzzleName
    ) {
        this.puzzleConnectionUi = puzzleConnectionUi;
        this.puzzleControlUi = puzzleControlUi;
        this.puzzleUiPanel = puzzleUiPanel;
        frame = new JFrame(puzzleName);
    }

    @Override
    public void start() {
        puzzleUiPanel.add(puzzleConnectionUi);
        puzzleUiPanel.add(puzzleControlUi);
        frame.setContentPane(puzzleUiPanel.getContentPane());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private final Component puzzleConnectionUi;
    private final Component puzzleControlUi;
    private final PuzzleUiPanel puzzleUiPanel;
    private final JFrame frame;
}
