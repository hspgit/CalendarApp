package model;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import model.calendarentry.CalendarEntry;
import model.calendarentry.RecurringEvent;
import model.calendarentry.SingleEvent;

import static utils.DateTimeUtils.DateTimeProcessingResult;

import static utils.DateTimeUtils.processEventDateTime;
import static utils.DateTimeUtils.stringToZdt;

/**
 * The BasicSingleCalendar class is an implementation of the SingleCalendar abstract class. This
 * class provides methods to add, edit, and get events from the calendar. It also provides methods
 * to export the calendar and show the availability status of the calendar. It uses a list to store
 * the entries.
 */
public class BasicSingleCalendar extends SingleCalendar {

  /**
   * The list of events in the calendar.
   */
  private final List<CalendarEntry> events;

  private String timeZone;

  /**
   * Constructor for the BasicSingleCalendar class. Initializes the list of events. LinkedList is
   * used as the underlying data structure. The list is empty initially meaning there are no events
   * in the calendar. Using LinkedList as the underlying data structure allows for efficient
   * insertion and deletion of events.
   */
  public BasicSingleCalendar(String timeZone) {
    this.timeZone = timeZone;
    this.events = new LinkedList<>();
  }


  /**
   * Adds a single event to the calendar. Converts the startDateTime and endDateTime strings to
   * ZonedDateTime objects. Check if the event conflicts with any existing events in the calendar.
   * If autoDecline is true, the event is not added if there is a conflict. If autoDecline is false,
   * the event is added regardless of conflicts.
   *
   * @param name          the name of the event.
   * @param startDateTime the start date and time of the event.
   * @param endDateTime   the end date and time of the event.
   * @param description   the description of the event.
   * @param location      the location of the event.
   * @param autoDecline   whether the event should be automatically declined.
   * @param isPrivate     whether the event is private.
   *
   * @throws IllegalArgumentException if there is a conflict with an existing event.
   */
  @Override
  public void addSingleEvent(
          String name,
          String startDateTime,
          String endDateTime,
          String description,
          String location,
          boolean autoDecline,
          boolean isPrivate) {
    addSingleEventHelper(
            name, startDateTime, endDateTime, description, location, autoDecline, isPrivate
    );
  }

  /**
   * Adds a single all day event to the calendar.
   *
   * @param name        the name of the event.
   * @param startDate   the start date of the event.
   * @param description the description of the event.
   * @param location    the location of the event.
   * @param autoDecline whether the event should be automatically declined.
   * @param isPrivate   whether the event is private.
   */
  @Override
  public void addSingleEventAllDay(
          String name,
          String startDate,
          String description,
          String location,
          boolean autoDecline,
          boolean isPrivate) {
    addSingleEventHelper(
            name, startDate, "", description, location, autoDecline, isPrivate
    );
  }

  /**
   * Adds a simple recurring event to the calendar until the given date and time.
   *
   * @param name          the name of the event.
   * @param startDateTime the start date and time of the event.
   * @param endDateTime   the end date and time of the event.
   * @param description   the description of the event.
   * @param location      the location of the event.
   * @param weekDays      the days of the week the event occurs on.
   * @param untilDateTime the date and time the event ends.
   * @param autoDecline   whether the event should be automatically declined.
   * @param isPrivate     whether the event is private.
   */
  @Override
  public void addRecurringEventUntil(
          String name,
          String startDateTime,
          String endDateTime,
          String description,
          String location,
          String weekDays,
          String untilDateTime,
          boolean autoDecline,
          boolean isPrivate) {
    DateTimeProcessingResult result = processEventDateTime(startDateTime, endDateTime, timeZone);

    ZonedDateTime startZonedDateTime = result.startDateTime;
    ZonedDateTime endZonedDateTime = result.endDateTime;
    boolean isAllDay = result.isAllDay;

    ZonedDateTime untilZonedDateTime = stringToZdt(untilDateTime, timeZone);

    addRecurringEventUntilHelper(
            name,
            description,
            location,
            weekDays,
            untilZonedDateTime,
            autoDecline,
            isPrivate,
            startZonedDateTime,
            endZonedDateTime,
            isAllDay);
  }

