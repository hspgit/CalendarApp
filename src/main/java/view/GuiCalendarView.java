package view;

import controller.features.Features;
import view.components.AddCalendarDialog;
import view.components.CreateEventDialog;
import view.components.ExportCalendarDialog;
import view.components.ImportCalendarDialog;
import view.components.MonthViewPanel;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Main calendar view implementation for the graphical user interface. Displays and manages a
 * calendar with month view, navigation controls, and menus for various calendar operations.
 */
public class GuiCalendarView extends JFrame implements GuiView {

  private YearMonth currentMonth;
  private Features features;
  private MonthViewPanel monthViewPanel;
  private JComboBox<String> calendarDropdown;
  private JComboBox<String> monthDropdown;
  private JComboBox<String> yearDropdown;
  private Map<String, Color> calendarColorMap;
  private Color selectedCalendarColor;
  private String selectedCalendarName;
  private String selectedCalendarTimeZone;
  private Map<String, String> calendarNameTimeZoneMap;

  private JPanel monthYearTZPanel;
  private JLabel monthYearLabel;
  private JLabel timeZoneLabel;

  private List<List<String>> eventNamesInMonth;

  private final Color[] availableColors = {
      new Color(230, 25, 75),   // Red
      new Color(60, 180, 75),   // Green
      new Color(0, 130, 200),   // Blue
      new Color(245, 130, 48),  // Orange
      new Color(145, 30, 180),  // Purple
      new Color(70, 240, 240),  // Cyan
      new Color(240, 50, 230),  // Magenta
      new Color(210, 245, 60),  // Lime
      new Color(170, 110, 40),  // Brown
      new Color(255, 225, 25)   // Yellow
  };
  private int nextColorIndex = 0;

