package controller.features;

import controller.fileexporter.CsvFileExporter;
import controller.fileparser.CsvFileParser;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.CalendarModel;
import view.GuiView;

import static utils.DateTimeUtils.convertDateTimeFormat;
import static utils.DateTimeUtils.getAllAvailableTimezones;
import static utils.DateTimeUtils.isInvalidDateTimeString;
import static utils.DateTimeUtils.isInvalidWeekDays;

/**
 * Implementation of the Features interface for GUI-based calendar operations. Provides methods to
 * interact with the calendar model and update the GUI view.
 */
public class GuiFeatures implements Features {
  private final CalendarModel model;
  private final GuiView view;

  /**
   * Map for converting weekday names to their character representation.
   */
  private final Map<String, Character> WEEKDAY_TO_CHAR = Map.of(
          "Mon", 'M',
          "Tue", 'T',
          "Wed", 'W',
          "Thu", 'R',
          "Fri", 'F',
          "Sat", 'S',
          "Sun", 'U'
  );

  /**
   * Constructs a new GuiFeatures instance with the specified model and view.
   *
   * @param model the calendar model to be used by the features
   * @param view  the GUI view to be updated
   */
  public GuiFeatures(CalendarModel model, GuiView view) {
    this.model = model;
    this.view = view;
  }

  /**
   * Refreshes the GUI view to reflect the current state of the model.
   */
  @Override
  public void refreshView() {
    view.refresh();
  }

  /**
   * Adds a new calendar with the specified name and timezone.
   *
   * @param calendarName the name of the calendar to be added
   * @param timezone     the timezone of the calendar to be added
   *
   * @return true if the calendar was added successfully, false otherwise
   */
  @Override
  public boolean addCalendar(String calendarName, String timezone) {
    try {
      model.addCalendar(calendarName, timezone);
    } catch (IllegalArgumentException e) {
      view.displayError(e.getMessage());
      return false;
    }
    view.refresh();
    return true;
  }

  /**
   * Retrieves the list of all available timezones from the system.
   *
   * @return an array of timezone strings
   */
  @Override
  public String[] getAvailableTimezones() {
    return getAllAvailableTimezones();
  }

  /**
   * Gets the current year and month for initial calendar display.
   *
   * @return the current year and month
   */
  @Override
  public YearMonth getCurrentMonth() {
    return YearMonth.now();
  }

  /**
   * Retrieves a map of all calendars and their timezones.
   *
   * @return a map with calendar names as keys and timezone IDs as values
   */
  @Override
  public Map<String, String> getAllCalendars() {
    return model.getAllCalendars();
  }

  /**
   * Retrieves event names for each day in the specified month.
   *
   * @param month the year and month to get event counts for
   *
   * @return a list of lists where each inner list contains event names for a day of the month
   */
  @Override
  public List<List<String>> getEventCountsInMonth(YearMonth month) {
    List<List<String>> eventNamesInMonth = new ArrayList<>();

    for (int i = 0; i < month.lengthOfMonth(); i++) {
      LocalDate date = month.atDay(i + 1);
      Map<String, String>[] eventsOnDate = model
              .getCurrentCalendar().getEventsOnDate(date.toString());
      List<String> eventNamesOnDay = new ArrayList<>();
      for (Map<String, String> eventStr : eventsOnDate) {
        eventNamesOnDay.add(eventStr.get("Name"));
      }
      eventNamesInMonth.add(eventNamesOnDay);
    }

    return eventNamesInMonth;
  }

  /**
   * Sets the currently active calendar.
   *
   * @param calendarName the name of the calendar to select
   */
  @Override
  public void selectCalendar(String calendarName) {
    model.useCalendar(calendarName);
  }

  /**
   * Gets the name of the currently active calendar.
   *
   * @return the name of the current calendar
   */
  @Override
  public String getCurrentCalendarName() {
    return model.getCurrentCalendarName();
  }

  /**
   * Retrieves detailed information about all events scheduled for a specific date.
   *
   * @param date the date to get events for
   *
   * @return a list of maps, where each map contains properties of an event
   */
  @Override
  public List<Map<String, String>> getEventDetailsOnDay(LocalDate date) {
    Map<String, String>[] response = model.getCurrentCalendar().getEventsOnDate(date.toString());

    return new ArrayList<>(Arrays.asList(response));
  }