  /**
   * Adds a simple recurring event to the calendar with the given frequency.
   *
   * @param name          the name of the event.
   * @param startDateTime the start date and time of the event.
   * @param endDateTime   the end date and time of the event.
   * @param description   the description of the event.
   * @param location      the location of the event.
   * @param weekDays      the days of the week the event occurs on.
   * @param frequency     the number of occurrences of the event.
   * @param autoDecline   whether the event should be automatically declined.
   * @param isPrivate     whether the event is private.
   */
  @Override
  public void addRecurringEventFrequency(
          String name,
          String startDateTime,
          String endDateTime,
          String description,
          String location,
          String weekDays,
          Integer frequency,
          boolean autoDecline,
          boolean isPrivate) {
    DateTimeProcessingResult result = processEventDateTime(startDateTime, endDateTime, timeZone);

    ZonedDateTime startZonedDateTime = result.startDateTime;
    ZonedDateTime endZonedDateTime = result.endDateTime;
    boolean isAllDay = result.isAllDay;

    CalendarEntry recurringEvent = new RecurringEvent(
            name,
            startZonedDateTime,
            endZonedDateTime,
            description,
            location,
            isPrivate,
            isAllDay,
            weekDays,
            frequency);

    if (autoDecline) {
      if (checkConflict(recurringEvent)) {
        throw new IllegalArgumentException("Conflict detected, Event not Created");
      }
    }

    this.events.add(recurringEvent);
  }

  /**
   * Adds an all day recurring event until a given date and time.
   *
   * @param name          the name of the event.
   * @param startDate     the start date and time of the event.
   * @param description   the description of the event.
   * @param location      the location of the event.
   * @param weekDays      the days of the week the event occurs on.
   * @param untilDateTime the date and time the event ends.
   * @param autoDecline   whether the event should be automatically declined.
   * @param isPrivate     whether the event is private.
   */
  @Override
  public void addRecurringAllDayEventUntil(
          String name,
          String startDate,
          String description,
          String location,
          String weekDays,
          String untilDateTime,
          boolean autoDecline,
          boolean isPrivate) {
    DateTimeProcessingResult result = processEventDateTime(startDate, "", timeZone);

    ZonedDateTime startZonedDateTime = result.startDateTime;
    ZonedDateTime endZonedDateTime = result.endDateTime;
    boolean isAllDay = result.isAllDay;

    ZonedDateTime untilZonedDateTime = stringToZdt(untilDateTime, timeZone);
    addRecurringEventUntilHelper(
            name,
            description,
            location,
            weekDays,
            untilZonedDateTime,
            autoDecline,
            isPrivate,
            startZonedDateTime,
            endZonedDateTime,
            isAllDay);
  }

  /**
   * Adds an all day recurring event with the given frequency.
   *
   * @param name        the name of the event.
   * @param startDate   the start date and time of the event.
   * @param description the description of the event.
   * @param location    the location of the event.
   * @param weekDays    the days of the week the event occurs on.
   * @param frequency   the number of occurrences of the event.
   * @param autoDecline whether the event should be automatically declined.
   * @param isPrivate   whether the event is private.
   */
  @Override
  public void addRecurringAllDayEventFrequency(
          String name,
          String startDate,
          String description,
          String location,
          String weekDays,
          Integer frequency,
          boolean autoDecline,
          boolean isPrivate) {
    DateTimeProcessingResult result = processEventDateTime(startDate, "", timeZone);

    ZonedDateTime startZonedDateTime = result.startDateTime;
    ZonedDateTime endZonedDateTime = result.endDateTime;
    boolean isAllDay = result.isAllDay;

    CalendarEntry recurringEvent = new RecurringEvent(
            name,
            startZonedDateTime,
            endZonedDateTime,
            description,
            location,
            isPrivate,
            isAllDay,
            weekDays,
            frequency);

    if (autoDecline) {
      if (checkConflict(recurringEvent)) {
        throw new IllegalArgumentException("Conflict detected, Event not Created");
      }
    }

    this.events.add(recurringEvent);
  }

