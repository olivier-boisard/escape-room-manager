package mongellaz.commands.toggleconfigurationmode;

public class ToggleConfigurationModeCommandFactory {
    public byte[] generate() {
        final byte lockCommand = 0x40;
        final byte toggle = 0x03;
        final byte endOfTransmission = 0x00;
        return new byte[]{lockCommand, toggle, endOfTransmission};
    }
}
