package mongellaz.devices.chinesemenupuzzle.commands.handshake;

import mongellaz.communication.ByteArrayGenerator;

public class ChineseMenuPuzzleHandshakeFactory implements ByteArrayGenerator {
    public byte[] generate() {
        // Variable declarations
        final byte handshakeCode = 0x10;
        final byte[] code = {0x4F, 0x2E, (byte) 0xC9, 0x1A, 0x31, 0x58, 0x61, 0x47};
        final byte endOfTransmission = 0x00;

        // Create byte array with data
        byte[] writeBuffer = new byte[code.length + 2];
        writeBuffer[0] = handshakeCode;
        System.arraycopy(code, 0, writeBuffer, 1, code.length);
        writeBuffer[writeBuffer.length - 1] = endOfTransmission;

        // Return
        return writeBuffer;
    }
}
