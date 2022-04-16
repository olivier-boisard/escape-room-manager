package mongellaz.devices.bookpuzzle.commands.statusrequest;

public class BookPuzzleStatusRequestFactory {
    public byte[] generate() {
        final byte statusRequest = 0x20;
        final byte endOfTransmission = 0x00;
        return new byte[]{statusRequest, endOfTransmission};
    }
}
