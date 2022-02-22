package mongellaz.communication.tests;

import mongellaz.communication.CommunicationException;

@FunctionalInterface
public interface ResponseProcessor {
    void process(final byte[] response) throws CommunicationException;
}
