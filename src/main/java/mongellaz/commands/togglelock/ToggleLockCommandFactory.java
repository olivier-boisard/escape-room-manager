package mongellaz.commands.togglelock;

import mongellaz.commands.ByteArrayFactory;

public class ToggleLockCommandFactory implements ByteArrayFactory {
    @Override
    public byte[] generate() {
        final byte lockCommand = 0x30;
        final byte toggle = 0x03;
        return new byte[]{lockCommand, toggle, END_OF_TRANSMISSION};
    }
}
