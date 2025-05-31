package view.components;

import java.awt.Window;
import java.awt.Dialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JCheckBox;
import javax.swing.KeyStroke;
import javax.swing.BorderFactory;
import javax.swing.table.DefaultTableModel;
import javax.swing.DefaultCellEditor;
import javax.swing.table.TableCellRenderer;

import controller.features.Features;

import static utils.DateTimeUtils.convertTo12HourFormat;
import static view.components.DayCard.formatButton;

/**
 * Dialog that displays events for a specific day and allows users to edit or add events.
 */
public class ViewEventOnDayDialog extends JDialog {
  private final LocalDate date;
  private final Features features;
  private List<Map<String, String>> eventDetails;
  private final JPanel mainPanel;

  /**
   * Constructs a dialog that displays events for a specific day.
   *
   * @param parent   the parent window for this dialog
   * @param date     the date for which to display events
   * @param features the features controller for accessing calendar functionality
   */
  public ViewEventOnDayDialog(Window parent, LocalDate date, Features features) {
    super(parent, "Viewing events on " + date, Dialog.ModalityType.APPLICATION_MODAL);
    this.date = date;
    this.features = features;
    this.eventDetails = features.getEventDetailsOnDay(date);

    getRootPane().registerKeyboardAction(e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
    );

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setSize(800, 600);

    mainPanel = createEventDetailsTable();
    add(mainPanel);

    setLocationRelativeTo(parent);

    setUp();
  }

  /**
   * Overrides the revalidate method to ensure the dialog content is updated.
   */
  @Override
  public void revalidate() {
    super.revalidate();
    setUp();
  }

  /**
   * Sets up the dialog by refreshing event data and updating the table.
   */
  private void setUp() {
    eventDetails = features.getEventDetailsOnDay(date);
    refreshTable();
  }

  /**
   * Refreshes the table with current event data.
   */
  private void refreshTable() {
    if (mainPanel != null) {
      mainPanel.removeAll();
      mainPanel.add(createEventDetailsTable(), BorderLayout.CENTER);
      mainPanel.revalidate();
      mainPanel.repaint();
    }
  }

  /**
   * Creates a table panel displaying event details with edit functionality.
   *
   * @return a panel containing the events table and add event button
   */
  private JPanel createEventDetailsTable() {
    JPanel panel = new JPanel();

    panel.setLayout(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    String[] columnNames = {"Name", "Start Time", "End Time", "Location", "Edit"};

    // Create table model with column names
    Object[][] data = new Object[eventDetails.size()][5];
    for (int i = 0; i < eventDetails.size(); i++) {
      Map<String, String> event = eventDetails.get(i);
      data[i][0] = event.get("Name");
      data[i][1] = convertTo12HourFormat(event.get("StartDateTime"));
      data[i][2] = convertTo12HourFormat(event.get("EndDateTime"));
      data[i][3] = event.get("Location");
      data[i][4] = "Edit"; // Text placeholder for button
    }

    DefaultTableModel model = new DefaultTableModel(data, columnNames) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return column == 4; // Only edit button column is editable
      }
    };

    JTable table = new JTable(model);
    table.setRowHeight(35);

    // Set column widths
    table.getColumnModel().getColumn(0).setPreferredWidth(150);
    table.getColumnModel().getColumn(1).setPreferredWidth(200);
    table.getColumnModel().getColumn(2).setPreferredWidth(200);
    table.getColumnModel().getColumn(3).setPreferredWidth(150);
    table.getColumnModel().getColumn(4).setPreferredWidth(50);

    // Set up the button renderer and editor for the Edit column
    table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
    table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(eventDetails));

    JScrollPane scrollPane = new JScrollPane(table);
    panel.add(scrollPane, BorderLayout.CENTER);

    JButton addButton = new JButton("Add Event");
    formatButton(addButton, Color.GREEN.darker().darker());
    addButton.addActionListener(e -> {
      JDialog dialog = new CreateEventDialog(
              ViewEventOnDayDialog.this,
              date,
              features);
      dialog.setVisible(true);
    });

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(addButton);
    panel.add(buttonPanel, BorderLayout.SOUTH);

    return panel;
  }

  /**
   * Custom button renderer for table cells that display as buttons.
   */
  private static class ButtonRenderer extends JButton implements TableCellRenderer {
    /**
     * Constructs a button renderer with appropriate styling.
     */
    public ButtonRenderer() {
      setOpaque(true);
      setForeground(Color.BLUE.darker().darker());
      setFont(new Font(getFont().getName(), Font.BOLD, 11));
    }

    /**
     * Returns the component used for drawing the cell.
     *
     * @param table      the JTable instance
     * @param value      the value to assign to the cell
     * @param isSelected true if cell is selected
     * @param hasFocus   true if cell has focus
     * @param row        the row of the cell to render
     * @param column     the column of the cell to render
     *
     * @return the component used for drawing the cell
     */
    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {
      setText(value.toString());
      return this;
    }
  }

  /**
   * Custom button editor for table cells that function as buttons.
   */
  private class ButtonEditor extends DefaultCellEditor {
    private JButton button;
    private String label;
    private boolean isPushed;
    private final List<Map<String, String>> eventList;
    private JTable table; // Added table field to store reference

    /**
     * Constructs a button editor with a list of events to edit.
     *
     * @param eventList the list of events that can be edited
     */
    public ButtonEditor(List<Map<String, String>> eventList) {
      super(new JCheckBox());
      this.eventList = eventList;
      button = new JButton();
      formatButton(button, Color.BLUE.darker().darker());
      button.addActionListener(e -> fireEditingStopped());
    }

    /**
     * Gets the component for editing a cell.
     *
     * @param table      the JTable instance
     * @param value      the value to assign to the cell
     * @param isSelected true if cell is selected
     * @param row        the row of the cell being edited
     * @param column     the column of the cell being edited
     *
     * @return the component for editing the cell
     */
    @Override
    public Component getTableCellEditorComponent(
            JTable table,
            Object value,
            boolean isSelected,
            int row,
            int column) {
      this.table = table; // Store the table reference
      label = value.toString();
      button.setText(label);
      isPushed = true;
      return button;
    }

    /**
     * Returns the value contained in the editor.
     *
     * @return the current value being edited
     */
    @Override
    public Object getCellEditorValue() {
      if (isPushed) {
        // Get the selected event and show the edit dialog
        int row = table.getSelectedRow();
        if (row >= 0 && row < eventList.size()) {
          Map<String, String> event = eventList.get(row);
          JDialog editDialog = new EditEventDialog(
                  ViewEventOnDayDialog.this,
                  event.get("Name"),
                  event.get("StartDateTime"),
                  event.get("EndDateTime"),
                  features
          );
          editDialog.setVisible(true);
        }
      }
      isPushed = false;
      return label;
    }

    /**
     * Tells the editor to stop editing and accept any partially edited value.
     *
     * @return true if editing was stopped, false otherwise
     */
    @Override
    public boolean stopCellEditing() {
      isPushed = false;
      return super.stopCellEditing();
    }
  }
}