  /**
   * Edits a single event in the calendar. Converts the startDateTime and endDateTime strings to
   * ZonedDateTime objects. Find the event to edit and edit the property with the new value.
   *
   * @param eventName     the name of the event to edit.
   * @param startDateTime the start date and time of the event.
   * @param endDateTime   the end date and time of the event.
   * @param propertyName  the property to edit.
   * @param propertyValue the new value of the property.
   *
   * @throws IllegalArgumentException if the event is not found.
   */
  @Override
  public void editSingleEvent(
          String eventName,
          String startDateTime,
          String endDateTime,
          String propertyName,
          String propertyValue,
          boolean autoDecline) {
    DateTimeProcessingResult result = processEventDateTime(startDateTime, endDateTime, timeZone);

    ZonedDateTime startZonedDateTime = result.startDateTime;
    ZonedDateTime endZonedDateTime = result.endDateTime;

    boolean eventFound = false;
    List<CalendarEntry> editedEvent = new LinkedList<>();

    for (CalendarEntry entry : events) {
      if (entry.matchesSingleOccurrence(eventName, startZonedDateTime, endZonedDateTime) != null) {
        entry.editSingleEvent(
                eventName,
                startZonedDateTime,
                endZonedDateTime,
                propertyName,
                propertyValue);

        editedEvent.add(entry);
        eventFound = true;
      }
    }

    if (!eventFound) {
      throw new IllegalArgumentException("Event not found");
    }

    events.removeAll(editedEvent);
    checkConflictAndAddAll(editedEvent, autoDecline);
  }

  /**
   * Edits all the events with the event name and following the start date time.
   *
   * @param eventName     the name of the event to edit.
   * @param startDateTime the start date and time of the event.
   * @param propertyName  the property to edit.
   * @param propertyValue the new value of the property.
   * @param autoDecline   whether to automatically decline conflicting events.
   */
  @Override
  public void editMultipleEventsFollowing(
          String eventName,
          String startDateTime,
          String propertyName,
          String propertyValue,
          boolean autoDecline) {
    editMultipleEventsHelper(eventName, startDateTime, propertyName, propertyValue, autoDecline);
  }

  /**
   * Edits all the events with the event name.
   *
   * @param eventName     the name of the event to edit.
   * @param propertyName  the property to edit.
   * @param propertyValue the new value of the property.
   * @param autoDecline   whether to automatically decline conflicting events.
   */
  @Override
  public void editMultipleEventsAll(
          String eventName,
          String propertyName,
          String propertyValue,
          boolean autoDecline) {
    editMultipleEventsHelper(eventName, "", propertyName, propertyValue, autoDecline);
  }

  /**
   * Edits all the events with the event name and the start date time.
   *
   * @param startDateTime the start date and time.
   * @param endDateTime   the end date and time.
   *
   * @return Array of events that are within the given range.
   */
  @Override
  public Map<String, String>[] getEventsRange(String startDateTime, String endDateTime) {
    if (startDateTime.isEmpty() || endDateTime.isEmpty()) {
      throw new IllegalArgumentException("Start and end date time cannot be empty");
    }

    ZonedDateTime startZonedDateTime = stringToZdt(startDateTime, timeZone);
    ZonedDateTime endZonedDateTime = stringToZdt(endDateTime, timeZone);

    if (endZonedDateTime.isBefore(startZonedDateTime)) {
      throw new IllegalArgumentException("End time must be after start time");
    }

    return getEventsHelper(startZonedDateTime, endZonedDateTime);
  }

