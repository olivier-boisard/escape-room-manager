package mongellaz.modules;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import java.awt.*;

public class BookPuzzleModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PuzzleUi.class).to(PuzzleUiImpl.class);
        bind(new TypeLiteral<Iterable<Component>>() {}).toProvider(BookPuzzleUiComponentsProvider.class);
        bind(PuzzleUiPanel.class).to(VerticalLayoutPuzzleUi.class);
        bind(String.class)
                .annotatedWith(Names.named("PuzzleName"))
                .toInstance("Puzzle des livres");
    }
}