  /**
   * Imports events from a CSV file into the current calendar. Displays a summary message with the
   * number of successfully imported events.
   *
   * @param filePath the path to the CSV file to import
   *
   * @throws RuntimeException if there's an error reading the file
   */
  @Override
  public void importCSV(String filePath) {
    Iterator<Map<String, String>> iterator;
    int total = 0;
    int added = 0;
    try {
      iterator = new CsvFileParser().parseFile(filePath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    while (iterator.hasNext()) {
      Map<String, String> event = iterator.next();
      boolean isAllDayEvent = Boolean.parseBoolean(event.getOrDefault("All Day Event",
              "false"));

      try {
        if (isAllDayEvent) {
          model.getCurrentCalendar().addSingleEventAllDay(
                  event.get("Subject"),
                  convertDateTimeFormat(event.get("Start Date") + " " + event.get("Start Time")),
                  event.getOrDefault("Description", ""),
                  event.getOrDefault("Location", ""),
                  true,
                  Boolean.parseBoolean(event.getOrDefault("Private", ""))
          );
        } else {
          model.getCurrentCalendar().addSingleEvent(
                  event.get("Subject"),
                  convertDateTimeFormat(event.get("Start Date") + " " + event.get("Start Time")),
                  convertDateTimeFormat(event.getOrDefault("End Date", "") + " "
                          + event.getOrDefault("End Time", "")),
                  event.getOrDefault("Description", ""),
                  event.getOrDefault("Location", ""),
                  true,
                  Boolean.parseBoolean(event.getOrDefault("Private", ""))
          );
        }
        total++;
        added++;
      } catch (IllegalArgumentException e) {
        total++;
      }
    }
    view.displayMessage("Successfully added " + added + " out of " + total + " events");
  }

  /**
   * Exports the current calendar to a CSV file. Displays a success message with the file path or an
   * error message if the export fails.
   *
   * @param filePath the path where the CSV file should be saved
   */
  @Override
  public void exportCSV(String filePath) {
    try {
      Map<String, String>[] events = model.getCurrentCalendar().getAllEvents();
      Path path = new CsvFileExporter().export(events, filePath);
      view.displayMessage("Successfully exported to file:" + path.toFile());
    } catch (Exception e) {
      view.displayError("Error exporting file: " + e.getMessage());
    }
  }

  /**
   * Retrieves detailed information about a specific event based on its identifying properties.
   *
   * @param eventName     the name of the event
   * @param startDateTime the start date and time of the event
   *
   * @return a map containing all details of the event
   */
  @Override
  public Map<String, String> getExactEvent(
          String eventName,
          String startDateTime) {

    return new LinkedHashMap<>(
            model.getCurrentCalendar().getExactEvent(eventName, startDateTime)
    );
  }

  /**
   * Adds a new event to the calendar based on the provided details. Handles both single and
   * recurring events.
   *
   * @param eventDetails map containing event details including name, dates, description, etc.
   *
   * @return true if the event was added successfully, false otherwise
   */
  @Override
  public boolean addEvent(Map<String, String> eventDetails) {
    if (!validateBasicEventDetails(eventDetails)) {
      return false;
    }

    final String eventName = eventDetails.getOrDefault("Name", "");
    final String startDateTime = eventDetails.getOrDefault("StartDateTime", "");
    final String endDateTime = eventDetails.getOrDefault("EndDateTime", "");
    final String description = eventDetails.get("Description");
    final String location = eventDetails.get("Location");
    final String isPrivateStr = eventDetails.get("IsPrivate");
    final String isRecurringStr = eventDetails.get("IsRecurring");

    boolean success;
    if (!Boolean.parseBoolean(isRecurringStr)) {
      success = addSingleEventToCalendar(
              eventName,
              startDateTime,
              endDateTime,
              description,
              location,
              isPrivateStr);
    } else {
      success = addRecurringEventToCalendar(
              eventDetails,
              eventName,
              startDateTime,
              endDateTime,
              description,
              location,
              isPrivateStr);
    }

    if (!success) {
      return false;
    }

    view.refresh();
    return true;
  }

  /**
   * Validates the basic event details that are required for all event types. Checks for name, start
   * and end date/time validity.
   *
   * @param eventDetails map containing event details to validate
   *
   * @return true if all basic event details are valid, false otherwise
   */
  private boolean validateBasicEventDetails(Map<String, String> eventDetails) {
    final String eventName = eventDetails.getOrDefault("Name", "");
    if (eventName.isEmpty()) {
      view.displayError("Event name cannot be empty");
      return false;
    }

    final String startDateTime = eventDetails.getOrDefault("StartDateTime", "");
    if (startDateTime.isEmpty()) {
      view.displayError("Start date and time cannot be empty");
      return false;
    }
    if (isInvalidDateTimeString(startDateTime)) {
      view.displayError("Invalid start date and time format");
      return false;
    }

    final String endDateTime = eventDetails.getOrDefault("EndDateTime", "");
    if (endDateTime.isEmpty()) {
      view.displayError("End date and time cannot be empty");
      return false;
    }
    if (isInvalidDateTimeString(endDateTime)) {
      view.displayError("Invalid end date and time format");
      return false;
    }

    return true;
  }

  /**
   * Adds a single (non-recurring) event to the calendar.
   *
   * @param eventName     the name of the event
   * @param startDateTime the start date and time of the event
   * @param endDateTime   the end date and time of the event
   * @param description   the event description
   * @param location      the event location
   * @param isPrivateStr  string representing whether the event is private ("true" or "false")
   *
   * @return true if the event was added successfully, false otherwise
   */
  private boolean addSingleEventToCalendar(
          String eventName,
          String startDateTime,
          String endDateTime,
          String description,
          String location,
          String isPrivateStr) {
    try {
      model.getCurrentCalendar().addSingleEvent(
              eventName,
              startDateTime,
              endDateTime,
              description,
              location,
              true, // autoDecline
              Boolean.parseBoolean(isPrivateStr));
      return true;
    } catch (IllegalArgumentException e) {
      view.displayError(e.getMessage());
      return false;
    }
  }

  /**
   * Adds a recurring event to the calendar. Handles both events with end dates and events with
   * occurrence counts.
   *
   * @param eventDetails  all event details
   * @param eventName     the name of the event
   * @param startDateTime the start date and time of the first occurrence
   * @param endDateTime   the end date and time of the first occurrence
   * @param description   the event description
   * @param location      the event location
   * @param isPrivateStr  string representing whether the event is private ("true" or "false")
   *
   * @return true if the recurring event was added successfully, false otherwise
   */
  private boolean addRecurringEventToCalendar(
          Map<String, String> eventDetails,
          String eventName,
          String startDateTime,
          String endDateTime,
          String description,
          String location,
          String isPrivateStr) {
    String recurringDays = buildRecurringDaysString(eventDetails.get("RecurringDays"));
    final String recurringType = eventDetails.get("RecurringEndType");

    try {
      if ("UntilDate".equals(recurringType)) {
        return addRecurringEventWithEndDate(
                eventDetails, eventName, startDateTime, endDateTime,
                description, location, recurringDays, isPrivateStr);
      } else {
        return addRecurringEventWithFrequency(
                eventDetails, eventName, startDateTime, endDateTime,
                description, location, recurringDays, isPrivateStr);
      }
    } catch (IllegalArgumentException e) {
      view.displayError(e.getMessage());
      return false;
    }
  }

  /**
   * Builds the recurring days string format from the UI representation. Converts day names (e.g.,
   * "Mon, Tue") to character codes (e.g., "MT").
   *
   * @param daysString comma-separated list of day names
   *
   * @return a string of characters representing weekdays
   */
  private String buildRecurringDaysString(String daysString) {
    if (daysString == null) {
      return "";
    }
    final String[] daysArray = daysString.split(", ");
    final StringBuilder daysBuilder = new StringBuilder();
    for (String day : daysArray) {
      daysBuilder.append(WEEKDAY_TO_CHAR.get(day));
    }
    return daysBuilder.toString();
  }

  /**
   * Adds a recurring event with an end date. The event will repeat until the specified end date.
   *
   * @param eventDetails  all event details
   * @param eventName     the name of the event
   * @param startDateTime the start date and time of the first occurrence
   * @param endDateTime   the end date and time of the first occurrence
   * @param description   the event description
   * @param location      the event location
   * @param recurringDays string representing which days of the week the event repeats on
   * @param isPrivateStr  string representing whether the event is private ("true" or "false")
   *
   * @return true if the event was added successfully, false otherwise
   */
  private boolean addRecurringEventWithEndDate(
          Map<String, String> eventDetails,
          String eventName,
          String startDateTime,
          String endDateTime,
          String description,
          String location,
          String recurringDays,
          String isPrivateStr) {
    final String untilDateTime = eventDetails.getOrDefault("RecurringUntilDateTime", "");
    if (untilDateTime.isEmpty()) {
      view.displayError("Until date cannot be empty");
      return false;
    }
    if (isInvalidDateTimeString(untilDateTime)) {
      view.displayError("Invalid until date format");
      return false;
    }

    model.getCurrentCalendar().addRecurringEventUntil(
            eventName,
            startDateTime,
            endDateTime,
            description,
            location,
            recurringDays,
            untilDateTime,
            true, // autoDecline
            Boolean.parseBoolean(isPrivateStr)
    );
    return true;
  }

  /**
   * Adds a recurring event with a specific number of occurrences. The event will repeat for the
   * specified number of times.
   *
   * @param eventDetails  all event details
   * @param eventName     the name of the event
   * @param startDateTime the start date and time of the first occurrence
   * @param endDateTime   the end date and time of the first occurrence
   * @param description   the event description
   * @param location      the event location
   * @param recurringDays string representing which days of the week the event repeats on
   * @param isPrivateStr  string representing whether the event is private ("true" or "false")
   *
   * @return true if the event was added successfully, false otherwise
   */
  private boolean addRecurringEventWithFrequency(
          Map<String, String> eventDetails,
          String eventName,
          String startDateTime,
          String endDateTime,
          String description,
          String location,
          String recurringDays,
          String isPrivateStr) {
    final String countStr = eventDetails.getOrDefault("RecurringOccurrences", "");
    if (countStr.isEmpty() || !countStr.matches("\\d+")) {
      view.displayError("Invalid number of occurrences");
      return false;
    }

    model.getCurrentCalendar().addRecurringEventFrequency(
            eventName,
            startDateTime,
            endDateTime,
            description,
            location,
            recurringDays,
            Integer.parseInt(countStr),
            true, // autoDecline
            Boolean.parseBoolean(isPrivateStr)
    );
    return true;
  }

  /**
   * Edits a single occurrence of an event. Updates a specific property of a single event instance.
   *
   * @param eventDetails map containing the event details and edit information
   *
   * @return true if the event was edited successfully, false otherwise
   */
  @Override
  public boolean editSingleOccurrence(Map<String, String> eventDetails) {
    // Extract event identification details
    final String eventName = eventDetails.getOrDefault("Name", "");
    final String startDateTime = eventDetails.getOrDefault("StartDateTime", "");
    final String endDateTime = eventDetails.getOrDefault("EndDateTime", "");

    // Process the property change
    final String convertedValue = processPropertyChange(eventDetails);
    if (convertedValue == null) {
      return false; // Validation failed
    }

    // Perform the model update
    if (!updateSingleEvent(eventName, startDateTime, endDateTime,
            eventDetails.getOrDefault("selectedProperty", ""), convertedValue)) {
      return false;
    }

    view.refresh();
    return true;
  }

  /**
   * Edits multiple occurrences of an event. Updates a specific property for all or following
   * occurrences of an event.
   *
   * @param eventDetails map containing the event details and edit information
   *
   * @return true if the events were edited successfully, false otherwise
   */
  @Override
  public boolean editMultipleOccurrences(Map<String, String> eventDetails) {
    final String eventName = eventDetails.getOrDefault("Name", "");
    final boolean allEvents = eventDetails.getOrDefault("followingEventsValue", "").isEmpty();

    // Process the property change
    final String convertedValue = processPropertyChange(eventDetails);
    if (convertedValue == null) {
      return false; // Validation failed
    }

    // Perform the model update based on edit type
    final String modelPropertyName = convertToModelPropertyName(
            eventDetails.getOrDefault("selectedProperty", ""));

    if (allEvents) {
      if (!updateAllEvents(eventName, modelPropertyName, convertedValue)) {
        return false;
      }
    } else {
      final String followingEventsValue = eventDetails.get("followingEventsValue");
      if (!updateFollowingEvents(
              eventName,
              followingEventsValue,
              modelPropertyName,
              convertedValue)) {
        return false;
      }
    }

    view.refresh();
    return true;
  }

  /**
   * Processes a property change by validating and converting the value. Validates the input value
   * according to the property type.
   *
   * @param eventDetails map containing event details and property change information
   *
   * @return the converted value, or null if validation failed
   */
  private String processPropertyChange(Map<String, String> eventDetails) {
    final String uiPropertyName = eventDetails.getOrDefault("selectedProperty", "");
    final String propertyValue = eventDetails.getOrDefault("newValue", "").trim();
    final String modelPropertyName = convertToModelPropertyName(uiPropertyName);

    try {
      return validateAndConvertPropertyValue(modelPropertyName, propertyValue);
    } catch (IllegalArgumentException e) {
      view.displayError(e.getMessage());
      return null;
    }
  }

  /**
   * Updates a single event in the model. Applies changes to a specific occurrence of an event.
   *
   * @param eventName      the name of the event
   * @param startDateTime  the start date and time of the event
   * @param endDateTime    the end date and time of the event
   * @param uiPropertyName the UI name of the property to update
   * @param convertedValue the new value for the property
   *
   * @return true if the update was successful, false otherwise
   */
  private boolean updateSingleEvent(
          String eventName,
          String startDateTime,
          String endDateTime,
          String uiPropertyName,
          String convertedValue) {
    try {
      model.getCurrentCalendar().editSingleEvent(
              eventName,
              startDateTime,
              endDateTime,
              convertToModelPropertyName(uiPropertyName),
              convertedValue,
              true
      );
      return true;
    } catch (IllegalArgumentException e) {
      view.displayError(e.getMessage());
      return false;
    }
  }

  /**
   * Updates all occurrences of an event in the model. Applies changes to every instance of the
   * specified recurring event.
   *
   * @param eventName         the name of the event
   * @param modelPropertyName the model name of the property to update
   * @param convertedValue    the new value for the property
   *
   * @return true if the update was successful, false otherwise
   */
  private boolean updateAllEvents(
          String eventName,
          String modelPropertyName,
          String convertedValue) {
    try {
      model.getCurrentCalendar().editMultipleEventsAll(
              eventName,
              modelPropertyName,
              convertedValue,
              true
      );
      return true;
    } catch (IllegalArgumentException e) {
      view.displayError(e.getMessage());
      return false;
    }
  }

  /**
   * Updates following occurrences of an event in the model. Applies changes to all instances of an
   * event starting from a specific date.
   *
   * @param eventName            the name of the event
   * @param followingEventsValue the start date/time from which to apply changes
   * @param modelPropertyName    the model name of the property to update
   * @param convertedValue       the new value for the property
   *
   * @return true if the update was successful, false otherwise
   */
  private boolean updateFollowingEvents(
          String eventName,
          String followingEventsValue,
          String modelPropertyName,
          String convertedValue) {
    try {
      model.getCurrentCalendar().editMultipleEventsFollowing(
              eventName,
              followingEventsValue,
              modelPropertyName,
              convertedValue,
              true
      );
      return true;
    } catch (IllegalArgumentException e) {
      view.displayError(e.getMessage());
      return false;
    }
  }

  /**
   * Converts UI property names to model property names. Maps user-friendly property names to the
   * internal names used by the model.
   *
   * @param uiPropertyName the property name as shown in the UI
   *
   * @return the corresponding property name used in the model
   */
  private String convertToModelPropertyName(String uiPropertyName) {
    switch (uiPropertyName) {
      case "Name":
        return "name";
      case "Start Date Time":
        return "startDateTime";
      case "End Date Time":
        return "endDateTime";
      case "Location":
        return "location";
      case "Description":
        return "description";
      case "Private":
        return "private";
      case "All Day":
        return "allDay";
      case "Frequency":
        return "frequency";
      case "Until Date Time":
        return "untilDateTime";
      case "Weekdays ('MTWRFSU')":
        return "weekDays";
      default:
        // If the property name is not recognized, return it as is
        return uiPropertyName;
    }
  }

  /**
   * Validates and converts property values based on property type. Ensures property values meet the
   * requirements for their specific type.
   *
   * @param propertyName  the model property name
   * @param propertyValue the property value to validate and convert
   *
   * @return the validated and converted property value
   *
   * @throws IllegalArgumentException if the property value is invalid for its type
   */
  private String validateAndConvertPropertyValue(String propertyName, String propertyValue) {
    if (propertyValue == null) {
      propertyValue = "";
    }

    switch (propertyName) {
      case "name":
        if (propertyValue.isBlank()) {
          throw new IllegalArgumentException("Event name cannot be empty");
        }
        return propertyValue;

      case "startDateTime":
      case "endDateTime":
      case "untilDateTime":
        return convertDateTimeFormat(propertyValue);

      case "private":
        if (!propertyValue.equalsIgnoreCase("true")
                && !propertyValue.equalsIgnoreCase("false")) {
          throw new IllegalArgumentException("Private value must be true or false");
        }
        return propertyValue.toLowerCase();

      case "allDay":
        // The Model doesn't actually use this value, it just triggers the setAllDay() method
        return "true";

      case "frequency":
        if (propertyValue.isEmpty() || !propertyValue.matches("\\d+")) {
          throw new IllegalArgumentException("Frequency must be a positive integer");
        }
        return propertyValue;

      case "weekDays":
        if (isInvalidWeekDays(propertyValue)) {
          throw new IllegalArgumentException("Invalid weekdays format");
        }
        return propertyValue;

      default:
        return propertyValue;
    }
  }
}