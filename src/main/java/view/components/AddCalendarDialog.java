package view.components;

import controller.features.Features;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;

/**
 * Dialog for creating a new calendar. Allows users to specify a calendar name and select a
 * timezone.
 */
public class AddCalendarDialog extends JDialog {
  private final JTextField calendarNameField;
  private final JComboBox<String> availableTimezonesComboBox;
  private final JButton addButton;
  private final JButton cancelButton;
  private final Features features;

  /**
   * Constructs an add calendar dialog.
   *
   * @param parent   the parent window for this dialog
   * @param features the features controller for accessing calendar functionality
   */
  public AddCalendarDialog(Window parent, Features features) {
    super(parent, "Add Calendar", Dialog.ModalityType.APPLICATION_MODAL);
    this.features = features;
    setSize(400, 250);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    getRootPane().registerKeyboardAction(e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
    );

    setLocationRelativeTo(parent);

    calendarNameField = new JTextField(20);
    availableTimezonesComboBox = new JComboBox<>(features.getAvailableTimezones());
    availableTimezonesComboBox.setSelectedItem("America/New_York");
    addButton = new JButton("Add");
    cancelButton = new JButton("Cancel");

    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

    // Calendar name panel
    JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    namePanel.add(new JLabel("Calendar Name:"));
    namePanel.add(calendarNameField);

    // Timezone panel
    JPanel timezonePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    timezonePanel.add(new JLabel("Timezone:"));
    timezonePanel.add(availableTimezonesComboBox);

    // Add components to center panel with spacing
    centerPanel.add(namePanel);
    centerPanel.add(Box.createVerticalStrut(10));
    centerPanel.add(timezonePanel);

    // Button panel for footer
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(cancelButton);
    buttonPanel.add(addButton);

    // Add panels to main container
    mainPanel.add(centerPanel, BorderLayout.CENTER);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    add(mainPanel);
    setUp();
  }

  /**
   * Sets up action listeners for the dialog buttons. Configures the add button to create a new
   * calendar and the cancel button to close the dialog.
   */
  private void setUp() {
    addButton.addActionListener(e -> {
      String calendarName = calendarNameField.getText();
      String timezone = (String) availableTimezonesComboBox.getSelectedItem();
      boolean success = features.addCalendar(calendarName, timezone);
      if (success) {
        dispose();
        features.selectCalendar(calendarName);
        features.refreshView();
      }
    });

    cancelButton.addActionListener(e -> dispose());
  }
}