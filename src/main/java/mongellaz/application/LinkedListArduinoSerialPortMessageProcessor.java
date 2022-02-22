package mongellaz.application;

import com.fazecast.jSerialComm.SerialPort;

import java.util.LinkedList;

public class LinkedListArduinoSerialPortMessageProcessor extends ArduinoSerialPortMessageProcessor {
    public LinkedListArduinoSerialPortMessageProcessor(SerialPort serialPort) {
        super(serialPort, new LinkedList<>());
    }
}
