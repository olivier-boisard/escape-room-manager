package mongellaz.communication.tests;


@FunctionalInterface
public interface ResponseProcessor {
    void process(final byte[] response);
}
