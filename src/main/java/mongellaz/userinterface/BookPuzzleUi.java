package mongellaz.userinterface;

import mongellaz.bookpuzzle.BookPuzzleDeviceController;
import mongellaz.commands.*;
import mongellaz.commands.handshake.HandshakeResult;
import mongellaz.commands.statusrequest.PiccReaderStatus;
import mongellaz.commands.toggleconfigurationmode.ConfigurationModeState;
import mongellaz.commands.togglelock.LockState;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Vector;

@SuppressWarnings("unused")
public class BookPuzzleUi implements HandshakeResultObserver, LockStateObserver, ConfigurationModeStateObserver, PiccReaderStatusesObserver {

    public void setConnectionOptions(Iterable<String> connectionOptions) {
        connectionOptionsComboBox.removeAllItems();
        for (String connectionOption : connectionOptions) {
            connectionOptionsComboBox.addItem(connectionOption);
        }
    }

    public String getSelectedConnectionOption() {
        return (String) connectionOptionsComboBox.getSelectedItem();
    }

    public void addConnectionButtonActionListener(ActionListener actionListener) {
        connectionButton.addActionListener(actionListener);
    }

    public void setBookPuzzleDeviceController(BookPuzzleDeviceController bookPuzzleDeviceController) {
        toggleLockButton.addActionListener(e -> {
            bookPuzzleDeviceController.sendToggleLockCommand();
            toggleLockButton.setEnabled(false);
        });

        toggleConfigurationModeButton.addActionListener(e -> {
            bookPuzzleDeviceController.sendToggleConfigurationModeCommand();
            toggleConfigurationModeButton.setEnabled(false);
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
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
        piccReaderStatusesTable.setModel(new DefaultTableModel(convertToRows(piccReaderStatuses), getColumnNames()));
        piccReaderStatusesTable.getColumn(STATUS_LABEL_STRING).setCellRenderer(statusStringTableCellRenderer);
    }

    private Vector<Vector<String>> convertToRows(Iterable<PiccReaderStatus> piccReaderStatuses) {
        int i = 0;
        Vector<Vector<String>> data = new Vector<>();
        for (PiccReaderStatus piccReaderStatus : piccReaderStatuses) {
            String statusString = switch (piccReaderStatus) {
                case NO_PICC -> NO_CHIP_STRING;
                case WRONG_PICC -> WRONG_CHIP_STRING;
                case CORRECT_PICC -> CORRECT_CHIP_STRING;
                case NEW_PICC -> NEW_CHIP_STRING;
            };
            Vector<String> row = new Vector<>();
            row.add("Lecteur " + ++i);
            row.add(statusString);
            data.add(row);
        }
        return data;
    }

    private Vector<String> getColumnNames() {
        Vector<String> columnNames = new Vector<>();
        columnNames.add(READER_LABEL_STRING);
        columnNames.add(STATUS_LABEL_STRING);
        return columnNames;
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
            textColor = Color.ORANGE;
        } else {
            configurationModeTextValueText = "Désactivé";
            textColor = Color.BLACK;
        }
        configurationModeTextValue.setText(configurationModeTextValueText);
        configurationModeTextValue.setForeground(textColor);
    }

    private void createUIComponents() {
        piccReaderStatusesTable = new JTable() {
            @Override
            public Class<?> getColumnClass(int column) {
                return convertColumnIndexToModel(column) == 0 ? String.class : super.getColumnClass(column);
            }
        };
    }

    private static class StatusStringTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Color textColor = switch ((String) value) {
                case WRONG_CHIP_STRING -> Color.RED;
                case CORRECT_CHIP_STRING -> Color.GREEN;
                case NEW_CHIP_STRING -> Color.BLUE;
                default -> Color.BLACK;
            };
            component.setForeground(textColor);
            return component;
        }
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
    private JComboBox<String> connectionOptionsComboBox;
    private JLabel connectionOptionLabel;
    private JButton connectionButton;
    private JSeparator connectionOptionsSeparator;

    private static final String READER_LABEL_STRING = "Lecteur";
    private static final String STATUS_LABEL_STRING = "Status";
    private static final String NO_CHIP_STRING = "Aucune puce";
    private static final String WRONG_CHIP_STRING = "Mauvaise puce";
    private static final String CORRECT_CHIP_STRING = "Bonne puce";
    private static final String NEW_CHIP_STRING = "Nouvelle puce";
    private final StatusStringTableCellRenderer statusStringTableCellRenderer = new StatusStringTableCellRenderer();
}
