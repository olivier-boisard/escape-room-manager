package mongellaz.communication.tests;

public interface ResponseProcessor {
    void process(final byte[] response);
}
