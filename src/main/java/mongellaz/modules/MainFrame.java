package mongellaz.modules;

import com.google.inject.ImplementedBy;

@ImplementedBy(MainFrameImpl.class)
public interface MainFrame {
    void start();
}
