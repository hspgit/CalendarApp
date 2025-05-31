package view.components;

import controller.features.Features;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Dialog for editing event properties. Provides options to edit various event properties and apply
 * changes to single or multiple occurrences.
 */
public class EditEventDialog extends JDialog {

  private final String eventName;
  private final String startDateTime;
  private final String endDateTime;
  private boolean isRecurring;
  private final Features features;
  private Map<String, String> eventDetails;
  private JComboBox<String> editPropertiesComboBox;
  private JTextField propertyValueField;
  private final ViewEventOnDayDialog parent;
  private JRadioButton singleEventRadioButton;
  private JRadioButton allEventsRadioButton;

  /**
   * Constructs an edit event dialog.
   *
   * @param parent        the parent dialog
   * @param eventName     the name of the event being edited
   * @param startDateTime the start date and time of the event
   * @param endDateTime   the end date and time of the event
   * @param features      the features controller for accessing calendar functionality
   */
  public EditEventDialog(
          ViewEventOnDayDialog parent,
          String eventName,
          String startDateTime,
          String endDateTime,
          Features features) {
    super(parent, "Editing " + eventName, Dialog.ModalityType.APPLICATION_MODAL);
    this.parent = parent;
    this.eventName = eventName;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.features = features;
    this.isRecurring = false;

    setSize(800, 600);

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    getRootPane().registerKeyboardAction(e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
    );

    setLocationRelativeTo(parent);

    setUp();

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());

    JPanel eventDetailsPanel = createEventDetailsPanel();
    JPanel eventEditorPanel = createEditorPanel();
    JPanel scopePanel = createScopePanel();
    JPanel buttonPanel = getButtonPanel();

    mainPanel.add(eventDetailsPanel, BorderLayout.NORTH);
    mainPanel.add(eventEditorPanel, BorderLayout.CENTER);
    mainPanel.add(scopePanel, BorderLayout.SOUTH);

