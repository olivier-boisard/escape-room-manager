package mongellaz;

public interface CommunicationManager {
    void write(byte[] data) throws ArduinoErrorException;

    byte[] read() throws ArduinoErrorException;
}
