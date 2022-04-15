package mongellaz.communication.implementations.socket;

import com.google.inject.Inject;
import mongellaz.communication.handshake.HandshakeResult;
import mongellaz.communication.handshake.HandshakeResultObserver;
import mongellaz.userinterface.ComponentHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;

@SuppressWarnings("unused")
public class SocketConnectionUi implements ComponentHandler, HandshakeResultObserver {

    @Inject
    SocketConnectionUi(SocketObserver socketObserver, @Nullable SocketConfigurationHandler socketConfigurationHandler) {
        int serverPort = 165;
        if (socketConfigurationHandler != null) {
            socketHostNameTextField.setText(socketConfigurationHandler.getHostName());
        }
        connectionButton.addActionListener(e -> {
            String hostName = socketHostNameTextField.getText();
            try {
                if (socketConfigurationHandler != null) {
                    socketConfigurationHandler.setHostName(hostName);
                }
                Socket socket = new Socket(hostName, serverPort);
                socketObserver.update(socket);
            } catch (IOException ex) {
                logger.error("Could not connect to socket: {}", ex.getMessage());
                connectionStatus.setText("Non connecté");
                connectionStatus.setForeground(Color.RED);
            }
        });
    }

    @Override
    public void update(HandshakeResult handshakeResult) {
        String connectionStatusString = null;
        Color textColor = null;
        if (handshakeResult == HandshakeResult.SUCCESS) {
            connectionStatusString = "Connecté";
            textColor = Color.GREEN;
        } else if (handshakeResult == HandshakeResult.FAILURE) {
            connectionStatusString = "Non connecté";
            textColor = Color.RED;
        }

        if (connectionStatusString != null) {
            connectionStatus.setText(connectionStatusString);
        }
        if (textColor != null) {
            connectionStatus.setForeground(textColor);
        }

        connectionButton.setEnabled(true);
    }

    @Override
    public Component getMainPanel() {
        return mainPanel;
    }

    private JLabel ipAddressLabel;
    private JTextField socketHostNameTextField;
    private JButton connectionButton;
    private JPanel mainPanel;
    private JLabel connectionStatus;
    private SocketConfigurationHandler socketConfigurationHandler;
    private final Logger logger = LogManager.getLogger();
}