    add(mainPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Creates the panel containing the scope selection options for editing.
   *
   * @return a panel with event scope selection controls
   */
  private JPanel createScopePanel() {
    JPanel scopePanel = new JPanel();
    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
    scopePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    "Apply To"),
            new EmptyBorder(10, 10, 10, 10)));
    scopePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Create radio buttons for scope selection
    singleEventRadioButton = new JRadioButton("Just this occurrence");
    allEventsRadioButton = new JRadioButton("All occurrences");
    JRadioButton followingEventsRadioButton = new JRadioButton("This and following occurrences");

    ButtonGroup scopeGroup = new ButtonGroup();
    scopeGroup.add(singleEventRadioButton);
    scopeGroup.add(allEventsRadioButton);
    scopeGroup.add(followingEventsRadioButton);

    if (isRecurring) {
      allEventsRadioButton.setEnabled(true);
      followingEventsRadioButton.setEnabled(true);
    } else {
      allEventsRadioButton.setEnabled(false);
      followingEventsRadioButton.setEnabled(false);
    }

    // Set default selection
    singleEventRadioButton.setSelected(true);

    // Set fonts and alignment
    singleEventRadioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
    singleEventRadioButton.setFont(new Font("Dialog", Font.PLAIN, 12));
    allEventsRadioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
    allEventsRadioButton.setFont(new Font("Dialog", Font.PLAIN, 12));
    followingEventsRadioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
    followingEventsRadioButton.setFont(new Font("Dialog", Font.PLAIN, 12));

    // Add components to panel
    scopePanel.add(singleEventRadioButton);
    scopePanel.add(Box.createRigidArea(new Dimension(0, 3)));
    scopePanel.add(allEventsRadioButton);
    scopePanel.add(Box.createRigidArea(new Dimension(0, 3)));
    scopePanel.add(followingEventsRadioButton);

    return scopePanel;
  }

  /**
   * Creates the button panel with save and cancel actions.
   *
   * @return a panel with action buttons
   */
  private JPanel getButtonPanel() {
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");

    saveButton.addActionListener(e -> saveChanges());
    cancelButton.addActionListener(e -> dispose());

    buttonPanel.add(cancelButton);
    buttonPanel.add(saveButton);

    return buttonPanel;
  }

  /**
   * Saves the changes made to the event based on the selected scope. Updates the UI accordingly
   * after saving.
   */
  private void saveChanges() {
    String selectedProperty = editPropertiesComboBox.getSelectedItem() != null
            ? editPropertiesComboBox.getSelectedItem().toString()
            : "";
    String newValue = propertyValueField.getText();

    Map<String, String> editDetails = Map.of(
            "Name", eventName,
            "StartDateTime", startDateTime,
            "EndDateTime", endDateTime,
            "selectedProperty", selectedProperty,
            "newValue", newValue
    );

    boolean success;
    if (singleEventRadioButton.isSelected()) {
      // Edit just this occurrence
      success = features.editSingleOccurrence(editDetails);
    } else if (allEventsRadioButton.isSelected()) {
      // Edit all occurrences
      success = features.editMultipleOccurrences(Map.of(
              "Name", eventName,
              "selectedProperty", selectedProperty,
              "newValue", newValue,
              "followingEventsValue", ""
      ));
    } else {
      // Edit following occurrences
      success = features.editMultipleOccurrences(Map.of(
              "Name", eventName,
              "selectedProperty", selectedProperty,
              "newValue", newValue,
              "followingEventsValue", startDateTime
      ));
    }

    if (success) {
      parent.revalidate();
      parent.repaint();
      features.refreshView();
      dispose();
    }
  }

  /**
   * Sets up the dialog by fetching event details from the features controller.
   */
  private void setUp() {
    eventDetails = features.getExactEvent(eventName, startDateTime);
    isRecurring = eventDetails.get("Is Recurring").equalsIgnoreCase("true");
  }

  /**
   * Creates a panel displaying all event details in a read-only format.
   *
   * @return a panel with the event's current property values
   */
  private JPanel createEventDetailsPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(0, 2));

    // Add padding around the panel's contents
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    for (Map.Entry<String, String> entry : eventDetails.entrySet()) {
      JLabel label = new JLabel(entry.getKey() + ": ");
      JTextField textField = new JTextField(entry.getValue());
      textField.setEditable(false);
      panel.add(label);
      panel.add(textField);
    }

    return panel;
  }

  /**
   * Creates a panel for editing event properties. Includes a dropdown for property selection and a
   * text field for entering new values.
   *
   * @return a panel with property editing controls
   */
  private JPanel createEditorPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Edit Property"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

    // Define property options based on whether the event is recurring
    List<String> propertyOptionsList = new ArrayList<>(java.util.List.of(
            "Name", "Start Date Time", "End Date Time", "Location",
            "Description", "Private", "All Day"));

    // Add recurring-specific properties only if the event is recurring
    if (isRecurring) {
      propertyOptionsList.addAll(List.of(
              "Frequency", "Until Date Time", "Weekdays ('MTWRFSU')"));
    }

    // Convert list to array for JComboBox
    String[] propertyOptions = propertyOptionsList.toArray(new String[0]);
    editPropertiesComboBox = new JComboBox<>(propertyOptions);
    JPanel selectorPanel = new JPanel(new BorderLayout());
    selectorPanel.add(new JLabel("Property:"), BorderLayout.NORTH);
    selectorPanel.add(editPropertiesComboBox, BorderLayout.CENTER);

    // Right side: value editor
    propertyValueField = new JTextField();
    JPanel valuePanel = new JPanel(new BorderLayout());
    valuePanel.add(new JLabel("New Value:"), BorderLayout.NORTH);
    valuePanel.add(propertyValueField, BorderLayout.CENTER);

    // Add listener to update the value field when property changes
    editPropertiesComboBox.addActionListener(e -> {
      String selectedProperty = (String) editPropertiesComboBox.getSelectedItem();
      if (selectedProperty != null) {
        String currentValue;

        // Map the UI property names to the model property names
        switch (selectedProperty) {
          case "Name":
            currentValue = eventDetails.get("Name");
            break;
          case "Start Date Time":
            currentValue = eventDetails.get("Start Date Time");
            break;
          case "End Date Time":
          case "Until Date Time":
            currentValue = eventDetails.get("End Date Time");
            break;
          case "Location":
            currentValue = eventDetails.get("Location");
            break;
          case "Description":
            currentValue = eventDetails.get("Description");
            break;
          case "Private":
            currentValue = String.valueOf(eventDetails.get("Private").equalsIgnoreCase("True"));
            break;
          case "All Day":
            currentValue = eventDetails.getOrDefault("allDay", "False");
            break;
          case "Frequency":
            currentValue = "0";
            break;
          case "Weekdays ('MTWRFSU')":
            currentValue = "MTWRFSU";
            break;
          default:
            currentValue = eventDetails.get(selectedProperty);
        }

        propertyValueField.setText(currentValue);
      }
    });

    // Main layout - use a panel with BorderLayout instead of GridLayout
    JPanel mainPanel = new JPanel(new BorderLayout(10, 0));

    // Create fixed-height panels for the components
    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.add(selectorPanel, BorderLayout.CENTER);
    leftPanel.setPreferredSize(new Dimension(300, 30));

    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.add(valuePanel, BorderLayout.CENTER);

    mainPanel.add(leftPanel, BorderLayout.WEST);
    mainPanel.add(rightPanel, BorderLayout.CENTER);

    panel.add(mainPanel, BorderLayout.NORTH);

    // Add help text below the property selector area
    JPanel helpPanel = createHelpPanel();
    panel.add(helpPanel, BorderLayout.CENTER);

    editPropertiesComboBox.setSelectedIndex(0);

    return panel;
  }

  /**
   * Creates a help panel with instructions for using the date/time format.
   *
   * @return a panel with help text
   */
  private JPanel createHelpPanel() {
    JPanel helpPanel = new JPanel();
    helpPanel.setLayout(new BoxLayout(helpPanel, BoxLayout.Y_AXIS));
    helpPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    helpPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

    String helpText = "<html>Notes:<br>"
            + "• Date/time format is MM/DD/YYYY HH:MM a (example: 05/25/2024 03:30 PM)<br>"
            + "• Weekday string must be of the format MTWRFSU where each letter corresponds "
            + "to one day of the week<br>"
            + "• Setting 'All Day' will ignore the time input and "
            + "convert the event to an all-day event</html>";

    JLabel helpLabel = new JLabel(helpText);
    helpLabel.setFont(new Font("Dialog", Font.ITALIC, 11));
    helpLabel.setForeground(new Color(100, 100, 100));
    helpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    helpPanel.add(helpLabel);
    return helpPanel;
  }
}