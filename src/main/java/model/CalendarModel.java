package model;

import java.util.Map;

/**
 * This interface represents the functionality of a multi-calendar system. It allows the user to
 * create, edit, and copy events between calendars. The user can also switch between calendars. The
 * user should be able to get the current calendar. When handling time zones, the timezone string
 * must be in the IANA Time Zone Database format.
 */
public interface CalendarModel {

  /**
   * Adds a new calendar to the system.
   *
   * @param calendarName the name of the new calendar.
   * @param timeZone     the time zone of the new calendar.
   *
   * @throws IllegalArgumentException if the calendar already exists or the time zone is invalid.
   */
  void addCalendar(String calendarName, String timeZone) throws IllegalArgumentException;

  /**
   * Changes the current calendar to the calendar with the given name.
   *
   * @param calendarName the name of the calendar to switch to.
   *
   * @throws IllegalArgumentException if the calendar does not exist.
   */
  void useCalendar(String calendarName) throws IllegalArgumentException;

  /**
   * Edits the given property of the given calendar.
   *
   * @param calendarName the name of the calendar to edit.
   * @param property     the property to edit.
   * @param value        the new value of the property.
   *
   * @throws IllegalArgumentException if the calendar does not exist or the property is invalid.
   */
  void editCalendar(
          String calendarName,
          String property,
          String value) throws IllegalArgumentException;

  /**
   * Copies an event from the current calendar to the target calendar. If autoDecline is true, any
   * conflicts caused by the new copy will result in the copy action to fail, resulting in no new
   * event being created.
   *
   * @param eventName          the name of the event to copy.
   * @param targetCalendarName the name of the target calendar.
   * @param sourceDateTime     the date and time of the event to copy.
   * @param targetDateTime     the date and time of the new event.
   * @param autoDecline        whether to automatically decline conflicting events.
   */
  void copySingleEvent(
          String eventName,
          String targetCalendarName,
          String sourceDateTime,
          String targetDateTime,
          boolean autoDecline);

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
  void copyMultipleEventsRange(
          String targetCalendarName,
          String rangeStartDate,
          String rangeEndDate,
          String targetDate,
          boolean autoDecline);

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
  void copyMultipleEventsOnDay(
          String targetCalendarName,
          String rangeStartDate,
          String targetDate,
          boolean autoDecline);

  /**
   * Gets the current calendar.
   *
   * @return the current calendar.
   */
  SingleCalendar getCurrentCalendar();


  /**
   * Gets the list of all calendars in the system. Name, time zone pairs are returned in a
   * SimpleEntry.
   *
   * @return the map of all calendars.
   */
  Map<String, String> getAllCalendars();

  /**
   * Gets the name of the current calendar.
   *
   * @return the name of the current calendar.
   */
  String getCurrentCalendarName();

}
