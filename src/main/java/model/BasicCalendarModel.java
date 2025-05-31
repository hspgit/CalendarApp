package model;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import model.calendarentry.CalendarEntry;

import static utils.DateTimeUtils.isInvalidTimeZone;
import static utils.DateTimeUtils.offsetDaysBetweenDates;
import static utils.DateTimeUtils.stringToZdt;

/**
 * This class implements the CalendarModel interface. It represents the functionality of a
 * multi-calendar system. It allows the user to create, edit, and copy events between calendars. The
 * user can also switch between calendars. The user should be able to get the current calendar. It
 * contains a map of calendars, where the key is the calendar name and the value is a pair of the
 * calendar's time zone and the calendar itself. It also contains the current calendar.
 */
public class BasicCalendarModel implements CalendarModel {

  /**
   * The map of calendars, where the key is the calendar name and the value is a pair of the
   * calendar's time zone and the calendar itself.
   */
  private final Map<String, AbstractMap.SimpleEntry<String, SingleCalendar>> calendars;
  private SingleCalendar currentCalendar;

  /**
   * Constructs a BasicCalendarModel object with the default time zone. Adds a default calendar to
   * the map of calendars. The default calendar has the default time zone and is the current
   * calendar. The default time zone is the system's default time zone.
   */
  public BasicCalendarModel() {
    final String currentTimeZone = TimeZone.getDefault().getID();
    currentCalendar = new BasicSingleCalendar(currentTimeZone);
    this.calendars = new HashMap<>();
    this.calendars.put("default", new AbstractMap.SimpleEntry<>(currentTimeZone, currentCalendar));
  }

  /**
   * Adds a new calendar to the system. Validates the calendar name and time zone before adding the
   * calendar to the map of calendars. The time zone must be a valid timezone, according to the IANA
   * Time Zone Database format.
   *
   * @param calendarName the name of the new calendar.
   * @param timeZone     the time zone of the new calendar.
   */
  @Override
  public void addCalendar(String calendarName, String timeZone) {
    validateNameAndTimeZone(calendarName, timeZone);
    calendars.put(
            calendarName,
            new AbstractMap.SimpleEntry<>(timeZone, new BasicSingleCalendar(timeZone))
    );
  }

  /**
   * Edits the given property of the given calendar. The property can be the name or the timezone of
   * the calendar. Validates the calendar name and time zone before editing the calendar. The time
   * zone must be a valid timezone, according to the IANA Time Zone Database format.
   *
   * @param calendarName the name of the calendar to edit.
   * @param property     the property to edit.
   * @param value        the new value of the property.
   */
  @Override
  public void editCalendar(String calendarName, String property, String value) {
    checkCalendarDoesNotExist(calendarName);
    switch (property) {
      case "name":
        checkCalendarExist(value);
        calendars.put(value, calendars.remove(calendarName));
        break;
      case "timezone":
        if (isInvalidTimeZone(value)) {
          throw new IllegalArgumentException("Invalid timezone: " + value);
        }
        calendars.get(calendarName).getValue().changeTimezone(value);
        calendars.put(
                calendarName,
                new AbstractMap.SimpleEntry<>(value, calendars.get(calendarName).getValue())
        );
        break;
      default:
        throw new IllegalArgumentException("Invalid property: " + property);
    }

  }

  /**
   * Changes the current calendar to the calendar with the given name. Validates the calendar name
   * before switching to the calendar.
   *
   * @param calendarName the name of the calendar to switch to.
   */
  @Override
  public void useCalendar(String calendarName) {
    checkCalendarDoesNotExist(calendarName);
    currentCalendar = calendars.get(calendarName).getValue();
  }

  /**
   * Copies an event from the current calendar to the target calendar. If autoDecline is true, any
   * conflicts caused by the new copy will result in the copy action to fail, resulting in no new
   * event being created. First, the event to copy is retrieved from the current calendar. Then the
   * event is updated to the target date and time. Finally, the event is added to the target
   * calendar.
   *
   * @param eventName          the name of the event to copy.
   * @param targetCalendarName the name of the target calendar.
   * @param sourceDateTime     the date and time of the event to copy.
   * @param targetDateTime     the date and time of the new event.
   * @param autoDecline        whether to automatically decline conflicting events.
   */
  @Override
  public void copySingleEvent(
          String eventName,
          String targetCalendarName,
          String sourceDateTime,
          String targetDateTime,
          boolean autoDecline) {
    checkCalendarDoesNotExist(targetCalendarName);
    CalendarEntry eventToCopy = currentCalendar.getEventWithStartDateTime(
            eventName,
            sourceDateTime);
    if (eventToCopy == null) {
      throw new IllegalArgumentException("Event does not exist: " + eventName);
    }
    String otherCalendarTimeZone = calendars.get(targetCalendarName).getKey();
    SingleCalendar otherCalendar = calendars.get(targetCalendarName).getValue();
    ZonedDateTime targetZDT = stringToZdt(targetDateTime, otherCalendarTimeZone);

    eventToCopy.updateDateTime(targetZDT);
    otherCalendar.addEvents(List.of(eventToCopy), autoDecline);
  }

