package mongellaz.application;

import java.util.LinkedList;

public class LinkedListArduinoSerialPortMessageListener extends ArduinoSerialPortMessageListener {
    public LinkedListArduinoSerialPortMessageListener() {
        super(new LinkedList<>());
    }
}
