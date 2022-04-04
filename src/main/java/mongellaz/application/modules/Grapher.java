package mongellaz.application.modules;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.grapher.graphviz.GraphvizGrapher;
import com.google.inject.grapher.graphviz.GraphvizModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class Grapher {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new BookPuzzleModule());
        String outputFilename = "graph.dot";
        Logger logger = LogManager.getLogger();
        try {
            writeGraph(outputFilename, injector);
            logger.info("Saved graph at '{}'", outputFilename);
        } catch (IOException e) {
            logger.fatal("Could not write graph at '{}'", outputFilename);
            e.printStackTrace();
        }
    }

    private static void writeGraph(String filename, Injector injector) throws IOException {
        PrintWriter out = new PrintWriter(filename, StandardCharsets.UTF_8);

        Injector grapherInjector = Guice.createInjector(new GraphvizModule());
        GraphvizGrapher grapher = grapherInjector.getInstance(GraphvizGrapher.class);
        grapher.setOut(out);
        grapher.setRankdir("TB");
        grapher.graph(injector);
    }
}