  /**
   * Gets the events in the calendar on the given date.
   *
   * @param startDate the start date and time.
   *
   * @return Array of events that are on the given date.
   */
  @Override
  public Map<String, String>[] getEventsOnDate(String startDate) {
    ZonedDateTime startZonedDateTime = stringToZdt(startDate, timeZone);
    ZonedDateTime endZonedDateTime = startZonedDateTime.withHour(23).withMinute(59);
    return getEventsHelper(startZonedDateTime, endZonedDateTime);
  }

  /**
   * Gets the events in the calendar between the given start and end date times. Returns Name,
   * StartDateTime, EndDateTime, Location. If endDateTime is empty, it will return all events on the
   * given start day.
   *
   * @param startDateTime the start date and time.
   * @param endDateTime   the end date and time.
   *
   * @return the events in the calendar between the given start and end date times.
   */
  private Map<String, String>[] getEventsHelper(
          ZonedDateTime startDateTime,
          ZonedDateTime endDateTime) {
    final List<Map<String, String>> eventStrings = new LinkedList<>();
    List<Map<String, String>> currentEventString;

    // Temporary list to hold events with their start times for sorting
    List<CalendarEntry> eventsInRange = new LinkedList<>();

    // Collect all events in the range
    for (CalendarEntry entry : events) {
      List<CalendarEntry> entriesInRange = entry
              .getEventsIfWithinRange(startDateTime, endDateTime);
      if (entriesInRange != null && !entriesInRange.isEmpty()) {
        eventsInRange.addAll(entriesInRange);
      }
    }

    // Sort events by start time
    eventsInRange.sort(Comparator.comparing(CalendarEntry::getStartDateTime));

    // Get event details from sorted events
    for (CalendarEntry entry : eventsInRange) {
      currentEventString = entry.getEventDetails(startDateTime, endDateTime, false);
      eventStrings.addAll(currentEventString);
    }

    return eventStrings.toArray(new Map[0]);
  }


  /**
   * Gets all events in the calendar as an array of formatted strings. Each event is converted to a
   * formatted string representation that includes all the event details in a predefined format.
   */
  @Override
  public Map<String, String>[] getAllEvents() {
    List<Map<String, String>> eventStrings = new LinkedList<>();
    for (CalendarEntry event : events) {
      eventStrings.addAll(event.getAllEventDetails());
    }
    return eventStrings.toArray(new Map[0]);
  }

  /**
   * Shows the availability status of the calendar at the given date and time.
   *
   * @param startDateTime the start date and time.
   *
   * @return "Available" if the calendar is available, "Busy" if the calendar is busy.
   *
   * @throws IllegalArgumentException if the startDateTime is null or empty.
   */
  @Override
  public String getStatusOnDateTime(String startDateTime) {
    if (startDateTime == null || startDateTime.isEmpty()) {
      throw new IllegalArgumentException("Start date time cannot be empty");
    }

    // Create a one-minute test event at the specified time
    final ZonedDateTime startDateTimeZDT = stringToZdt(startDateTime, timeZone);
    final ZonedDateTime endDateTime = startDateTimeZDT.plusMinutes(1);

    return checkAvailability(startDateTimeZDT, endDateTime);
  }

  /**
   * Shows the availability status of the calendar within the given range.
   *
   * @param rangeStartDateTime the start date and time of the range.
   * @param rangeEndDateTime   the end date and time of the range.
   *
   * @return "Available" if the calendar is available within the given range, "Busy" otherwise.
   */
  @Override
  public String getStatusInRange(String rangeStartDateTime, String rangeEndDateTime) {
    if (rangeStartDateTime == null || rangeStartDateTime.isEmpty()) {
      throw new IllegalArgumentException("Start date time cannot be empty");
    }
    if (rangeEndDateTime == null || rangeEndDateTime.isEmpty()) {
      throw new IllegalArgumentException("End date time cannot be empty");
    }

    ZonedDateTime startDateTimeZDT = stringToZdt(rangeStartDateTime, timeZone);
    ZonedDateTime endDateTimeZDT = stringToZdt(rangeEndDateTime, timeZone);

    return checkAvailability(startDateTimeZDT, endDateTimeZDT);
  }

