package mongellaz.modules;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceApplication {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new BookPuzzleModule());
        MainFrame mainFrame = injector.getInstance(MainFrame.class);
        mainFrame.start();
    }
}
