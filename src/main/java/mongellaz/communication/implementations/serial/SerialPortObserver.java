package mongellaz.communication.implementations.serial;

import com.fazecast.jSerialComm.SerialPort;

public interface SerialPortObserver {
    void update(SerialPort serialPort);
}
