package view.components;

import controller.features.Features;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.HashMap;
import java.util.Map;

/**
 * Dialog for importing calendar data from CSV files. Allows users to select a target calendar and a
 * CSV file to import.
 */
public class ImportCalendarDialog extends JDialog {
  private final Features features;
  private final JFileChooser fileChooser;
  private final JComboBox<String> calendarComboBox;
  private final JLabel timezoneLabel;
  private Map<String, String> calendarNameTimeZoneMap;

  /**
   * Constructs an import calendar dialog.
   *
   * @param parent   the parent window for this dialog
   * @param features the features controller for accessing calendar functionality
   */
  public ImportCalendarDialog(Window parent, Features features) {
    super(parent, "Import CSV", Dialog.ModalityType.APPLICATION_MODAL);
    this.features = features;
    this.calendarNameTimeZoneMap = new HashMap<>();

    setTitle("Import CSV");
    setSize(800, 600);
    setLocationRelativeTo(parent);
    setModal(true);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
    fileChooser.setAcceptAllFileFilterUsed(false);
    fileChooser.setMultiSelectionEnabled(false);
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setDialogTitle("Import CSV");
    fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);

    updateCalendarData();
    calendarComboBox = refreshCalendarDropDown();
    timezoneLabel = new JLabel();
    updateTimezoneLabel();

    setActionListener();
    JPanel panel = createPanel();
    add(panel);
  }

  /**
   * Updates the map of calendar names and their associated timezones.
   */
  private void updateCalendarData() {
    calendarNameTimeZoneMap.clear();
    calendarNameTimeZoneMap = features.getAllCalendars();
  }

  /**
   * Updates the timezone label based on the currently selected calendar.
   */
  private void updateTimezoneLabel() {
    String selectedCalendar = (String) calendarComboBox.getSelectedItem();
    if (selectedCalendar != null && !selectedCalendar.equals("<New Calendar>")) {
      String timezone = calendarNameTimeZoneMap.get(selectedCalendar);
      timezoneLabel.setText("Timezone: " + timezone);
    } else {
      timezoneLabel.setText("Timezone: ");
    }
  }

  /**
   * Creates and initializes the calendar selection dropdown.
   *
   * @return the initialized calendar combo box
   */
  private JComboBox<String> refreshCalendarDropDown() {
    final JComboBox<String> comboBox =
            new JComboBox<>(calendarNameTimeZoneMap.keySet().toArray(new String[0]));
    comboBox.addItem("<New Calendar>");
    comboBox.setSelectedItem(features.getCurrentCalendarName());
    return comboBox;
  }

  /**
   * Sets action listeners for interactive components in the dialog.
   */
  private void setActionListener() {
    fileChooser.addActionListener(e -> {
      if (JFileChooser.APPROVE_SELECTION.equals(e.getActionCommand())) {
        features.importCSV(fileChooser.getSelectedFile().getAbsolutePath());
        features.refreshView();
        dispose();
      } else if (JFileChooser.CANCEL_SELECTION.equals(e.getActionCommand())) {
        dispose();
      }
    });

    calendarComboBox.addActionListener(e -> {
      if ("<New Calendar>".equals(calendarComboBox.getSelectedItem())) {
        new AddCalendarDialog(ImportCalendarDialog.this, features).setVisible(true);

        updateCalendarData();
        calendarComboBox.setModel(new DefaultComboBoxModel<>(
                calendarNameTimeZoneMap.keySet().toArray(new String[0])));
        calendarComboBox.addItem("<New Calendar>");
      } else {
        features.selectCalendar((String) calendarComboBox.getSelectedItem());
        features.refreshView();
      }

      this.calendarComboBox.setSelectedItem(features.getCurrentCalendarName());
      updateTimezoneLabel();
    });
  }

  /**
   * Creates the main panel with all UI components for the dialog.
   *
   * @return the configured panel containing all UI elements
   */
  private JPanel createPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    panel.setBorder(new EmptyBorder(10, 10, 10, 10));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;

    JLabel calendarLabel = new JLabel("Import to :");
    panel.add(calendarLabel, gbc);
    gbc.gridy++;

    // Create a panel for the dropdown and timezone label
    JPanel dropdownPanel = new JPanel(new BorderLayout(10, 0));
    dropdownPanel.add(calendarComboBox, BorderLayout.CENTER);
    dropdownPanel.add(timezoneLabel, BorderLayout.EAST);

    // Add the panel containing both components
    gbc.insets = new Insets(0, 0, 5, 0);
    panel.add(dropdownPanel, gbc);
    gbc.gridy++;

    // Add a divider with proper sizing
    gbc.insets = new Insets(15, 0, 15, 0);
    JSeparator separator = new JSeparator();
    separator.setForeground(Color.DARK_GRAY);
    panel.add(separator, gbc);
    gbc.gridy++;

    JLabel selectFileLabel = new JLabel("Select File :");
    gbc.insets = new Insets(5, 5, 5, 5);
    panel.add(selectFileLabel, gbc);
    gbc.gridy++;

    // Give file chooser more space
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weighty = 1.0;
    panel.add(fileChooser, gbc);

    return panel;
  }
}