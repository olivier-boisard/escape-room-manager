package mongellaz.commands;


@FunctionalInterface
public interface ResponseProcessor {
    void process(final byte[] response);
}
