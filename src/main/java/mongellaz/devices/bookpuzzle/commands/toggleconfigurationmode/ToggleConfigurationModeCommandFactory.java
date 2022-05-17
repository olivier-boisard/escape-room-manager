package mongellaz.devices.bookpuzzle.commands.toggleconfigurationmode;

import mongellaz.communication.ByteArrayGenerator;

public class ToggleConfigurationModeCommandFactory implements ByteArrayGenerator {
    public byte[] generate() {
        final byte lockCommand = 0x40;
        final byte toggle = 0x03;
        final byte endOfTransmission = 0x00;
        return new byte[]{lockCommand, toggle, endOfTransmission};
    }
}
