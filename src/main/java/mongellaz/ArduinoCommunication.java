package mongellaz;

public interface ArduinoCommunication {
    void write(byte[] data) throws ArduinoErrorException;

    byte[] read() throws ArduinoErrorException;
}
