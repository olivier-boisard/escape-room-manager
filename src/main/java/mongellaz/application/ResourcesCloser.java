package mongellaz.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

class ResourcesCloser {

    public ResourcesCloser() {
        this.closeables = new LinkedList<>();
    }

    public void closeResources() {
        for (Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (IOException ex) {
                logger.fatal("Error while closing resource: {}", ex.getMessage());
            }
        }
    }

    public void addCloseable(Closeable closeable) {
        closeables.add(closeable);
    }

    private final List<Closeable> closeables;
    private final Logger logger = LogManager.getLogger();
}
