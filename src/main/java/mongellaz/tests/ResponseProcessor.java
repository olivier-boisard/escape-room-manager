package mongellaz.tests;


@FunctionalInterface
public interface ResponseProcessor {
    void process(final byte[] response);
}
