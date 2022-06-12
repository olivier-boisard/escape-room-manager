package mongellaz.application.modules;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import mongellaz.communication.ByteArrayGenerator;
import mongellaz.devices.bookpuzzle.commands.handshake.BookPuzzleHandshakeFactory;

public class BookPuzzleSocketModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ByteArrayGenerator.class)
                .annotatedWith(Names.named("HeartBeatByteArrayGenerator"))
                .to(BookPuzzleHandshakeFactory.class);
    }
}
