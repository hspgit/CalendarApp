package model.calendarentry;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static utils.DateTimeUtils.stringToZdt;


/**
 * Abstract class representing a calendar entry. A calendar entry can be a single event or a
 * recurring event. Using ZonedDateTime concrete class directly because it is recommended in
 * official java documentation, and we need public methods from ZonedDateTime class that are not
 * available in the parent interface ChronoZonedDateTime.
 */
public abstract class CalendarEntry {

  protected String name;
  protected ZonedDateTime startDateTime;
  protected ZonedDateTime endDateTime;
  protected String description;
  protected String location;
  protected boolean isPrivate;
  protected boolean isAllDay;
  protected CalendarEntry previousState;

  /**
   * Constructs a calendar entry with the given parameters.
   *
   * @param name          the name of the calendar entry
   * @param startDateTime the start date time of the calendar entry
   * @param endDateTime   the end date time of the calendar entry
   * @param description   the description of the calendar entry
   * @param location      the location of the calendar entry
   * @param isPrivate     whether the calendar entry is private
   * @param isAllDay      whether the calendar entry is an all-day event
   */
  protected CalendarEntry(
          String name,
          ZonedDateTime startDateTime,
          ZonedDateTime endDateTime,
          String description,
          String location,
          boolean isPrivate,
          boolean isAllDay) {
    this.name = name;
    this.isPrivate = isPrivate;
    this.isAllDay = isAllDay;
    this.description = description;
    this.location = location;
    previousState = null;

    if (isAllDay) { // Set start time to 00:00 and end time to 23:59
      this.startDateTime = startDateTime.toLocalDate().atStartOfDay(startDateTime.getZone());
      this.endDateTime = startDateTime.withHour(23).withMinute(59);
    } else {
      this.startDateTime = startDateTime;
      this.endDateTime = endDateTime;
    }
  }

  /**
   * Checks if the calendar entry conflicts with another calendar entry. Checking for conflicts if
   * start and end date times overlap.
   *
   * @param other the other calendar entry
   *
   * @return true if the calendar entries conflict, false otherwise
   */
  public abstract boolean isConflict(CalendarEntry other);

  /**
   * Gets the event details for the given time range. The event details include the event name,
   * start date time, end date time and location.
   *
   * @param startZDT the start date time of the time range
   * @param endZDT   the end date time of the time range
   *
   * @return the list of event details
   */
  public abstract List<Map<String, String>> getEventDetails(
          ZonedDateTime startZDT,
          ZonedDateTime endZDT,
          boolean isRecurring);

  /**
   * Converts the calendar entry details into a formatted string. This can also be used to export
   * the calendar entry to a CSV file.
   *
   * @return the formatted string.
   */
  public abstract List<Map<String, String>> getAllEventDetails();

  /**
   * Updates the time zone of the calendar entry.
   *
   * @param timeZone the new time zone
   */
  public abstract void updateZone(ZoneId timeZone);

  /**
   * Updates the date time of the calendar entry such that it starts on the target date time in the
   * provided time zone.
   *
   * @param targetZDT the new date time along with the time zone.
   */
  public abstract void updateDateTime(ZonedDateTime targetZDT);

  /**
   * Gets the list of events that are within the given time range.
   *
   * @param startZDT the start date of the time range
   * @param endZDT   the end date of the time range
   *
   * @return the list of events in the calendar entry that are within the time range
   */
  public abstract List<CalendarEntry> getEventsIfWithinRange(
          ZonedDateTime startZDT,
          ZonedDateTime endZDT);

  /**
   * Checks if the event starts with the given event name and start date time.
   *
   * @param eventName the name of the event.
   * @param startZDT  the start date time of the event.
   *
   * @return Pair of the event and a boolean indicating if the event starts with the given name. The
   *         boolean is true if the event is part of the recurring event.
   */
  public abstract AbstractMap.SimpleEntry<CalendarEntry, Boolean> isEventStartWith(
          String eventName,
          ZonedDateTime startZDT);

  /**
   * Move the event by the given number of days.
   *
   * @param offsetDays the number of days to move the event by.
   */
  public abstract void addOffsetDays(int offsetDays);

  /**
   * Edits a single event. Editing a single event involves editing the name, description, location,
   * private status, start date time, end date time, and whether the event is an all-day event.
   *
   * @param propertyName  the name of the property to edit
   * @param propertyValue the new value of the property
   */
  public abstract void editSingleEvent(
          String eventName,
          ZonedDateTime startDateTime,
          ZonedDateTime endDateTime,
          String propertyName,
          String propertyValue);