  /**
   * Copies all events from the current calendar to the target calendar that fall within the given
   * date range. If autoDecline is true, any conflicts caused by the new copies will result in the
   * copy action to fail, resulting in no new events being created.
   *
   * @param targetCalendarName the name of the target calendar.
   * @param rangeStartDate     the start date of the range.
   * @param rangeEndDate       the end date of the range.
   * @param targetDate         the date of the new events.
   * @param autoDecline        whether to automatically decline conflicting events.
   */
  @Override
  public void copyMultipleEventsRange(
          String targetCalendarName,
          String rangeStartDate,
          String rangeEndDate,
          String targetDate,
          boolean autoDecline) {
    checkCalendarDoesNotExist(targetCalendarName);

    List<CalendarEntry> eventsToCopy = currentCalendar
            .getCalendarEntriesWithinRange(rangeStartDate, rangeEndDate);

    int offSet = offsetDaysBetweenDates(rangeStartDate, targetDate);
    addEventToCalendar(targetCalendarName, offSet, eventsToCopy, autoDecline);
  }

  /**
   * Copies all events from the current calendar to the target calendar that fall within the given
   * day. If autoDecline is true, any conflicts caused by the new copies will result in the copy
   * action to fail, resulting in no new events being created.
   *
   * @param targetCalendarName the name of the target calendar.
   * @param rangeStartDate     the start date of the range.
   * @param targetDate         the date of the new events.
   * @param autoDecline        whether to automatically decline conflicting events.
   */
  @Override
  public void copyMultipleEventsOnDay(
          String targetCalendarName,
          String rangeStartDate,
          String targetDate,
          boolean autoDecline) {
    checkCalendarDoesNotExist(targetCalendarName);

    List<CalendarEntry> eventsToCopy = currentCalendar
            .getCalendarEntriesOnDay(rangeStartDate);

    int offSet = offsetDaysBetweenDates(rangeStartDate, targetDate);
    addEventToCalendar(targetCalendarName, offSet, eventsToCopy, autoDecline);

  }

  /**
   * Adds a list of calendar entries to the target calendar with the specified offset. This helper
   * method updates the events' date/time based on the provided offset and changes their time zone
   * to match the target calendar before adding them.
   *
   * @param targetCalendarName the name of the calendar to add events to
   * @param offSet             the number of days to offset each event by
   * @param eventsToCopy       the list of calendar entries to be copied to the target calendar
   * @param autoDecline        whether to automatically decline events that conflict with existing
   *                           events
   */
  private void addEventToCalendar(
          String targetCalendarName,
          int offSet,
          List<CalendarEntry> eventsToCopy,
          boolean autoDecline) {
    ZoneId targetTimeZone = ZoneId.of(calendars.get(targetCalendarName).getKey());

    for (CalendarEntry event : eventsToCopy) {
      event.addOffsetDays(offSet);
      event.updateZone(targetTimeZone);
    }

    SingleCalendar otherCalendar = calendars.get(targetCalendarName).getValue();
    otherCalendar.addEvents(eventsToCopy, autoDecline);
  }

  /**
   * Gets the current calendar. Used interface for loose coupling.
   *
   * @return the instance of the current calendar.
   */
  @Override
  public SingleCalendar getCurrentCalendar() {
    return currentCalendar;
  }


  /**
   * Gets the current calendar name. Used interface for loose coupling.
   *
   * @return the name of the current calendar.
   */
  @Override
  public String getCurrentCalendarName() {
    return calendars.entrySet().stream()
            .filter(entry -> entry.getValue().getValue() == currentCalendar)
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);
  }


  /**
   * Gets the list of all calendars in the system. Name, time zone pairs are returned in a
   * SimpleEntry.
   *
   * @return the map of all calendars.
   */
  @Override
  public Map<String, String> getAllCalendars() {
    return calendars.entrySet().stream()
            .collect(Collectors.toMap(
                    Map.Entry::getKey, entry -> entry.getValue().getKey()
            ));
  }


  /**
   * Validates the calendar name and time zone before adding a new calendar to the map of calendars.
   * The time zone must be a valid timezone, according to the IANA Time Zone Database format.
   *
   * @param calendarName the name of the new calendar.
   * @param timeZone     the time zone of the new calendar.
   *
   * @throws IllegalArgumentException if the calendar name is empty, the calendar already exists, or
   *                                  the time zone is invalid.
   */
  private void validateNameAndTimeZone(String calendarName, String timeZone) {
    if (calendarName == null || calendarName.isBlank()) {
      throw new IllegalArgumentException("Calendar name cannot be empty");
    }
    checkCalendarExist(calendarName);
    if (timeZone == null || timeZone.isBlank()) {
      throw new IllegalArgumentException("Timezone cannot be empty");
    }
    if (isInvalidTimeZone(timeZone)) {
      throw new IllegalArgumentException("Invalid timezone: " + timeZone);
    }
  }

  /**
   * Checks if the calendar with the given name does not exist in the map of calendars.
   *
   * @param calendarName the name of the calendar to check.
   *
   * @throws IllegalArgumentException if the calendar does not exist.
   */
  private void checkCalendarDoesNotExist(String calendarName) {
    if (!calendars.containsKey(calendarName)) {
      throw new IllegalArgumentException("Calendar does not exist: " + calendarName);
    }
  }

  /**
   * Checks if the calendar with the given name already exists in the map of calendars.
   *
   * @param calendarName the name of the calendar to check.
   *
   * @throws IllegalArgumentException if the calendar already exists.
   */
  private void checkCalendarExist(String calendarName) {
    if (calendars.containsKey(calendarName)) {
      throw new IllegalArgumentException("Calendar already exists: " + calendarName);
    }
  }


}