  /**
   * Helper method to check calendar availability for a given time range.
   *
   * @param startDateTime the start date and time.
   * @param endDateTime   the end date and time.
   *
   * @return "Available" if the calendar is available, "Busy" if the calendar is busy.
   */
  private String checkAvailability(ZonedDateTime startDateTime, ZonedDateTime endDateTime) {
    final CalendarEntry testEvent = new SingleEvent("_test_", startDateTime, endDateTime,
            "", "", false, false);

    for (CalendarEntry event : events) {
      if (event.isConflict(testEvent)) {
        return "Busy";
      }
    }

    return "Available";
  }

  /**
   * Change the timeZone of the calendar and all the events in the calendar.
   *
   * @param newTimezone the new timezone to change to.
   */
  @Override
  public void changeTimezone(String newTimezone) {
    ZoneId zoneId = ZoneId.of(newTimezone);
    this.timeZone = newTimezone;
    for (CalendarEntry event : events) {
      event.updateZone(zoneId);
    }
  }

  /**
   * Gets the events in the calendar between the given start and end date times. Returns list of
   * events that are within the given range.
   *
   * @param startDate The Start date of the range.
   * @param endDate   the end date of the range.
   *
   * @return the list of events that come within the given range.
   */
  @Override
  protected List<CalendarEntry> getCalendarEntriesWithinRange(String startDate, String endDate) {
    ZonedDateTime start = stringToZdt(startDate, timeZone).withHour(0).withMinute(0);
    ZonedDateTime end;
    end = stringToZdt(endDate, timeZone).withHour(23).withMinute(59);
    return fetchEvents(start, end);
  }

  /**
   * Gets the events in the calendar on the given date. Returns list of events that are on the
   * given
   *
   * @param startDate The Start date of the range.
   *
   * @return the list of events that come on the given date.
   */
  @Override
  protected List<CalendarEntry> getCalendarEntriesOnDay(String startDate) {
    ZonedDateTime start = stringToZdt(startDate, timeZone).withHour(0).withMinute(0);
    ZonedDateTime end;
    end = start.withHour(23).withMinute(59);
    return fetchEvents(start, end);
  }

  private List<CalendarEntry> fetchEvents(ZonedDateTime startDateTime, ZonedDateTime endDateTime) {
    List<CalendarEntry> fetchedEvents = new LinkedList<>();

    for (CalendarEntry event : events) {
      List<CalendarEntry> eventsInRange = event.getEventsIfWithinRange(startDateTime, endDateTime);
      if (!(eventsInRange == null || eventsInRange.isEmpty())) {
        fetchedEvents.addAll(eventsInRange);
      }
    }
    return fetchedEvents;
  }

  /**
   * Gets the event with the given name and start date time.
   *
   * @param eventName     the name of the event.
   * @param startDateTime the start date time of the event.
   *
   * @return the event with the given name and start date time if found, null otherwise.
   */
  @Override
  protected CalendarEntry getEventWithStartDateTime(String eventName, String startDateTime) {
    ZonedDateTime startDateTimeZDT = stringToZdt(startDateTime, timeZone);
    for (CalendarEntry event : events) {
      AbstractMap.SimpleEntry<CalendarEntry, Boolean> matchedEvent
              = event.isEventStartWith(eventName, startDateTimeZDT);
      if (matchedEvent.getKey() != null) {
        return matchedEvent.getKey();
      }
    }
    return null;
  }

