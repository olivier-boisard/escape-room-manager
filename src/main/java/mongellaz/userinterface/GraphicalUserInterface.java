package mongellaz.userinterface;

import com.google.inject.name.Named;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;

public class GraphicalUserInterface implements UserInterface {

    public GraphicalUserInterface(Container container, @Named("MainFrameName") String name) {
        this.container = container;
        this.frame = new JFrame(name);
        // Using JFrame directly here seems to break the DIP, however implementing it blindly here would not help.
        // This class is self-contained - if JFrame is not the right choice anymore, it only needs to be changed here.
    }

    @Override
    public void start() {
        frame.setContentPane(container);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void addWindowListener(WindowAdapter event) {
        frame.addWindowListener(event);
    }

    private final Container container;
    private final JFrame frame;
}
