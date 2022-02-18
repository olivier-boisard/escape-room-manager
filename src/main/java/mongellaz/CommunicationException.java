package mongellaz;


public class CommunicationException extends Exception {

    public CommunicationException() {
        super();
    }

    public CommunicationException(String message) {
        super(message);
    }

    public CommunicationException(Throwable e) {
        super(e);
    }
}