  /**
   * Adds the given events to the calendar. If autoDecline is true, any conflicts caused by the new
   * events will result in the add action to fail, resulting in no new events being created.
   *
   * @param eventsToAdd the events to add.
   * @param autoDecline whether to automatically decline conflicting events.
   */
  @Override
  protected void addEvents(List<CalendarEntry> eventsToAdd, boolean autoDecline) {
    // First check all events for conflicts
    if (autoDecline) {
      for (CalendarEntry entry : eventsToAdd) {
        if (checkConflict(entry)) {
          throw new IllegalArgumentException("Conflict detected, no events were added");
        }
      }
    }

    // If we reach here, all events are conflict-free
    this.events.addAll(eventsToAdd);
  }

  /**
   * Get all event details of the event with the given name, start date time and end date time.
   *
   * @param eventName     the name of the event.
   * @param startDateTime the start date time of the event.
   *
   * @return a map of event details with keys as field names and values as field values.
   */
  @Override
  public Map<String, String> getExactEvent(
          String eventName,
          String startDateTime) {
    ZonedDateTime startDateTimeZDT = stringToZdt(startDateTime, timeZone);
    for (CalendarEntry event : events) {
      AbstractMap.SimpleEntry<CalendarEntry, Boolean> matchedEvent
              = event.isEventStartWith(eventName, startDateTimeZDT);
      if (matchedEvent.getKey() != null) {
        List<Map<String, String>> matchedEventMap = matchedEvent.getKey().getAllEventDetails();
        Map<String, String> cleanedMap = cleanMapKeyValues(matchedEventMap.get(0));
        if (matchedEvent.getValue()) {
          cleanedMap.put("Is Recurring", "True");
        } else {
          cleanedMap.put("Is Recurring", "False");
        }

        return cleanedMap;
      }
    }

    return Map.of();
  }

  private Map<String, String> cleanMapKeyValues(Map<String, String> data) {

    // Create the final map with only the required keys
    Map<String, String> resultMap = new LinkedHashMap<>();

    // Add fields with potential quote removal
    addCleanField(resultMap, data, "Name");


    // Add Start Date Time
    if (data.containsKey("StartDate") && data.containsKey("StartTime")) {
      resultMap.put("Start Date Time", data.get("StartDate") + " " + data.get("StartTime"));
    }

    // Add End Date Time
    if (data.containsKey("EndDate") && data.containsKey("EndTime")) {
      resultMap.put("End Date Time", data.get("EndDate") + " " + data.get("EndTime"));
    }

    addCleanField(resultMap, data, "Description");
    addCleanField(resultMap, data, "Location");

    // Add Public/Private flags
    if (data.containsKey("IsPrivate")) {
      boolean isPrivate = "True".equalsIgnoreCase(data.get("IsPrivate"));
      resultMap.put("Private", Boolean.toString(isPrivate));
    }

    // Add All Day
    if (data.containsKey("IsAllDay")) {
      resultMap.put("All Day", data.get("IsAllDay"));
    }

    return resultMap;
  }

  /**
   * Helper method to add a field from source map to target map with quotes removed.
   */
  private void addCleanField(
          Map<String, String> targetMap,
          Map<String, String> sourceMap,
          String fieldName) {

    if (sourceMap.containsKey(fieldName)) {
      String value = sourceMap.get(fieldName);
      // Remove surrounding quotes if present
      if (value != null && !value.isEmpty()) {
        if ((value.startsWith("\"") && value.endsWith("\""))) {
          value = value.substring(1, value.length() - 1);
        }
      }
      targetMap.put(fieldName, value);
    }
  }


