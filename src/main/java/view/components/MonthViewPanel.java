package view.components;

import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.BorderFactory;

import controller.features.Features;

/**
 * Panel that displays a calendar month view with day cards showing events.
 */
public class MonthViewPanel extends JPanel {
  private YearMonth month;
  private final JPanel daysPanel;
  private JButton prevButton;
  private JButton nextButton;
  private JButton todayButton;
  private final Features features;
  private Color borderColor;

  /**
   * Constructs a month view panel for displaying a calendar month.
   *
   * @param month              the year and month to display
   * @param eventCountsInMonth list of events for each day of the month
   * @param features           the features controller for accessing calendar functionality
   */
  public MonthViewPanel(YearMonth month, List<List<String>> eventCountsInMonth, Features features) {
    this.month = month;
    this.features = features;
    setLayout(new BorderLayout());
    setBackground(new Color(240, 240, 240));

    JPanel topPanel = new JPanel(new BorderLayout(0, 5));

    JPanel navigationPanel = createNavigationPanel();
    topPanel.add(navigationPanel, BorderLayout.NORTH);

    JPanel headerPanel = createHeaderPanel();
    topPanel.add(headerPanel, BorderLayout.CENTER);

    add(topPanel, BorderLayout.NORTH);

    daysPanel = new JPanel();
    daysPanel.setLayout(new GridLayout(0, 7));
    populateDaysPanel(eventCountsInMonth);
    add(daysPanel, BorderLayout.CENTER);
  }

  /**
   * Creates a panel with day of week headers.
   *
   * @return a panel with day of week labels
   */
  private JPanel createHeaderPanel() {
    JPanel headerPanel = new JPanel(new GridLayout(1, 7));
    headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(196, 196, 196)));

    String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    for (String day : daysOfWeek) {
      JLabel dayLabel = new JLabel(day, SwingConstants.LEFT);
      dayLabel.setFont(new Font(dayLabel.getFont().getName(), Font.PLAIN, 16));
      dayLabel.setForeground(new Color(87, 87, 87));
      dayLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
      headerPanel.add(dayLabel);
    }

    return headerPanel;
  }

  /**
   * Creates a panel with navigation buttons for moving between months.
   *
   * @return a panel with previous, today, and next buttons
   */
  private JPanel createNavigationPanel() {
    JPanel navigationPanel = new JPanel(new BorderLayout());

    prevButton = new JButton("<");
    todayButton = new JButton("Today");
    nextButton = new JButton(">");

    // Use FlowLayout with zero horizontal gap
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    buttonPanel.add(prevButton);
    buttonPanel.add(todayButton);
    buttonPanel.add(nextButton);

    navigationPanel.add(buttonPanel, BorderLayout.EAST);
    return navigationPanel;
  }

  /**
   * Populates the days panel with day cards showing events for each day.
   *
   * @param eventNamesInMonth list of events for each day of the month
   */
  private void populateDaysPanel(List<List<String>> eventNamesInMonth) {
    if (month == null) {
      return;
    }

    daysPanel.removeAll();

    int firstDayOfMonth = month.atDay(1).getDayOfWeek().getValue() % 7;

    createDummyDay(firstDayOfMonth);


    for (int day = 1; day <= month.lengthOfMonth(); day++) {
      LocalDate dateTime = month.atDay(day);
      JButton dayCard = new DayCard(eventNamesInMonth.get(day - 1), dateTime, features);
      dayCard.setMargin(new Insets(10, 10, 10, 10));
      if (month.getMonthValue() == LocalDate.now().getMonthValue()
              && month.getYear() == LocalDate.now().getYear()
              && day == LocalDate.now().getDayOfMonth()) {
        dayCard.setBorder(BorderFactory
                .createCompoundBorder(
                        BorderFactory.createMatteBorder(2, 2, 2, 2, borderColor),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
      } else {
        dayCard.setBorder(BorderFactory
                .createCompoundBorder(BorderFactory
                                .createMatteBorder(0, 0, 1, 1, new Color(196, 196, 196)),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
      }

      daysPanel.add(dayCard);
    }

    // Fill the remaining slots in the grid if needed
    int totalCells = 42; // 6 rows * 7 columns
    int remainingCells = totalCells - (firstDayOfMonth + month.lengthOfMonth());
    createDummyDay(remainingCells);

    daysPanel.revalidate();
    daysPanel.repaint();
  }

  /**
   * Creates empty placeholder panels for days not in the current month.
   *
   * @param remainingCells number of placeholder cells to create
   */
  private void createDummyDay(int remainingCells) {
    for (int i = 0; i < remainingCells; i++) {
      JPanel dummyDay = new JPanel();
      dummyDay.setBackground(new Color(218, 218, 218));
      dummyDay.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(196, 196, 196)));
      daysPanel.add(dummyDay);
    }
  }

  /**
   * Updates the panel to display a different month with the specified events.
   *
   * @param month             the new month to display
   * @param eventNamesInMonth list of events for each day of the new month
   * @param calendarColor     color to use for highlighting the current day
   */
  public void setMonth(YearMonth month, List<List<String>> eventNamesInMonth, Color calendarColor) {
    this.month = month;
    this.borderColor = calendarColor;
    populateDaysPanel(eventNamesInMonth);
    daysPanel.revalidate();
    daysPanel.repaint();
    revalidate();
    repaint();
  }

  /**
   * Adds an action listener to the previous month button.
   *
   * @param listener the action listener to add
   */
  public void addPreviousMonthListener(ActionListener listener) {
    prevButton.addActionListener(listener);
  }

  /**
   * Adds an action listener to the next month button.
   *
   * @param listener the action listener to add
   */
  public void addNextMonthListener(ActionListener listener) {
    nextButton.addActionListener(listener);
  }

  /**
   * Adds an action listener to the today button.
   *
   * @param listener the action listener to add
   */
  public void addTodayListener(ActionListener listener) {
    todayButton.addActionListener(listener);
  }
}