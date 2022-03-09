package mongellaz.modules;

import com.google.inject.Provider;
import mongellaz.userinterface.BookPuzzleControlUi;
import mongellaz.userinterface.SerialPortPuzzleConnectionUi;

import java.awt.*;
import java.util.ArrayList;

public class BookPuzzleUiComponentsProvider implements Provider<Iterable<Component>> {

    @Override
    public Iterable<Component> get() {
        ArrayList<Component> components = new ArrayList<>();
        components.add(new SerialPortPuzzleConnectionUi().getMainPanel());
        components.add(new BookPuzzleControlUi().getMainPanel());
        return components;
    }
}
