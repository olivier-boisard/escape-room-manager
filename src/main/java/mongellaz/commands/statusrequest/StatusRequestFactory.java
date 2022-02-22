package mongellaz.commands.statusrequest;

import mongellaz.commands.ByteArrayFactory;

public class StatusRequestFactory implements ByteArrayFactory {
    @Override
    public byte[] generate() {
        final byte statusRequest = 0x20;
        return new byte[]{statusRequest, END_OF_TRANSMISSION};
    }
}
