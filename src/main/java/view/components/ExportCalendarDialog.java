package view.components;

import controller.features.Features;

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
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Dialog for exporting calendar data to CSV files. Allows users to select a source calendar and
 * specify a CSV file export location.
 */
public class ExportCalendarDialog extends JDialog {
  private final Features features;
  private final JFileChooser fileChooser;
  private final JComboBox<String> calendarComboBox;
  private final JLabel timezoneLabel;
  private Map<String, String> calendarNameTimeZoneMap;

  /**
   * Constructs an export calendar dialog.
   *
   * @param parent   the parent window for this dialog
   * @param features the features controller for accessing calendar functionality
   */
  public ExportCalendarDialog(Window parent, Features features) {
    super(parent, "Export CSV", Dialog.ModalityType.APPLICATION_MODAL);
    this.features = features;
    this.calendarNameTimeZoneMap = new HashMap<>();

    setTitle("Export CSV");
    setSize(800, 600);
    setLocationRelativeTo(parent);
    setModal(true);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    fileChooser = new JFileChooser();
    fileChooser.setFileFilter(
            new FileNameExtensionFilter("CSV Files", "csv"));
    fileChooser.setAcceptAllFileFilterUsed(false);
    fileChooser.setMultiSelectionEnabled(false);
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setDialogTitle("Export CSV");
    fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);

    updateCalendarData();
    calendarComboBox = createCalendarDropDown();
    timezoneLabel = new JLabel();
    updateTimezoneLabel();
    updateFileChooserName();

    setActionListener();
    JPanel panel = createPanel();
    add(panel);
  }

  /**
   * Updates the file chooser's suggested file name based on selected calendar.
   */
  private void updateFileChooserName() {
    String selectedCalendar = (String) calendarComboBox.getSelectedItem();
    if (selectedCalendar != null) {
      String timezone = calendarNameTimeZoneMap.get(selectedCalendar);
      // Replace illegal filename characters in timezone
      String safeTimezone = timezone.replaceAll("[\\/:*?\"<>|]", "_");
      // Set suggested file name
      String fileName = selectedCalendar + "_" + safeTimezone;
      if (!fileName.toLowerCase().endsWith(".csv")) {
        fileName += ".csv";
      }
      fileChooser.setSelectedFile(new File(fileName));
    }
  }

  /**
   * Sets up action listeners for interactive components in the dialog.
   */
  private void setActionListener() {
    fileChooser.addActionListener(e -> {
      if (JFileChooser.APPROVE_SELECTION.equals(e.getActionCommand())) {
        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        if (!filePath.toLowerCase().endsWith(".csv")) {
          filePath += ".csv";
        }
        features.exportCSV(filePath);
        features.refreshView();
        dispose();
      } else if (JFileChooser.CANCEL_SELECTION.equals(e.getActionCommand())) {
        dispose();
      }
    });

    calendarComboBox.addActionListener(e -> {
      String selectedCalendar = (String) calendarComboBox.getSelectedItem();
      if (selectedCalendar != null) {
        features.selectCalendar(selectedCalendar);
        features.refreshView();
        updateTimezoneLabel();
        updateFileChooserName();
      }
    });
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
    if (selectedCalendar != null) {
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
  private JComboBox<String> createCalendarDropDown() {
    final JComboBox<String> comboBox =
            new JComboBox<>(calendarNameTimeZoneMap.keySet().toArray(new String[0]));
    comboBox.setSelectedItem(features.getCurrentCalendarName());
    return comboBox;
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

    JLabel calendarLabel = new JLabel("Export from:");
    panel.add(calendarLabel, gbc);
    gbc.gridy++;

    // Create a panel for the dropdown and timezone label
    JPanel dropdownPanel = new JPanel(new BorderLayout(10, 0));
    dropdownPanel.add(calendarComboBox, BorderLayout.CENTER);
    dropdownPanel.add(timezoneLabel, BorderLayout.EAST);

    // Add the panel containing both components
    gbc.insets = new Insets(0, 0, 10, 0);
    panel.add(dropdownPanel, gbc);
    gbc.gridy++;

    // Add a separator
    JSeparator separator = new JSeparator();
    separator.setForeground(Color.DARK_GRAY);
    panel.add(separator, gbc);
    gbc.gridy++;

    gbc.insets = new Insets(5, 5, 5, 5);
    JLabel selectFileLabel = new JLabel("Save to:");
    panel.add(selectFileLabel, gbc);
    gbc.gridy++;

    // Give file chooser more space
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weighty = 1.0;
    panel.add(fileChooser, gbc);

    return panel;
  }
}