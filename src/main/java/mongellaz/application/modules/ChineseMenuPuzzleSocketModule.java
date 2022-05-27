package mongellaz.application.modules;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import mongellaz.communication.ByteArrayGenerator;
import mongellaz.devices.chinesemenupuzzle.commands.handshake.ChineseMenuPuzzleHandshakeFactory;
import mongellaz.devices.chinesemenupuzzle.commands.statusrequest.ChineseMenuStatusRequestFactory;

public class ChineseMenuPuzzleSocketModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ByteArrayGenerator.class)
                .annotatedWith(Names.named("HeartBeatByteArrayGenerator"))
                .toInstance(new ByteArrayGenerator() {
                    @Override
                    public byte[] generate() {
                        final byte[] handshake = handshakeFactory.generate();
                        final byte[] statusRequest = statusRequestFactory.generate();
                        int handshakeLength = handshake.length;
                        int statusRequestLength = statusRequest.length;
                        byte[] output = new byte[handshakeLength + statusRequestLength];
                        System.arraycopy(handshake, 0, output, 0, handshakeLength);
                        System.arraycopy(statusRequest, 0, output, handshakeLength, statusRequestLength);
                        return output;
                    }

                    private final ChineseMenuPuzzleHandshakeFactory handshakeFactory = new ChineseMenuPuzzleHandshakeFactory();
                    private final ChineseMenuStatusRequestFactory statusRequestFactory = new ChineseMenuStatusRequestFactory();
                });
    }
}
