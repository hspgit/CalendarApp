package view.components;

import controller.features.Features;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.List;

/**
 * Button representing a day in a calendar with events. Displays the day number and up to 3 events
 * for that day.
 */
public class DayCard extends JButton {

  private final List<String> eventNamesOnDay;
  private final LocalDate date;
  private final Features features;

  /**
   * Constructs a day card for a specific date.
   *
   * @param eventNamesOnDay list of event names occurring on this day
   * @param date            the date this card represents
   * @param features        the features controller for accessing calendar functionality
   */
  public DayCard(List<String> eventNamesOnDay, LocalDate date, Features features) {
    this.eventNamesOnDay = eventNamesOnDay;
    this.date = date;
    this.features = features;
    setLayout(new BorderLayout());
    setUp();
  }

  /**
   * Sets up the day card with date panel, events panel, and click listener.
   */
  private void setUp() {
    add(createDatePanel(), BorderLayout.NORTH);
    add(createEventsPanel(), BorderLayout.CENTER);
    addDayCardListener();
  }

  /**
   * Creates a panel displaying the day of month.
   *
   * @return a panel with the day number
   */
  private JPanel createDatePanel() {
    JLabel dateLabel = new JLabel(String.valueOf(date.getDayOfMonth()));
    dateLabel.setFont(new Font(dateLabel.getFont().getName(), Font.PLAIN, 15));
    JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
    datePanel.setOpaque(false);
    datePanel.add(dateLabel);
    return datePanel;
  }

  /**
   * Creates a panel displaying events for this day. Shows up to 3 events and indicates if there are
   * more.
   *
   * @return a panel with event information
   */
  private JPanel createEventsPanel() {
    JPanel eventsPanel = new JPanel();
    eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
    eventsPanel.setOpaque(false);

    if (eventNamesOnDay == null || eventNamesOnDay.isEmpty()) {
      JLabel noEventsLabel = new JLabel("");
      noEventsLabel.setHorizontalAlignment(SwingConstants.CENTER);
      noEventsLabel.setFont(new Font(noEventsLabel.getFont().getName(), Font.ITALIC, 12));
      noEventsLabel.setForeground(new Color(80, 80, 80));
      eventsPanel.add(noEventsLabel);
    } else {
      int totalEvents = eventNamesOnDay.size();
      int maxEventsToShow = 3;
      int eventsToShow = Math.min(maxEventsToShow, totalEvents);

      for (int i = 0; i < eventsToShow; i++) {
        String eventName = eventNamesOnDay.get(i);
        JLabel eventLabel = new JLabel(eventName);
        eventLabel.setFont(new Font(eventLabel.getFont().getName(), Font.PLAIN, 12));
        eventLabel.setForeground(new Color(80, 80, 80));
        eventsPanel.add(eventLabel);
      }

      if (totalEvents > maxEventsToShow) {
        JLabel moreEventsLabel = new JLabel("+ " + (totalEvents - maxEventsToShow) + " more");
        moreEventsLabel.setFont(
                new Font(moreEventsLabel.getFont().getName(), Font.ITALIC, 12));
        moreEventsLabel.setForeground(new Color(80, 80, 80));
        eventsPanel.add(moreEventsLabel);
      }
    }

    return eventsPanel;
  }

  /**
   * Adds a click listener to open the event viewing dialog for this day.
   */
  private void addDayCardListener() {
    addActionListener(e -> {
      JDialog dialog = new ViewEventOnDayDialog(
              SwingUtilities.getWindowAncestor(DayCard.this),
              date, features);
      dialog.setVisible(true);
    });
  }

  /**
   * Formats the button with the specified color. Sets font, foreground color, margins, and other
   * visual properties.
   *
   * @param button the button to format
   * @param color  the color to set for the button foreground
   */
  public static void formatButton(JButton button, Color color) {
    button.setFont(new Font(button.getFont().getName(), Font.BOLD, 12));
    button.setForeground(color);
    button.setMargin(new Insets(2, 4, 2, 4));
    button.setFocusPainted(false);
    button.setContentAreaFilled(true);
    button.setBorderPainted(true);
  }
}