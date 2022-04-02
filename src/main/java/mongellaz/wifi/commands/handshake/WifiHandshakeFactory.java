package mongellaz.wifi.commands.handshake;

public class WifiHandshakeFactory {
    public byte[] generate() {
        // Variable declarations
        final byte handshakeCode = 0x10;
        final byte[] code = {0x5A, 0x40, (byte) 0x8F, 0x02, 0x3E, 0x7B, (byte) 0xC8, (byte) 0xD2};
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
