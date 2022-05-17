package mongellaz.devices.bookpuzzle.commands.handshake;

import mongellaz.communication.ByteArrayGenerator;

public class BookPuzzleHandshakeFactory implements ByteArrayGenerator {
    @Override
    public byte[] generate() {
        // Variable declarations
        final byte handshakeCode = 0x10;
        final byte[] code = {0x01, (byte) 0xEE, 0x35, (byte) 0xD7, 0x2A, (byte) 0x80, 0x58, (byte) 0xEA};
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
