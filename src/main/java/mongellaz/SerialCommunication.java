package mongellaz;

import com.fazecast.jSerialComm.*;

import java.util.Arrays;

public class SerialCommunication {
    public static void main(String[] args) {
        SerialPort comPort = SerialPort.getCommPorts()[0];
        if (!comPort.openPort()){
            //TODO manage error
        }

        try {
            boolean stop = false;
            while (!stop) {
                byte[] writeBuffer = new byte[]{0x06};
                comPort.writeBytes(writeBuffer, writeBuffer.length);
                if (comPort.bytesAvailable() == 0) {
                    Thread.sleep(200);
                    continue;
                }

                byte[] readBuffer = new byte[comPort.bytesAvailable()];
                int numRead = comPort.readBytes(readBuffer, comPort.bytesAvailable());
                System.out.println("Read " + numRead + " bytes.");
                System.out.println(Arrays.toString(Arrays.copyOfRange(readBuffer, 0, numRead)));
                stop = true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        comPort.closePort();
    }
}