  /**
   * Constructs a new GuiCalendarView with the specified title. Initializes the frame with default
   * size, position, and components.
   *
   * @param title the title of the calendar window
   */
  public GuiCalendarView(String title) {
    super(title);
    setSize(1600, 900);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());
    setMinimumSize(new Dimension(800, 450));
    initLookAndFeel();
    initComponents();
  }

  /**
   * Initializes the look and feel of the application. Sets the system look and feel and default
   * font.
   */
  private void initLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      setDefaultFont();
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
             | UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    }
  }

  /**
   * Initializes the main UI components. Sets up dropdown menus and creates the menu bar.
   */
  private void initComponents() {
    calendarDropdown = new JComboBox<>(new String[]{"Loading calendars..."});
    monthDropdown = new JComboBox<>(new String[]{
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    });
    yearDropdown = new JComboBox<>(new String[]{
        "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030"
    });
    calendarColorMap = new HashMap<>();
    calendarNameTimeZoneMap = new LinkedHashMap<>();

    setJMenuBar(createMenuBar());
  }

  /**
   * Sets the default font for all UI components. Applies Arial 14pt as the default font for all
   * components.
   */
  private void setDefaultFont() {
    Font defaultFont = new Font("Arial", Font.PLAIN, 14);
    UIDefaults defaults = UIManager.getDefaults();
    Enumeration<Object> keys = defaults.keys();
    while (keys.hasMoreElements()) {
      Object key = keys.nextElement();
      if (key.toString().toLowerCase().contains("font")) {
        defaults.put(key, defaultFont);
      }
    }
  }

  /**
   * Creates the main menu bar for the application.
   *
   * @return the configured menu bar
   */
  private JMenuBar createMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    menuBar.add(createFileMenu());
    return menuBar;
  }

  /**
   * Creates the File menu with options for creating, importing, and exporting calendars.
   *
   * @return the configured File menu
   */
  private JMenu createFileMenu() {
    JMenu fileMenu = new JMenu("File");
    JMenuItem newEvent = new JMenuItem("New Event");
    newEvent.addActionListener(e -> openCreateEventDialog());
    fileMenu.add(newEvent);

    JMenuItem newCal = new JMenuItem("New Calendar");
    newCal.addActionListener(e -> openAddCalendarDialog());
    fileMenu.add(newCal);

    fileMenu.add(new JSeparator());

    JMenuItem importCal = new JMenuItem("Import");
    importCal.addActionListener(e -> openImportCalendarDialog());
    fileMenu.add(importCal);

    JMenuItem exportCal = new JMenuItem("Export");
    exportCal.addActionListener(e -> openExportCalendarDialog());
    fileMenu.add(exportCal);
    return fileMenu;
  }

  /**
   * Opens the dialog for creating a new event.
   */
  private void openCreateEventDialog() {
    JDialog dialog = new CreateEventDialog(this, currentMonth.atDay(1), features);
    dialog.setVisible(true);
  }

  /**
   * Opens the dialog for adding a new calendar.
   */
  private void openAddCalendarDialog() {
    JDialog dialog = new AddCalendarDialog(this, features);
    dialog.setVisible(true);
  }

  /**
   * Opens the dialog for importing a calendar.
   */
  private void openImportCalendarDialog() {
    JDialog dialog = new ImportCalendarDialog(this, features);
    dialog.setVisible(true);
  }

  /**
   * Opens the dialog for exporting a calendar.
   */
  private void openExportCalendarDialog() {
    JDialog dialog = new ExportCalendarDialog(this, features);
    dialog.setVisible(true);
  }

  /**
   * Sets up the main view components and displays the calendar. Initializes the month view,
   * dropdown menus, and event data.
   */
  @Override
  public void setUp() {
    currentMonth = features.getCurrentMonth();
    updateMonthAndYearDropdowns();
    setMonthAndYearDropdownListeners();

    updateCalendarData();
    setupCalendarDropdown();
    selectedCalendarName = calendarDropdown.getSelectedItem().toString();
    selectedCalendarTimeZone = calendarNameTimeZoneMap.get(selectedCalendarName);
    setMonthYearTZPanel();

    addCalendarDropdownListener();
    setEventCounts();

    JPanel contentPanel = new JPanel(new BorderLayout());
    contentPanel.add(createTopPanel(), BorderLayout.NORTH);
    setEventCounts();
    monthViewPanel = createMonthViewPanel();
    contentPanel.add(monthViewPanel, BorderLayout.CENTER);
    add(contentPanel, BorderLayout.CENTER);

    updateCalendarDropdownItems();
    setMonthYearTZPanel();
    setVisible(true);
  }

  /**
   * Updates the month and year dropdowns to reflect the current month.
   */
  private void updateMonthAndYearDropdowns() {
    monthDropdown.setSelectedIndex(currentMonth.getMonthValue() - 1);
    yearDropdown.setSelectedItem(String.valueOf(currentMonth.getYear()));
  }

  /**
   * Sets action listeners for the month and year dropdown menus. Updates the calendar view when
   * selections change.
   */
  private void setMonthAndYearDropdownListeners() {
    monthDropdown.addActionListener(e -> {
      int selectedMonth = monthDropdown.getSelectedIndex();
      currentMonth = YearMonth.of(currentMonth.getYear(), selectedMonth + 1);
      updateUIForMonthChange();
    });

    yearDropdown.addActionListener(e -> {
      int selectedYear = Integer.parseInt(yearDropdown.getSelectedItem().toString());
      currentMonth = YearMonth.of(selectedYear, currentMonth.getMonthValue());
      updateUIForMonthChange();
    });
  }

  /**
   * Updates the UI components when the current month changes.
   */
  private void updateUIForMonthChange() {
    setMonthYearTZPanel();
    setEventCounts();
    monthViewPanel.setMonth(currentMonth, eventNamesInMonth, selectedCalendarColor);
    refresh();
  }

  /**
   * Updates the calendar data from the features controller.
   */
  private void updateCalendarData() {
    calendarNameTimeZoneMap.clear();
    calendarNameTimeZoneMap = features.getAllCalendars();
  }

  /**
   * Sets up the calendar dropdown with available calendars.
   */
  private void setupCalendarDropdown() {
    calendarDropdown = new JComboBox<>(calendarNameTimeZoneMap.keySet().toArray(new String[0]));
    calendarDropdown.setSelectedIndex(0);
  }

  /**
   * Creates the top panel with month/year display and calendar selection.
   *
   * @return the configured top panel
   */
  private JPanel createTopPanel() {
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 10));
    topPanel.add(getMonthYearPanel(), BorderLayout.WEST);

    JPanel quickMonthSwitcherPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    quickMonthSwitcherPanel.add(monthDropdown);
    quickMonthSwitcherPanel.add(yearDropdown);

    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    rightPanel.add(new JLabel("Calendar: "));
    rightPanel.add(calendarDropdown);

    topPanel.add(quickMonthSwitcherPanel, BorderLayout.CENTER);
    topPanel.add(rightPanel, BorderLayout.EAST);
    return topPanel;
  }

  /**
   * Gets or creates the panel displaying the month, year, and timezone.
   *
   * @return the month/year/timezone panel
   */
  private JPanel getMonthYearPanel() {
    if (monthYearTZPanel == null) {
      monthYearTZPanel = new JPanel(new BorderLayout());
      monthYearLabel = new JLabel("", JLabel.CENTER);
      timeZoneLabel = new JLabel("", JLabel.CENTER);
      monthYearTZPanel.add(monthYearLabel, BorderLayout.NORTH);
      monthYearTZPanel.add(timeZoneLabel, BorderLayout.SOUTH);
    }
    return monthYearTZPanel;
  }

  /**
   * Creates the month view panel that displays the calendar grid.
   *
   * @return the configured month view panel
   */
  private MonthViewPanel createMonthViewPanel() {
    MonthViewPanel panel = new MonthViewPanel(currentMonth, eventNamesInMonth, features);
    panel.addPreviousMonthListener(e -> {
      currentMonth = currentMonth.minusMonths(1);
      updateMonthAndYearDropdowns();
      updateUIForMonthChange();
    });
    panel.addNextMonthListener(e -> {
      currentMonth = currentMonth.plusMonths(1);
      updateMonthAndYearDropdowns();
      updateUIForMonthChange();
    });
    panel.addTodayListener(e -> {
      currentMonth = features.getCurrentMonth();
      updateMonthAndYearDropdowns();
      updateUIForMonthChange();
    });
    return panel;
  }

  /**
   * Updates the event counts for the current month from the features controller.
   */
  private void setEventCounts() {
    eventNamesInMonth = features.getEventCountsInMonth(currentMonth);
  }

  /**
   * Adds a listener to the calendar dropdown to handle calendar selection changes.
   */
  private void addCalendarDropdownListener() {
    removeAllCalendarDropdownListeners();
    calendarDropdown.addActionListener(e -> {
      selectedCalendarName = (String) calendarDropdown.getSelectedItem();
      if (selectedCalendarName != null) {
        features.selectCalendar(selectedCalendarName);
        selectedCalendarTimeZone = calendarNameTimeZoneMap.get(selectedCalendarName);
        setMonthYearTZPanel();
        setEventCounts();
        monthViewPanel.setMonth(currentMonth, eventNamesInMonth, selectedCalendarColor);
        refresh();
      }
    });
  }

  /**
   * Removes all listeners from the calendar dropdown.
   */
  private void removeAllCalendarDropdownListeners() {
    for (ActionListener al : calendarDropdown.getActionListeners()) {
      calendarDropdown.removeActionListener(al);
    }
  }

  /**
   * Updates the items in the calendar dropdown and assigns colors to calendars.
   */
  private void updateCalendarDropdownItems() {
    calendarDropdown.removeAllItems();
    for (String calendarName : calendarNameTimeZoneMap.keySet()) {
      if (!calendarColorMap.containsKey(calendarName)) {
        Color color = availableColors[nextColorIndex % availableColors.length];
        calendarColorMap.put(calendarName, color);
        nextColorIndex++;
      }
      calendarDropdown.addItem(calendarName);
    }
  }

  /**
   * Updates the month/year/timezone panel with current calendar information. Sets the calendar
   * color, month/year text, and timezone display.
   */
  private void setMonthYearTZPanel() {
    if (monthYearLabel == null || timeZoneLabel == null) {
      getMonthYearPanel();
    }
    timeZoneLabel.setText("<html><span style='font-weight:100;'>"
            + selectedCalendarTimeZone + "</span></html>");
    timeZoneLabel.setFont(new Font(monthYearLabel.getFont().getName(), Font.PLAIN, 16));

    monthYearLabel.setText("<html><span style='font-weight:bold;'>"
            + currentMonth.format(DateTimeFormatter.ofPattern("MMMM"))
            + "</span>&nbsp;<span style='font-weight:100;'>"
            + currentMonth.format(DateTimeFormatter.ofPattern("yyyy"))
            + "</span></html>");
    monthYearLabel.setFont(new Font(monthYearLabel.getFont().getName(), Font.PLAIN, 32));

    String selectedCalendarItem = (String) calendarDropdown.getSelectedItem();
    if (selectedCalendarItem != null) {
      selectedCalendarColor = calendarColorMap.getOrDefault(selectedCalendarItem, Color.BLACK);
      monthYearLabel.setForeground(selectedCalendarColor);
    } else {
      monthYearLabel.setForeground(Color.BLACK);
    }
  }

  /**
   * Adds the features controller to the view.
   *
   * @param features the features controller for accessing calendar functionality
   */
  @Override
  public void addFeatures(Features features) {
    this.features = features;
  }

  /**
   * Refreshes the view to reflect the current state of the model. Updates calendar data, selection,
   * and display.
   */
  @Override
  public void refresh() {
    updateCalendarData();
    selectedCalendarName = features.getCurrentCalendarName();
    selectedCalendarTimeZone = calendarNameTimeZoneMap.get(selectedCalendarName);
    removeAllCalendarDropdownListeners();
    updateCalendarDropdownItems();
    calendarDropdown.setSelectedItem(selectedCalendarName);
    addCalendarDropdownListener();
    if (!calendarColorMap.containsKey(selectedCalendarName)) {
      Color calendarColor = availableColors[nextColorIndex % availableColors.length];
      calendarColorMap.put(selectedCalendarName, calendarColor);
      nextColorIndex++;
    }
    selectedCalendarColor = calendarColorMap.get(selectedCalendarName);
    setEventCounts();
    monthViewPanel.setMonth(currentMonth, eventNamesInMonth, selectedCalendarColor);
    setMonthYearTZPanel();
    revalidate();
    repaint();
  }

  /**
   * Displays an error message dialog.
   *
   * @param message the error message to display
   */
  @Override
  public void displayError(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Displays an informational message dialog.
   *
   * @param message the message to display
   */
  @Override
  public void displayMessage(String message) {
    JOptionPane.showMessageDialog(this, message, "Message", JOptionPane.INFORMATION_MESSAGE);
  }
}