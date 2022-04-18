package mongellaz.devices.common.togglelock;

@SuppressWarnings("ClassCanBeRecord")
public class ToggleLockCommandFactory {

    public ToggleLockCommandFactory(byte lockCommand) {
        this.lockCommand = lockCommand;
    }

    public byte[] generate() {
        final byte toggle = 0x03;
        final byte endOfTransmission = 0x00;
        return new byte[]{lockCommand, toggle, endOfTransmission};
    }

    final byte lockCommand;
}
