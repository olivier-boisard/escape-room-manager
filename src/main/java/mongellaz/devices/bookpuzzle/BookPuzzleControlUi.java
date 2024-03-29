package mongellaz.devices.bookpuzzle;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import mongellaz.devices.bookpuzzle.devicecontroller.BookPuzzleDeviceController;
import mongellaz.devices.bookpuzzle.commands.toggleconfigurationmode.ConfigurationModeStateObserver;
import mongellaz.devices.common.togglelock.LockStateObserver;
import mongellaz.devices.bookpuzzle.commands.statusrequest.PiccReaderStatusesObserver;
import mongellaz.devices.bookpuzzle.commands.statusrequest.PiccReaderStatus;
import mongellaz.devices.bookpuzzle.commands.toggleconfigurationmode.ConfigurationModeState;
import mongellaz.devices.common.togglelock.LockState;
import mongellaz.userinterface.ComponentHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

@SuppressWarnings("unused")
public class BookPuzzleControlUi implements ComponentHandler, LockStateObserver, ConfigurationModeStateObserver, PiccReaderStatusesObserver {

    @Inject
    BookPuzzleControlUi(BookPuzzleDeviceController bookPuzzleDeviceController, @Named("ReaderNames") List<String> readerNames) {
        configureToggleLockButton(bookPuzzleDeviceController);
        configureToggleConfigurationModeButton(bookPuzzleDeviceController);
        this.readerNames = readerNames;
    }

    @Override
    public Component getMainPanel() {
        return mainPanel;
    }

    @Override
    public void update(LockState lockState) {
        logger.debug("Update lock state");
        updateLockButton(lockState);
        updateLockStatus(lockState);
    }

    @Override
    public void update(ConfigurationModeState configurationModeState) {
        logger.debug("Update configuration mode state");
        updateConfigurationModeButton(configurationModeState);
        updateConfigurationModeState(configurationModeState);
    }

    @Override
    public void update(Iterable<PiccReaderStatus> piccReaderStatuses) {
        logger.debug("Update PICC reader status");
        piccReaderStatusesTable.setModel(new DefaultTableModel(convertToRows(piccReaderStatuses), getColumnNames()) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        piccReaderStatusesTable.getColumn(STATUS_LABEL_STRING).setCellRenderer(statusStringTableCellRenderer);
    }

    private void configureToggleLockButton(BookPuzzleDeviceController bookPuzzleDeviceController) {
        toggleLockButton.addActionListener(e -> {
            bookPuzzleDeviceController.sendToggleLockCommand();
            toggleLockButton.setEnabled(false);
        });
    }

    private void configureToggleConfigurationModeButton(BookPuzzleDeviceController bookPuzzleDeviceController) {
        toggleConfigurationModeButton.addActionListener(e -> {
            bookPuzzleDeviceController.sendToggleConfigurationModeCommand();
            toggleConfigurationModeButton.setEnabled(false);
        });
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
            row.add(readerNames.get(i++));
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
        logger.info("Enabled lock button");
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
        logger.info("Set lock status text to '{}'", lockStateTextValueText);
    }

    private void updateConfigurationModeButton(ConfigurationModeState configurationModeState) {
        String toggleConfigurationModeButtonNewText = switch (configurationModeState) {
            case ENABLED -> "Désactiver le mode configuration";
            case DISABLED -> "Activer le mode configuration";
        };
        toggleConfigurationModeButton.setText(toggleConfigurationModeButtonNewText);
        toggleConfigurationModeButton.setEnabled(true);
        logger.info("Enable configuration mode button");
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
        logger.info("Set configuration mode status text to '{}'", configurationModeTextValueText);
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

    private final List<String> readerNames;

    private JPanel mainPanel;
    private JButton toggleLockButton;
    private JButton toggleConfigurationModeButton;
    private JPanel buttonPanel;
    private JPanel statusPanel;
    private JLabel lockStateText;
    private JLabel lockStateTextValue;
    private JLabel configurationModeText;
    private JLabel configurationModeTextValue;
    private JTable piccReaderStatusesTable;
    private JScrollPane piccReadersStatusesScrollPane;

    private static final String READER_LABEL_STRING = "Lecteur";
    private static final String STATUS_LABEL_STRING = "Status";
    private static final String NO_CHIP_STRING = "Aucune puce";
    private static final String WRONG_CHIP_STRING = "Mauvaise puce";
    private static final String CORRECT_CHIP_STRING = "Bonne puce";
    private static final String NEW_CHIP_STRING = "Nouvelle puce";
    private final StatusStringTableCellRenderer statusStringTableCellRenderer = new StatusStringTableCellRenderer();
    private final Logger logger = LogManager.getLogger();
}
