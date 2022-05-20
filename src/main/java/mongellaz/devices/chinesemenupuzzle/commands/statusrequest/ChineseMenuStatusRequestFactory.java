package mongellaz.devices.chinesemenupuzzle.commands.statusrequest;

import mongellaz.communication.ByteArrayGenerator;

public class ChineseMenuStatusRequestFactory implements ByteArrayGenerator {
    public byte[] generate() {
        final byte statusRequest = 0x20;
        final byte endOfTransmission = 0x00;
        return new byte[]{statusRequest, endOfTransmission};
    }
}
