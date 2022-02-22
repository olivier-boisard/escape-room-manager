package mongellaz.commands.handshake;

import mongellaz.commands.ByteArrayFactory;

public class HandshakeFactory implements ByteArrayFactory {
    @Override
    public byte[] generate() {
        // Variable declarations
        final byte handshakeCode = 0x10;
        final byte[] softwareId = {0x01, (byte) 0xEE, 0x35, (byte) 0xD7, 0x2A, (byte) 0x80, 0x58, (byte) 0xEA};

        // Create byte array with data
        byte[] writeBuffer = new byte[softwareId.length + 2];
        writeBuffer[0] = handshakeCode;
        System.arraycopy(softwareId, 0, writeBuffer, 1, softwareId.length);
        writeBuffer[writeBuffer.length - 1] = END_OF_TRANSMISSION;

        // Return
        return writeBuffer;
    }
}
