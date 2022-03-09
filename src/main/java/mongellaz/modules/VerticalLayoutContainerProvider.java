package mongellaz.modules;

import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("ClassCanBeRecord")
public class VerticalLayoutContainerProvider implements Provider<Container> {

    @Inject
    VerticalLayoutContainerProvider(Iterable<Component> components) {
        this.components = components;
    }

    @Override
    public Container get() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (Component component : components) {
            panel.add(component);
        }
        return panel;
    }

    private final Iterable<Component> components;
}