  private void addSingleEventHelper(
          String name,
          String startDateTime,
          String endDateTime,
          String description,
          String location,
          boolean autoDecline,
          boolean isPrivate) {
    DateTimeProcessingResult result = processEventDateTime(startDateTime, endDateTime, timeZone);
    ZonedDateTime startZonedDateTime = result.startDateTime;
    ZonedDateTime endZonedDateTime = result.endDateTime;
    boolean isAllDay = result.isAllDay;

    CalendarEntry singleEvent = new SingleEvent(name, startZonedDateTime, endZonedDateTime,
            description, location, isPrivate, isAllDay);

    if (autoDecline) {
      if (checkConflict(singleEvent)) {
        throw new IllegalArgumentException("Conflict detected, Event not Created");
      }
    }

    events.add(singleEvent);
  }

  private void addRecurringEventUntilHelper(
          String name,
          String description,
          String location,
          String weekDays,
          ZonedDateTime untilDateTime,
          boolean autoDecline,
          boolean isPrivate,
          ZonedDateTime startZonedDateTime,
          ZonedDateTime endZonedDateTime,
          boolean isAllDay) {
    CalendarEntry recurringEvent = new RecurringEvent(
            name,
            startZonedDateTime,
            endZonedDateTime,
            description,
            location,
            isPrivate,
            isAllDay,
            weekDays,
            untilDateTime);

    if (autoDecline) {
      if (checkConflict(recurringEvent)) {
        throw new IllegalArgumentException("Conflict detected, Event not Created");
      }
    }

    this.events.add(recurringEvent);
  }

  /**
   * Checks if the given event conflicts with any existing events in the calendar.
   *
   * @param singleEvent the event to check for conflicts
   *
   * @return true if the event conflicts with any existing events, false otherwise.
   */
  private boolean checkConflict(CalendarEntry singleEvent) {
    for (CalendarEntry entry : events) {
      if (entry.isConflict(singleEvent)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Undo the edit of the events.
   *
   * @param events the events to undo the edit.
   */
  private void undoEvents(List<CalendarEntry> events) {
    for (CalendarEntry event : events) {
      event.undoEdit();
    }
  }

  /**
   * Checks if the given event conflicts with any existing events in the calendar. If there is a
   * conflict, the events are not added to the calendar.
   *
   * @param editedEvent the events to check for conflicts.
   * @param autoDecline whether to automatically decline conflicting events.
   */
  private void checkConflictAndAddAll(List<CalendarEntry> editedEvent, boolean autoDecline) {
    if (autoDecline) {
      for (CalendarEntry event : editedEvent) {
        if (checkConflict(event)) {
          undoEvents(editedEvent);
          this.events.addAll(editedEvent);
          throw new IllegalArgumentException("Event conflicts with existing event");
        }
      }
    }
    this.events.addAll(editedEvent);
  }

  /**
   * Edits a recurring event in the calendar. Converts the startDateTime string to a ZonedDateTime
   * object. Find the event to edit and edit the property with the new value.
   *
   * @param eventName     the name of the event to edit.
   * @param startDateTime the start date and time of the event.
   * @param propertyName  the property to edit.
   * @param propertyValue the new value of the property.
   *
   * @throws IllegalArgumentException if the event is not found.
   */
  private void editMultipleEventsHelper(
          String eventName,
          String startDateTime,
          String propertyName,
          String propertyValue,
          boolean autoDecline) {
    ZonedDateTime startZonedDateTime = null;
    if (!startDateTime.isEmpty()) {
      startZonedDateTime = stringToZdt(startDateTime, timeZone);
    }
    List<CalendarEntry> editedEvent = new LinkedList<>();

    for (CalendarEntry event : events) {
      CalendarEntry matchedEvent = event.matchesRecurringEvent(eventName, startZonedDateTime);

      if (matchedEvent != null) {
        matchedEvent.editRecurringEvent(eventName, startZonedDateTime, propertyName, propertyValue);
        editedEvent.add(matchedEvent);
      }
    }

    this.events.removeAll(editedEvent);
    if (editedEvent.isEmpty()) {
      throw new IllegalArgumentException("Could not find any recurring event: " + eventName);
    }

    checkConflictAndAddAll(editedEvent, autoDecline);
  }
}