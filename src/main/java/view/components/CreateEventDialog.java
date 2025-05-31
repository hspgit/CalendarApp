package view.components;

import controller.features.Features;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Dialog for creating new calendar events. Provides fields for entering event details and recurring
 * options.
 */
public class CreateEventDialog extends JDialog {

  private final LocalDate startDate;
  private final Features features;

  private JTextField nameField;
  private JTextField startDateField;
  private JTextField startTimeField;
  private JTextField endDateField;
  private JTextField endTimeField;
  private JTextField eventDescriptionArea;
  private JTextField locationField;
  private JToggleButton isPrivateToggle;
  private JCheckBox isRecurringCheckbox;
  private JCheckBox[] weekdayCheckboxes;
  private JRadioButton untilDateOption;
  private JRadioButton numberOfTimesOption;
  private JTextField untilDateField;
  private JTextField untilTimeField;
  private JTextField numberOfTimesField;
  private final Window parent;

  /**
   * Constructs a create event dialog.
   *
   * @param parent    the parent window for this dialog
   * @param startDate the initial date for the new event
   * @param features  the features controller for accessing calendar functionality
   */
  public CreateEventDialog(Window parent, LocalDate startDate, Features features) {
    super(parent, "Create Event");
    this.parent = parent;
    this.startDate = startDate;
    this.features = features;
    initializeDialog();
  }

  /**
   * Initializes the dialog components and layout.
   */
  private void initializeDialog() {
    setSize(1200, 800);
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    getRootPane().registerKeyboardAction(e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
    );

    setLocationRelativeTo(parent);

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());

    JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 0));

    contentPanel.add(createLeftPanel());
    contentPanel.add(createRightPanel());

    JPanel buttonPanel = createButtonPanel();

    mainPanel.add(contentPanel, BorderLayout.CENTER);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);

    add(mainPanel);
  }

  /**
   * Creates the left panel with basic event details.
   *
   * @return a panel with fields for basic event information
   */
  private JPanel createLeftPanel() {
    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JPanel fieldsPanel = new JPanel();
    fieldsPanel.setLayout(new GridLayout(0, 1, 5, 5));

    nameField = new JTextField();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    startDateField = new JTextField(startDate.format(formatter));

    startTimeField = new JTextField("09:00");
    endDateField = new JTextField(startDate.format(formatter));
    endTimeField = new JTextField("10:00");
    locationField = new JTextField();
    eventDescriptionArea = new JTextField();

    fieldsPanel.add(new JLabel("Event Name:"));
    fieldsPanel.add(nameField);
    fieldsPanel.add(new JLabel("Event Start Date:"));
    fieldsPanel.add(startDateField);
    fieldsPanel.add(new JLabel("Event Start Time:"));
    fieldsPanel.add(startTimeField);
    fieldsPanel.add(new JLabel("Event End Date:"));
    fieldsPanel.add(endDateField);
    fieldsPanel.add(new JLabel("Event End Time:"));
    fieldsPanel.add(endTimeField);
    fieldsPanel.add(new JLabel("Event Location:"));
    fieldsPanel.add(locationField);
    fieldsPanel.add(new JLabel("Event Description:"));
    fieldsPanel.add(eventDescriptionArea);

    fieldsPanel.add(createVisibilityPanel());

    leftPanel.add(fieldsPanel, BorderLayout.CENTER);
    return leftPanel;
  }

  /**
   * Creates the right panel with recurrence options.
   *
   * @return a panel with recurring event options
   */
  private JPanel createRightPanel() {
    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JPanel recurTogglePanel = createRecurTogglePanel();
    rightPanel.add(recurTogglePanel, BorderLayout.NORTH);

    JPanel recurringPanel = createRecurringPanel();
    rightPanel.add(recurringPanel, BorderLayout.CENTER);

    return rightPanel;
  }

  /**
   * Creates a panel with the recurring event toggle.
   *
   * @return a panel with the recurring event checkbox
   */
  private JPanel createRecurTogglePanel() {
    JPanel recurTogglePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    isRecurringCheckbox = new JCheckBox("Recurring Event");
    isRecurringCheckbox.addActionListener(e -> {
      boolean isRecurring = isRecurringCheckbox.isSelected();
      enableRecurrenceOptions(isRecurring);
    });
    recurTogglePanel.add(isRecurringCheckbox);
    return recurTogglePanel;
  }

  /**
   * Creates a panel containing all recurrence options.
   *
   * @return a panel with recurrence configuration options
   */
  private JPanel createRecurringPanel() {
    JPanel recurringPanel = new JPanel();
    recurringPanel.setLayout(new BoxLayout(recurringPanel, BoxLayout.Y_AXIS));
    recurringPanel.setBorder(BorderFactory.createTitledBorder("Recurrence Options"));

    // Initially disable all components in the recurrence panel
    recurringPanel.setEnabled(false);

    // Add a weekday selection panel
    recurringPanel.add(createWeekdaysPanel());

    // Create an ending options panel with a title border
    JPanel endingOptionsPanel = createEndingOptionsPanel();
    recurringPanel.add(endingOptionsPanel);

    return recurringPanel;
  }

  /**
   * Creates a panel containing the recurrence ending options with a titled border.
   *
   * @return a panel with ending options
   */
  private JPanel createEndingOptionsPanel() {
    JPanel endingOptionsPanel = new JPanel();
    endingOptionsPanel.setLayout(new BoxLayout(endingOptionsPanel, BoxLayout.Y_AXIS));
    endingOptionsPanel.setBorder(BorderFactory.createTitledBorder("Choose Ending Strategy"));

    // Add date options panels
    endingOptionsPanel.add(createUntilDatePanel());
    endingOptionsPanel.add(createNumberOfTimesPanel());

    return endingOptionsPanel;
  }

  /**
   * Creates a panel for selecting the weekdays on which the event recurs.
   *
   * @return a panel with weekday checkboxes
   */
  private JPanel createWeekdaysPanel() {
    JPanel weekdaysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    weekdaysPanel.add(new JLabel("Repeat on: "));
    weekdayCheckboxes = new JCheckBox[7];
    String[] weekdays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    for (int i = 0; i < 7; i++) {
      weekdayCheckboxes[i] = new JCheckBox(weekdays[i]);
      weekdayCheckboxes[i].setEnabled(false);
      weekdaysPanel.add(weekdayCheckboxes[i]);
    }
    return weekdaysPanel;
  }

  /**
   * Creates a panel for specifying an end date for the recurring event.
   *
   * @return a panel with until date option
   */
  private JPanel createUntilDatePanel() {
    // Create radio button group if not already created
    if (untilDateOption == null) {
      ButtonGroup endOptionsGroup = new ButtonGroup();
      untilDateOption = new JRadioButton("Until Date (YYYY-MM-DD):");
      numberOfTimesOption = new JRadioButton("Number of times (+ve number):");
      untilDateOption.setEnabled(false);
      numberOfTimesOption.setEnabled(false);
      endOptionsGroup.add(untilDateOption);
      endOptionsGroup.add(numberOfTimesOption);
      untilDateOption.setSelected(true);

      // Add listeners to the radio buttons
      untilDateOption.addActionListener(e -> updateEndOptionsFields());
      numberOfTimesOption.addActionListener(e -> updateEndOptionsFields());
    }

    // Until date/time panel
    JPanel untilDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    untilDatePanel.add(untilDateOption);
    untilDateField = new JTextField(10);
    untilTimeField = new JTextField(5);
    untilDateField.setEnabled(false);
    untilTimeField.setEnabled(false);
    untilDatePanel.add(untilDateField);
    untilDatePanel.add(new JLabel("Time (HH:MM) :"));
    untilDatePanel.add(untilTimeField);
    return untilDatePanel;
  }

  /**
   * Creates a panel for specifying the number of occurrences of the recurring event.
   *
   * @return a panel with number of occurrences option
   */
  private JPanel createNumberOfTimesPanel() {
    JPanel numberOfTimesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    numberOfTimesPanel.add(numberOfTimesOption);
    numberOfTimesField = new JTextField(5);
    numberOfTimesField.setEnabled(false);
    numberOfTimesPanel.add(numberOfTimesField);
    return numberOfTimesPanel;
  }

  /**
   * Creates the button panel with help text and action buttons.
   *
   * @return a panel with help text and action buttons
   */
  private JPanel createButtonPanel() {
    JPanel outerPanel = new JPanel(new BorderLayout());

    JPanel helpPanel = getHelpPanel();

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    JButton saveButton = new JButton("Create");
    JButton cancelButton = new JButton("Cancel");

    cancelButton.addActionListener(e -> dispose());

    saveButton.addActionListener(e -> saveEventDetails());
    buttonPanel.add(cancelButton);

    buttonPanel.add(saveButton);

    outerPanel.add(helpPanel, BorderLayout.NORTH);
    outerPanel.add(buttonPanel, BorderLayout.CENTER);

    return outerPanel;
  }

  /**
   * Creates a help panel with formatting instructions.
   *
   * @return a panel with help text
   */
  private static JPanel getHelpPanel() {
    JPanel helpPanel = new JPanel();
    helpPanel.setLayout(new BoxLayout(helpPanel, BoxLayout.Y_AXIS));
    helpPanel.setBorder(new EmptyBorder(5, 10, 10, 10));

    JLabel helpLabel = new JLabel(
            "Note: Time format is HH:MM (14:30) and date format is "
                    + "YYYY-MM-DD (2023-05-15)");
    helpLabel.setFont(new Font("Dialog", Font.ITALIC, 11));
    helpLabel.setForeground(new Color(100, 100, 100));
    helpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    helpPanel.add(helpLabel);
    return helpPanel;
  }

  /**
   * Creates a panel for setting event visibility (public/private).
   *
   * @return a panel with visibility toggle
   */
  private JPanel createVisibilityPanel() {
    JPanel visibilityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    visibilityPanel.add(new JLabel("Visibility:"));
    isPrivateToggle = new JToggleButton("Private");
    isPrivateToggle.setSelected(true);
    isPrivateToggle.addActionListener(e -> {
      if (isPrivateToggle.isSelected()) {
        isPrivateToggle.setText("Private");
      } else {
        isPrivateToggle.setText("Public");
      }
    });
    visibilityPanel.add(isPrivateToggle);
    return visibilityPanel;
  }

  /**
   * Enables or disables all recurrence option components.
   *
   * @param enable true to enable recurrence options, false to disable them
   */
  private void enableRecurrenceOptions(boolean enable) {
    // Enable/disable checkboxes
    for (JCheckBox checkbox : weekdayCheckboxes) {
      checkbox.setEnabled(enable);
    }

    // Enable/disable radio buttons
    untilDateOption.setEnabled(enable);
    numberOfTimesOption.setEnabled(enable);

    if (enable) {
      // If enabling recurrence, set at least one weekday checked by default (current day)
      int todayIndex = startDate.getDayOfWeek().getValue() - 1; // 0 for Monday, 6 for Sunday
      if (todayIndex >= 0 && todayIndex < 7) {
        weekdayCheckboxes[todayIndex].setSelected(true);
      } else {
        // Default to Monday if conversion doesn't work
        weekdayCheckboxes[0].setSelected(true);
      }

      // Set initial state based on which radio button is selected
      updateEndOptionsFields();

      // Add listeners to the radio buttons to update field states when selection changes
      untilDateOption.addActionListener(e -> updateEndOptionsFields());
      numberOfTimesOption.addActionListener(e -> updateEndOptionsFields());
    } else {
      // If disabling recurrence, disable all related fields
      untilDateField.setEnabled(false);
      untilTimeField.setEnabled(false);
      numberOfTimesField.setEnabled(false);
    }
  }

  /**
   * Updates the enabled state of end option fields based on radio button selection.
   */
  private void updateEndOptionsFields() {
    if (untilDateOption.isSelected()) {
      untilDateField.setEnabled(true);
      untilTimeField.setEnabled(true);
      numberOfTimesField.setEnabled(false);
      numberOfTimesField.setText("");
    } else if (numberOfTimesOption.isSelected()) {
      untilDateField.setEnabled(false);
      untilTimeField.setEnabled(false);
      numberOfTimesField.setEnabled(true);
      untilDateField.setText("");
      untilTimeField.setText("");
    }
  }

  /**
   * Collects event details from the form and saves them to the calendar. Closes the dialog on
   * successful save.
   */
  private void saveEventDetails() {
    String name = nameField.getText().trim();
    String startDate = startDateField.getText().trim();
    String startTime = startTimeField.getText().trim();
    String endDate = endDateField.getText().trim();
    String endTime = endTimeField.getText().trim();
    String location = locationField.getText().trim();
    String description = eventDescriptionArea.getText().trim();
    boolean isPrivate = isPrivateToggle.isSelected();
    boolean isRecurring = isRecurringCheckbox.isSelected();

    // Create a map to store all event details
    Map<String, String> eventDetails = new LinkedHashMap<>();
    eventDetails.put("Name", name);
    eventDetails.put("StartDateTime", startDate + "T" + startTime);
    eventDetails.put("EndDateTime", endDate + "T" + endTime);
    eventDetails.put("Location", location);
    eventDetails.put("Description", description);
    eventDetails.put("IsPrivate", String.valueOf(isPrivate));
    eventDetails.put("IsRecurring", String.valueOf(isRecurring));

    // Add recurring details if applicable
    if (isRecurring) {
      StringBuilder selectedDays = new StringBuilder();
      String[] weekdays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
      for (int i = 0; i < 7; i++) {
        if (weekdayCheckboxes[i].isSelected()) {
          if (selectedDays.length() > 0) {
            selectedDays.append(", ");
          }
          selectedDays.append(weekdays[i]);
        }
      }
      eventDetails.put("RecurringDays", selectedDays.toString());

      if (untilDateOption.isSelected()) {
        eventDetails.put("RecurringEndType", "UntilDate");
        eventDetails.put("RecurringUntilDateTime", untilDateField.getText().trim()
                + "T" + untilTimeField.getText().trim());
      } else {
        eventDetails.put("RecurringEndType", "NumberOfTimes");
        eventDetails.put("RecurringOccurrences", numberOfTimesField.getText().trim());
      }
    }

    boolean success = features.addEvent(eventDetails);
    if (success) {
      parent.revalidate();
      parent.repaint();
      features.refreshView();
      dispose(); // Close the popup only after successful save
    }
  }
}