  /**
   * Edits a recurring event. Editing a recurring event involves editing all instances of the event
   * or a specific instance of the event or all future instances of the event.
   *
   * @param eventName     the name of the event
   * @param startDateTime the start date time of the event
   * @param propertyName  the name of the property to edit
   * @param propertyValue the new value of the property
   */
  public abstract void editRecurringEvent(
          String eventName,
          ZonedDateTime startDateTime,
          String propertyName,
          String propertyValue);

  /**
   * Matches a single event. A single event is considered to match if the event name, start date
   * time, and end date time are the same.
   *
   * @param eventName     the name of the event
   * @param startDateTime the start date time of the event
   * @param endDateTime   the end date time of the event
   *
   * @return the matching single event
   */
  public abstract CalendarEntry matchesSingleOccurrence(
          String eventName,
          ZonedDateTime startDateTime,
          ZonedDateTime endDateTime);

  /**
   * Matches a recurring event. This method is overridden by the RecurringEvent only.
   *
   * @param eventName     the name of the event
   * @param startDateTime the start date time of the event
   *
   * @return the matching recurring event
   */
  public abstract CalendarEntry matchesRecurringEvent(
          String eventName,
          ZonedDateTime startDateTime);

  /**
   * Creates a copy of the calendar entry. This method is overridden by the SingleEvent and
   * RecurringEvent classes.
   *
   * @return the copy of the calendar entry
   */
  public abstract CalendarEntry copy();

  abstract boolean isConflictWithSingleEvent(SingleEvent other);

  abstract boolean isConflictWithRecurringEvent(RecurringEvent other);

  /**
   * Updates the simple parameters of the calendar entry.
   *
   * @param propertyName  the name of the property to update
   * @param propertyValue the new value of the property
   */
  protected void updateSimpleParameters(String propertyName, String propertyValue) {
    final Map<String, Consumer<String>> propertyUpdaters = new HashMap<>();
    propertyUpdaters.put("name", this::setName);
    propertyUpdaters.put("description", this::setDescription);
    propertyUpdaters.put("location", this::setLocation);
    propertyUpdaters.put("public", value -> this.setPrivate(!Boolean.parseBoolean(value)));
    propertyUpdaters.put("private", value -> this.setPrivate(Boolean.parseBoolean(value)));
    propertyUpdaters.put("startDateTime", this::setStartDateTime);
    propertyUpdaters.put("endDateTime", this::setEndDateTime);
    propertyUpdaters.put("allDay", value -> this.setAllDay());

    Consumer<String> updater = propertyUpdaters.get(propertyName);
    if (updater != null) {
      updater.accept(propertyValue);
    } else {
      throw new IllegalArgumentException("Invalid property name: " + propertyName);
    }
  }

  /**
   * Updates to the previous state of the calendar entry.
   */
  public void undoEdit() {
    if (previousState != null) {
      this.name = previousState.name;
      this.startDateTime = previousState.startDateTime;
      this.endDateTime = previousState.endDateTime;
      this.description = previousState.description;
      this.location = previousState.location;
      this.isPrivate = previousState.isPrivate;
      this.isAllDay = previousState.isAllDay;
    }
  }

  private void setName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be null or empty");
    }
    this.name = name;
  }


  private void setStartDateTime(String newDateTime) {
    final ZonedDateTime startZDT = stringToZdt(newDateTime, startDateTime.getZone().toString());
    if (endDateTime.isBefore(startZDT)) {
      throw new IllegalArgumentException("Start date time cannot be after end date time");
    }
    startDateTime = startZDT;
    isAllDay = checkAllDay(startDateTime, endDateTime);
  }

  private void setEndDateTime(String newDateTime) {
    final ZonedDateTime endZDT = stringToZdt(newDateTime, endDateTime.getZone().toString());
    if (this.startDateTime.isAfter(endZDT)) {
      throw new IllegalArgumentException("End date time cannot be before start date time");
    }
    endDateTime = endZDT;
    isAllDay = checkAllDay(startDateTime, endDateTime);
  }

  private void setDescription(String newDescription) {
    description = newDescription;
  }

  private void setLocation(String newLocation) {
    location = newLocation;
  }

  private void setPrivate(boolean aPrivate) {
    isPrivate = aPrivate;
  }

  private void setAllDay() {
    startDateTime = startDateTime.toLocalDate().atStartOfDay(startDateTime.getZone());
    endDateTime = startDateTime.plusHours(23).withMinute(59);
    isAllDay = true;
  }

  protected boolean checkAllDay(ZonedDateTime startZDT, ZonedDateTime endZDT) {
    return startZDT.toLocalDate().equals(endZDT.toLocalDate())
            && endZDT.isEqual(startZDT.plusHours(23).withMinute(59));
  }

  public ZonedDateTime getStartDateTime() {
    return startDateTime;
  }

}
