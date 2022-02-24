package mongellaz.application;

import mongellaz.commands.ConfigurationModeStateObserver;
import mongellaz.commands.HandshakeResultObserver;
import mongellaz.commands.LockStateObserver;
import mongellaz.commands.PiccReaderStatusesObserver;
import mongellaz.commands.handshake.HandshakeResult;
import mongellaz.commands.statusrequest.PiccReaderStatus;
import mongellaz.commands.toggleconfigurationmode.ConfigurationModeState;
import mongellaz.commands.togglelock.LockState;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

@SuppressWarnings("unused")
public class Ui implements LockStateObserver, ConfigurationModeStateObserver, HandshakeResultObserver, PiccReaderStatusesObserver {
    public Ui(Controller controller) {
        toggleLockButton.addActionListener(e -> {
            controller.sendToggleLockCommand();
            toggleLockButton.setEnabled(false);
        });

        toggleConfigurationModeButton.addActionListener(e -> {
            controller.sendToggleConfigurationModeCommand();
            toggleConfigurationModeButton.setEnabled(false);
        });

        for (String connectionOption : controller.getConnectionOptions()) {
            serialPortComboBox.addItem(connectionOption);
        }
    }

    @Override
    public void update(LockState lockState) {
        updateLockButton(lockState);
        updateLockStatus(lockState);
    }

    @Override
    public void update(ConfigurationModeState configurationModeState) {
        updateConfigurationModeButton(configurationModeState);
        updateConfigurationModeState(configurationModeState);
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
            connectionStateTextValue.setText(connectionStatusString);
        }
        if (textColor != null) {
            connectionStateTextValue.setForeground(textColor);
        }
    }

    @Override
    public void update(Iterable<PiccReaderStatus> piccReaderStatuses) {
        final String noChipString = "Aucune puce";
        final String wrongChipString = "Mauvaise puce";
        final String correctChipString = "Bonne puce";
        final String newChipString = "Nouvelle puce";
        final String readerLabelString = "Lecteur";
        final String statusLabelString = "Status";
        int i = 0;

        Vector<Vector<String>> data = new Vector<>();
        DefaultTableModel tableModel = new DefaultTableModel();
        for (PiccReaderStatus piccReaderStatus : piccReaderStatuses) {
            String statusString = switch (piccReaderStatus) {
                case NO_PICC -> noChipString;
                case WRONG_PICC -> wrongChipString;
                case CORRECT_PICC -> correctChipString;
                case NEW_PICC -> newChipString;
            };
            Vector<String> row = new Vector<>();
            row.add("Lecteur " + ++i);
            row.add(statusString);
            data.add(row);
        }

        Vector<String> columnNames = new Vector<>();
        columnNames.add(readerLabelString);
        columnNames.add(statusLabelString);

        piccReaderStatusesTable.setModel(new DefaultTableModel(data, columnNames));
        piccReaderStatusesTable.getColumn(statusLabelString).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Color textColor = switch ((String) value) {
                    case wrongChipString -> Color.RED;
                    case correctChipString -> Color.GREEN;
                    case newChipString -> Color.BLUE;
                    default -> Color.BLACK;
                };
                component.setForeground(textColor);
                return component;
            }
        });
    }

    public void setConnectionStateToConnecting() {
        connectionStateTextValue.setText("Connection...");
        connectionStateTextValue.setForeground(Color.GRAY);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void updateLockButton(LockState lockState) {
        String lockButtonNewText = switch (lockState) {
            case OPEN -> "Fermer le verrou";
            case CLOSED -> "Ouvrir le verrou";
        };
        toggleLockButton.setText(lockButtonNewText);
        toggleLockButton.setEnabled(true);
    }

    private void updateLockStatus(LockState lockState) {
        String lockStateTextValueText;
        Color textColor;
        if (lockState == LockState.OPEN) {
            lockStateTextValueText = "Ouvert";
            textColor = Color.GREEN;
        } else {
            lockStateTextValueText = "Fermé";
            textColor = Color.RED;
        }
        lockStateTextValue.setText(lockStateTextValueText);
        lockStateTextValue.setForeground(textColor);
    }

    private void updateConfigurationModeButton(ConfigurationModeState configurationModeState) {
        String toggleConfigurationModeButtonNewText = switch (configurationModeState) {
            case ENABLED -> "Désactiver le mode configuration";
            case DISABLED -> "Activer le mode configuration";
        };
        toggleConfigurationModeButton.setText(toggleConfigurationModeButtonNewText);
        toggleConfigurationModeButton.setEnabled(true);
    }

    private void updateConfigurationModeState(ConfigurationModeState configurationModeState) {
        String configurationModeTextValueText;
        Color textColor;
        if (configurationModeState == ConfigurationModeState.ENABLED) {
            configurationModeTextValueText = "Activé";
            textColor = Color.GREEN;
        } else {
            configurationModeTextValueText = "Désactivé";
            textColor = Color.BLACK;
        }
        configurationModeTextValue.setText(configurationModeTextValueText);
        configurationModeTextValue.setForeground(textColor);
    }

    private JPanel mainPanel;
    private JButton toggleLockButton;
    private JButton toggleConfigurationModeButton;
    private JLabel connectionStateText;
    private JLabel connectionStateTextValue;
    private JPanel buttonPanel;
    private JPanel statusPanel;
    private JLabel lockStateText;
    private JLabel lockStateTextValue;
    private JLabel configurationModeText;
    private JLabel configurationModeTextValue;
    private JTable piccReaderStatusesTable;
    private JScrollPane piccReadersStatusesScrollPane;
    private JPanel connectionPanel;
    private JComboBox<String> serialPortComboBox;
    private JLabel serialPortLabel;
    private JButton connectionButton;

    private void createUIComponents() {
        piccReaderStatusesTable = new JTable() {
            @Override
            public Class<?> getColumnClass(int column) {
                return convertColumnIndexToModel(column) == 0 ? String.class : super.getColumnClass(column);
            }
        };
    }
}
