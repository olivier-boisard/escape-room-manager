package mongellaz.application.modules;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import mongellaz.communication.ByteArrayGenerator;
import mongellaz.devices.chinesemenupuzzle.commands.statusrequest.ChineseMenuStatusRequestFactory;

public class ChineseMenuPuzzleSocketModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ByteArrayGenerator.class)
                .annotatedWith(Names.named("HeartBeatByteArrayGenerator"))
                .to(ChineseMenuStatusRequestFactory.class);
    }
}
