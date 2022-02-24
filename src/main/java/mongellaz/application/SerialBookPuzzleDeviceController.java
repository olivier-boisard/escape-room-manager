package mongellaz.application;

import com.fazecast.jSerialComm.SerialPort;
import mongellaz.bookpuzzle.BookPuzzleDeviceController;
import mongellaz.commands.*;
import mongellaz.commands.handshake.HandshakeFactory;
import mongellaz.commands.handshake.HandshakeResponseProcessor;
import mongellaz.commands.statusrequest.StatusRequestFactory;
import mongellaz.commands.statusrequest.StatusRequestResponseProcessor;
import mongellaz.commands.toggleconfigurationmode.ToggleConfigurationModeCommandFactory;
import mongellaz.commands.toggleconfigurationmode.ToggleConfigurationModeResponseProcessor;
import mongellaz.commands.togglelock.ToggleLockCommandFactory;
import mongellaz.commands.togglelock.ToggleLockResponseProcessor;
import mongellaz.communication.ByteArrayObserver;

import java.util.ArrayList;
import java.util.List;

//TODO refactor this class
public class SerialBookPuzzleDeviceController implements BookPuzzleDeviceController {

    public SerialBookPuzzleDeviceController(ByteArrayObserver commandHandler) {
        this.commandHandler = commandHandler;
    }

    public void start() {
        commandHandler.update(new HandshakeFactory().generate());
        commandHandler.update(statusRequestFactory.generate());
    }

    public void addBookPuzzleDeviceStateObserver(BookPuzzleDeviceStateObserver bookPuzzleDeviceStateObserver) {
        handshakeResponseProcessor.addHandshakeResultObserver(bookPuzzleDeviceStateObserver);
        toggleLockResponseProcessor.addLockStateObserver(bookPuzzleDeviceStateObserver);
        toggleConfigurationModeResponseProcessor.addConfigurationModeStateObserver(bookPuzzleDeviceStateObserver);
        statusRequestResponseProcessor.addPiccReaderStatusesObserver(bookPuzzleDeviceStateObserver);

        statusRequestResponseProcessor.addLockStateObserver(bookPuzzleDeviceStateObserver);
        statusRequestResponseProcessor.addConfigurationModeStateObserver(bookPuzzleDeviceStateObserver);
    }

    @Override
    public List<String> getConnectionOptions() {
        ArrayList<String> connectionOptions = new ArrayList<>();
        for (SerialPort serialPort : SerialPort.getCommPorts()) {
            connectionOptions.add(serialPort.getDescriptivePortName());
        }
        return connectionOptions;
    }

    @Override
    public void sendToggleLockCommand() {
        commandHandler.update(toggleLockCommandFactory.generate());
    }

    @Override
    public void sendToggleConfigurationModeCommand() {
        commandHandler.update(toggleConfigurationModeCommandFactory.generate());
    }

    public void setHandshakeResponseProcessor(HandshakeResponseProcessor handshakeResponseProcessor) {
        this.handshakeResponseProcessor = handshakeResponseProcessor;
    }

    public void setToggleLockResponseProcessor(ToggleLockResponseProcessor toggleLockResponseProcessor) {
        this.toggleLockResponseProcessor = toggleLockResponseProcessor;
    }

    public void setToggleConfigurationModeResponseProcessor(ToggleConfigurationModeResponseProcessor toggleConfigurationModeResponseProcessor) {
        this.toggleConfigurationModeResponseProcessor = toggleConfigurationModeResponseProcessor;
    }

    public void setStatusRequestResponseProcessor(StatusRequestResponseProcessor statusRequestResponseProcessor) {
        this.statusRequestResponseProcessor = statusRequestResponseProcessor;
    }

    private final StatusRequestFactory statusRequestFactory = new StatusRequestFactory();
    private final ToggleLockCommandFactory toggleLockCommandFactory = new ToggleLockCommandFactory();
    private final ToggleConfigurationModeCommandFactory toggleConfigurationModeCommandFactory = new ToggleConfigurationModeCommandFactory();
    private HandshakeResponseProcessor handshakeResponseProcessor;
    private ToggleLockResponseProcessor toggleLockResponseProcessor;
    private ToggleConfigurationModeResponseProcessor toggleConfigurationModeResponseProcessor;
    private StatusRequestResponseProcessor statusRequestResponseProcessor;

    private final ByteArrayObserver commandHandler;
}
