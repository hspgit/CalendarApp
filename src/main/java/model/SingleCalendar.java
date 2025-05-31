package model;

import java.util.List;
import java.util.Map;

import model.calendarentry.CalendarEntry;

/**
 * The SingleCalendar abstract class represents a single calendar the calendar application. It
 * provides methods to add, edit, and get events from the calendar. It also provides methods to
 * export the calendar and show the availability status of the calendar.
 */
public abstract class SingleCalendar {

  /**
   * Adds a single event to the calendar.
   *
   * @param name          the name of the event.
   * @param startDateTime the start date and time of the event.
   * @param endDateTime   the end date and time of the event.
   * @param description   the description of the event.
   * @param location      the location of the event.
   * @param autoDecline   whether the event should be automatically declined.
   * @param isPrivate     whether the event is private.
   */
  public abstract void addSingleEvent(
          String name,
          String startDateTime,
          String endDateTime,
          String description,
          String location,
          boolean autoDecline,
          boolean isPrivate
  );

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
  public abstract void addSingleEventAllDay(
          String name,
          String startDate,
          String description,
          String location,
          boolean autoDecline,
          boolean isPrivate
  );

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
  public abstract void addRecurringEventUntil(
          String name,
          String startDateTime,
          String endDateTime,
          String description,
          String location,
          String weekDays,
          String untilDateTime,
          boolean autoDecline,
          boolean isPrivate
  );

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
  public abstract void addRecurringEventFrequency(
          String name,
          String startDateTime,
          String endDateTime,
          String description,
          String location,
          String weekDays,
          Integer frequency,
          boolean autoDecline,
          boolean isPrivate
  );

  /**
   * Adds a recurring event to the calendar until a given date and time.
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
  public abstract void addRecurringAllDayEventUntil(
          String name,
          String startDate,
          String description,
          String location,
          String weekDays,
          String untilDateTime,
          boolean autoDecline,
          boolean isPrivate
  );

  /**
   * Adds a all day recurring event with the given frequency.
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
  public abstract void addRecurringAllDayEventFrequency(
          String name,
          String startDate,
          String description,
          String location,
          String weekDays,
          Integer frequency,
          boolean autoDecline,
          boolean isPrivate
  );

  /**
   * Edits a single event in the calendar.
   *
   * @param eventName     the name of the event to edit.
   * @param startDateTime the start date and time of the event.
   * @param endDateTime   the end date and time of the event.
   * @param propertyName  the property to edit.
   * @param propertyValue the new value of the property.
   */
  public abstract void editSingleEvent(
          String eventName,
          String startDateTime,
          String endDateTime,
          String propertyName,
          String propertyValue,
          boolean autoDecline
  );

  /**
   * Edits all the events with the event name and following the start date time.
   *
   * @param eventName     the name of the event to edit.
   * @param startDateTime the start date and time of the event.
   * @param propertyName  the property to edit.
   * @param propertyValue the new value of the property.
   * @param autoDecline   whether to automatically decline conflicting events.
   */
  public abstract void editMultipleEventsFollowing(
          String eventName,
          String startDateTime,
          String propertyName,
          String propertyValue,
          boolean autoDecline
  );

  /**
   * Edits all the events with the event name.
   *
   * @param eventName     the name of the event to edit.
   * @param propertyName  the property to edit.
   * @param propertyValue the new value of the property.
   * @param autoDecline   whether to automatically decline conflicting events.
   */
  public abstract void editMultipleEventsAll(
          String eventName,
          String propertyName,
          String propertyValue,
          boolean autoDecline
  );

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
  public abstract Map<String, String>[] getEventsRange(String startDateTime, String endDateTime);

  /**
   * Gets the events in the calendar between the given start and end date times. Returns Name,
   * StartDateTime, EndDateTime, Location. If endDateTime is empty, it will return all events on the
   * given start day.
   *
   * @param startDate the start date and time.
   *
   * @return the events in the calendar between the given start and end date times.
   */
  public abstract Map<String, String>[] getEventsOnDate(String startDate);


  /**
   * Exports the calendar to a file.
   *
   * @return The absolute path of the file.
   */
  public abstract Map<String,String>[] getAllEvents();


  /**
   * Shows the availability status of the calendar.
   *
   * @param startDateTime the start date and time.
   *
   * @return the availability status of the calendar for the given start date and time.
   */
  public abstract String getStatusOnDateTime(String startDateTime);

  /**
   * Shows the availability status of the calendar within the given range.
   *
   * @param rangeStartDateTime the start date and time of the range.
   * @param rangeEndDateTime   the end date and time of the range.
   *
   * @return "Available" if the calendar is available within the given range, "Busy" otherwise.
   */
  public abstract String getStatusInRange(String rangeStartDateTime, String rangeEndDateTime);

  /**
   * Changes the timezone of the calendar.
   *
   * @param newTimezone the new timezone.
   */
  public abstract void changeTimezone(String newTimezone);

  /**
   * Gets the events in the calendar between the given start and end date times. Returns list of
   * events that are within the given range.
   *
   * @param startDate The Start date of the range.
   * @param endDate   the end date of the range.
   *
   * @return the list of events that come within the given range.
   */
  protected abstract List<CalendarEntry> getCalendarEntriesWithinRange(
          String startDate,
          String endDate);

  /**
   * Gets the events in the calendar on the specific day. Returns list of events that are within the
   * given range.
   *
   * @param startDate The Start date of the range.
   *
   * @return the list of events that come within the given day.
   */
  protected abstract List<CalendarEntry> getCalendarEntriesOnDay(String startDate);

  /**
   * Gets the event with the given name and start date time.
   *
   * @param eventName     the name of the event.
   * @param startDateTime the start date time of the event.
   *
   * @return the event with the given name and start date time if found, null otherwise.
   */
  protected abstract CalendarEntry getEventWithStartDateTime(
          String eventName,
          String startDateTime);

  /**
   * Adds the given events to the calendar. If autoDecline is true, any conflicts caused by the new
   * events will result in the add action to fail, resulting in no new events being created.
   *
   * @param events      the events to add.
   * @param autoDecline whether to automatically decline conflicting events.
   */
  protected abstract void addEvents(List<CalendarEntry> events, boolean autoDecline);

  /**
   * Gets the events in the calendar with the given name and start date time.
   *
   * @param eventName     the name of the event.
   * @param startDateTime the start date time of the event.
   *
   * @return a map of event properties and their values.
   */
  public abstract Map<String, String> getExactEvent(
          String eventName,
          String startDateTime);
}
