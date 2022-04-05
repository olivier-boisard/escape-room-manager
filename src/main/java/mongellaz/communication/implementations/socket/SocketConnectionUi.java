package mongellaz.communication.implementations.socket;

import mongellaz.communication.handshake.HandshakeResult;
import mongellaz.communication.handshake.HandshakeResultObserver;
import mongellaz.userinterface.ComponentHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;

@SuppressWarnings("unused")
public class SocketConnectionUi implements ComponentHandler, HandshakeResultObserver {

    SocketConnectionUi(SocketObserver socketObserver) {
        int serverPort = 165;
        connectionButton.addActionListener(e -> {
            String ipAddress = ipAddressField.getText();
            try {
                Socket socket = new Socket(ipAddress, serverPort);
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
    private JTextField ipAddressField;
    private JButton connectionButton;
    private JPanel mainPanel;
    private JLabel connectionStatus;
    private final Logger logger = LogManager.getLogger();
}
