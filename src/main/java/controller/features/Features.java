package controller.features;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

/**
 * Interface defining the operations that can be performed on a calendar application. Provides
 * methods for managing calendars, viewing and manipulating events, and importing/exporting data.
 */
public interface Features {

  /**
   * Refreshes the view to reflect the current state of the model.
   */
  void refreshView();

  /**
   * Adds a new calendar with the specified name and timezone.
   *
   * @param calendarName the name of the calendar to be added
   * @param timezone     the timezone of the calendar to be added
   *
   * @return true if the calendar was added successfully, false otherwise
   */
  boolean addCalendar(String calendarName, String timezone);

  /**
   * Sets the currently active calendar.
   *
   * @param calendarName the name of the calendar to select
   */
  void selectCalendar(String calendarName);

  /**
   * Gets the name of the currently active calendar.
   *
   * @return the name of the current calendar
   */
  String getCurrentCalendarName();

  /**
   * Retrieves the list of all available timezones from the system.
   *
   * @return an array of timezone strings
   */
  String[] getAvailableTimezones();

  /**
   * Gets the current year and month for initial calendar display.
   *
   * @return the current year and month
   */
  YearMonth getCurrentMonth();

  /**
   * Retrieves a map of all calendars and their timezones.
   *
   * @return a map with calendar names as keys and timezone IDs as values
   */
  Map<String, String> getAllCalendars();

  /**
   * Retrieves event names for each day in the specified month.
   *
   * @param month the year and month to get event counts for
   *
   * @return a list of lists where each inner list contains event names for a day of the month
   */
  List<List<String>> getEventCountsInMonth(YearMonth month);

  /**
   * Retrieves detailed information about all events scheduled for a specific date.
   *
   * @param date the date to get events for
   *
   * @return a list of maps, where each map contains properties of an event
   */
  List<Map<String, String>> getEventDetailsOnDay(LocalDate date);

  /**
   * Retrieves detailed information about a specific event based on its identifying properties.
   *
   * @param eventName     the name of the event
   * @param startDateTime the start date and time of the event
   *
   * @return a map containing all details of the event
   */
  Map<String, String> getExactEvent(String eventName, String startDateTime);

  /**
   * Adds a new event to the calendar based on the provided details. Handles both single and
   * recurring events.
   *
   * @param eventDetails map containing event details including name, dates, description, etc.
   *
   * @return true if the event was added successfully, false otherwise
   */
  boolean addEvent(Map<String, String> eventDetails);

  /**
   * Edits a single occurrence of an event. Updates a specific property of a single event instance.
   *
   * @param eventDetails map containing the event details and edit information
   *
   * @return true if the event was edited successfully, false otherwise
   */
  boolean editSingleOccurrence(Map<String, String> eventDetails);

  /**
   * Edits multiple occurrences of an event. Updates a specific property for all or following
   * occurrences of an event.
   *
   * @param eventDetails map containing the event details and edit information
   *
   * @return true if the events were edited successfully, false otherwise
   */
  boolean editMultipleOccurrences(Map<String, String> eventDetails);

  /**
   * Imports events from a CSV file into the current calendar.
   *
   * @param filePath the path to the CSV file to import
   */
  void importCSV(String filePath);

  /**
   * Exports the current calendar to a CSV file.
   *
   * @param filePath the path where the CSV file should be saved
   */
  void exportCSV(String filePath);
}