package mongellaz.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import mongellaz.userinterface.BookPuzzleControlUi;
import mongellaz.userinterface.SerialPortPuzzleConnectionUi;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class BookPuzzleModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(String.class)
                .annotatedWith(Names.named("MainFrameName"))
                .toInstance("Puzzle des livres");
    }

    @SuppressWarnings("unused")
    @Provides
    private static Iterable<Component> provideComponents() {
        ArrayList<Component> components = new ArrayList<>();
        components.add(new SerialPortPuzzleConnectionUi().getMainPanel());
        components.add(new BookPuzzleControlUi().getMainPanel());
        return components;
    }

    @SuppressWarnings("unused")
    @Provides
    private static Container provideVerticalLayoutContainer(Iterable<Component> components) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (Component component : components) {
            panel.add(component);
        }
        return panel;
    }
}
