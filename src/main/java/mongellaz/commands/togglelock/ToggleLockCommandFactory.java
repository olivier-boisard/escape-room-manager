package mongellaz.commands.togglelock;

public class ToggleLockCommandFactory {
    public byte[] generate() {
        final byte lockCommand = 0x30;
        final byte toggle = 0x03;
        final byte endOfTransmission = 0x00;
        return new byte[]{lockCommand, toggle, endOfTransmission};
    }
}
