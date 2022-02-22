package mongellaz.commands.toggleconfigurationmode;

import mongellaz.commands.ByteArrayFactory;

public class ToggleConfigurationModeCommandFactory implements ByteArrayFactory {
    @Override
    public byte[] generate() {
        final byte lockCommand = 0x40;
        final byte toggle = 0x03;
        return new byte[]{lockCommand, toggle, END_OF_TRANSMISSION};
    }
}
