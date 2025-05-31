package model.calendarentry;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


import static utils.DateTimeUtils.isTimeOverLapping;

/**
 * Represents a single event in a calendar. A single event is an event that occurs once and has at
 * least the start time.
 */
public class SingleEvent extends CalendarEntry {

  /**
   * The set of restricted edits for a single event. We need this because a single event can be
   * matched with edit multiple commands, and we need to restrict the edit of certain properties.
   */
  private final Set<String> restrictedEdits = Set.of(
          "startDateTime",
          "endDateTime",
          "frequency",
          "untilDateTime",
          "weekDays");

  /**
   * Constructs a single event with the given name, start date time, end date time, description,
   * location, privacy, and all day status.
   *
   * @param name          the name of the event
   * @param startDateTime the start date time of the event
   * @param endDateTime   the end date time of the event
   * @param description   the description of the event
   * @param location      the location of the event
   * @param isPrivate     the privacy of the event
   * @param isAllDay      the all day status of the event
   */
  public SingleEvent(
          String name,
          ZonedDateTime startDateTime,
          ZonedDateTime endDateTime,
          String description,
          String location,
          boolean isPrivate,
          boolean isAllDay) {
    super(name, startDateTime, endDateTime, description, location, isPrivate, isAllDay);
    if (isAllDay) { // Set start time to 00:00 and end time to 23:59
      this.startDateTime = startDateTime.toLocalDate().atStartOfDay(startDateTime.getZone());
      this.endDateTime = startDateTime.withHour(23).withMinute(59);
    }
    previousState = null;
  }

  /**
   * Copy constructor for a single event.
   *
   * @param singleEvent the single event that needs to be copied.
   */
  private SingleEvent(SingleEvent singleEvent) {
    super(
            singleEvent.name,
            singleEvent.startDateTime,
            singleEvent.endDateTime,
            singleEvent.description,
            singleEvent.location,
            singleEvent.isPrivate,
            singleEvent.isAllDay
    );
  }

  /**
   * Checks if this single event conflicts with another calendar entry.
   *
   * @param other the other calendar entry
   *
   * @return true if this single event conflicts with the other calendar entry, false otherwise.
   */
  @Override
  public boolean isConflict(CalendarEntry other) {
    return other.isConflictWithSingleEvent(this);
  }

  /**
   * Checks if this single event conflicts with another single event. A single event conflicts with
   * another single event if their time overlaps.
   *
   * @param other the other single event
   *
   * @return true if this single event conflicts with the other single event, false otherwise.
   */
  @Override
  public boolean isConflictWithSingleEvent(SingleEvent other) {
    return isTimeOverLapping(
            startDateTime,
            endDateTime,
            other.startDateTime,
            other.endDateTime);
  }


  /**
   * Checks if this single event conflicts with a recurring event. A single event conflicts with a
   * recurring event if any of the recurring event's occurrences overlap with this single event.
   *
   * @param other the recurring event
   *
   * @return true if this single event conflicts with the recurring event, false otherwise.
   */
  @Override
  boolean isConflictWithRecurringEvent(RecurringEvent other) {
    return other.isConflictWithSingleEvent(this);
  }

  /**
   * Checks if the single event has the same name and start date time. Returns the copy of the
   * single event if the event matches, null otherwise.
   *
   * @param eventName the name of the event.
   * @param startZDT  the start date time of the event.
   *
   * @return the copy of the single event if the event matches, null otherwise.
   */
  @Override
  public AbstractMap.SimpleEntry<CalendarEntry, Boolean> isEventStartWith(
          String eventName,
          ZonedDateTime startZDT) {
    if (eventName.equals(this.name) && startDateTime.equals(startZDT)) {
      return new AbstractMap.SimpleEntry<>(this.copy(), false);
    }

    return new AbstractMap.SimpleEntry<>(null, false);
  }

  /**
   * Moves the event by the given number of days.
   *
   * @param offsetDays the number of days to move the event by.
   */
  @Override
  public void addOffsetDays(int offsetDays) {
    this.startDateTime = this.startDateTime.plusDays(offsetDays);
    this.endDateTime = this.endDateTime.plusDays(offsetDays);
  }

  /**
   * Gets the event details for the given time range. The event details include the event name,
   * start date time, end date time and location.
   *
   * @param startZTD the start date time of the time range
   * @param endZTD   the end date time of the time range
   *
   * @return the list of event details
   */
  @Override
  public List<Map<String,String>> getEventDetails(
          ZonedDateTime startZTD,
          ZonedDateTime endZTD,
          boolean isRecurring) {
    final boolean isInRange = isTimeOverLapping(
            startDateTime,
            endDateTime,
            startZTD,
            endZTD);
    if (!isInRange) {
      return List.of(); // Return an empty list if event is not in range
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    Map<String, String> eventDetails = Map.of(
        "Name", name != null ? name : "",
        "StartDateTime", startDateTime != null ? startDateTime.format(formatter) : "",
        "EndDateTime", endDateTime != null ? endDateTime.format(formatter) : "",
        "Description", description != null ? description : "",
        "Location", location != null ? location : "",
        "IsPrivate", String.valueOf(isPrivate),
        "IsAllDay", String.valueOf(isAllDay),
        "IsRecurring", String.valueOf(isRecurring)
    );

    return List.of(eventDetails);
  }

  /**
   * Converts the single event to a string having all the properties separated by commas. This is
   * useful for exporting the single event to a CSV file.
   *
   * @return the formatted string.
   */
  @Override
  public List<Map<String,String>> getAllEventDetails() {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    Map<String, String> eventDetails = Map.of(
        "Name", name != null ? name : "",
        "StartDate", startDateTime != null ? startDateTime.format(dateFormatter) : "",
        "StartTime", startDateTime != null ? startDateTime.format(timeFormatter) : "",
        "EndDate", endDateTime != null ? endDateTime.format(dateFormatter) : "",
        "EndTime", endDateTime != null ? endDateTime.format(timeFormatter) : "",
        "Description", description != null ? description : "",
        "Location", location != null ? location : "",
        "IsPrivate", String.valueOf(isPrivate),
        "IsAllDay", String.valueOf(isAllDay)
    );
    return List.of(eventDetails);
  }

  /**
   * Updates the time zone of the Single event.
   *
   * @param timeZone the new time zone
   */
  @Override
  public void updateZone(ZoneId timeZone) {
    startDateTime = startDateTime.withZoneSameInstant(timeZone);
    endDateTime = endDateTime.withZoneSameInstant(timeZone);
    isAllDay = checkAllDay(startDateTime, endDateTime);
  }

  /**
   * Updates the date time of the calendar entry such that it starts on the target date time in the
   * provided time zone.
   *
   * @param targetZDT the new date time along with the time zone.
   */
  @Override
  public void updateDateTime(ZonedDateTime targetZDT) {
    final TemporalAmount duration = Duration.between(startDateTime, endDateTime);
    startDateTime = targetZDT;
    endDateTime = targetZDT.plus(duration);
    isAllDay = checkAllDay(startDateTime, endDateTime);
  }

  /**
   * Gets the events in the calendar between the given start and end date times.
   *
   * @param startZDT the start date of the time range
   * @param endZDT   the end date of the time range
   *
   * @return the copy of the single event if it falls within the time range, null otherwise.
   */
  @Override
  public List<CalendarEntry> getEventsIfWithinRange(ZonedDateTime startZDT, ZonedDateTime endZDT) {
    if (isTimeOverLapping(
            startDateTime,
            endDateTime,
            startZDT,
            endZDT)) {
      return List.of(this.copy());
    }
    return null;
  }

  /**
   * Edits a single event with the given property name and property value.
   *
   * @param propertyName  the name of the property to edit
   * @param propertyValue the new value of the property
   */
  @Override
  public void editSingleEvent(
          String eventName,
          ZonedDateTime startDateTime,
          ZonedDateTime endDateTime,
          String propertyName,
          String propertyValue) {
    previousState = new SingleEvent(this);
    updateSimpleParameters(propertyName, propertyValue);
  }

  /**
   * Edits the single event if the property name is not complex(restricted), i.e., startDateTime,
   * endDateTime, frequency and daysOfWeek. Fails silently otherwise.
   *
   * @param eventName     the name of the event
   * @param startDateTime the start date time of the event
   * @param propertyName  the name of the property to edit
   * @param propertyValue the new value of the property
   */
  @Override
  public void editRecurringEvent(
          String eventName,
          ZonedDateTime startDateTime,
          String propertyName,
          String propertyValue) {
    if (restrictedEdits.contains(propertyName)) {
      // fail Silently for complex parameters.
      return;
    }
    editSingleEvent(eventName, startDateTime, endDateTime, propertyName, propertyValue);
  }

  /**
   * Matches a single event. A single event is considered to match if the event name, start date
   * time, and end date time are the same.
   *
   * @param eventName     the name of the event
   * @param startDateTime the start date time of the event
   * @param endDateTime   the end date time of the event
   *
   * @return the single event if it matches, false otherwise.
   */
  @Override
  public CalendarEntry matchesSingleOccurrence(
          String eventName,
          ZonedDateTime startDateTime,
          ZonedDateTime endDateTime) {
    if (name.equals(eventName)
            && this.startDateTime.equals(startDateTime)
            && this.endDateTime.equals(endDateTime)) {
      return this;
    }
    return null;
  }

  /**
   * Undo the edit by reverting the Single event to its previous state.
   */
  @Override
  public void undoEdit() {
    super.undoEdit();
    previousState = previousState.previousState;
  }

  /**
   * Copies the single event.
   *
   * @return the copy of the single event.
   */
  @Override
  public CalendarEntry copy() {
    return new SingleEvent(this);
  }

  /**
   * Matches a recurring event.
   *
   * @param eventName     the name of the event
   * @param startDateTime the start date time of the event
   *
   * @return this if the event matches, null otherwise.
   */
  @Override
  public CalendarEntry matchesRecurringEvent(String eventName, ZonedDateTime startDateTime) {
    if (startDateTime == null) {
      if (name.equals(eventName)) {
        return this;
      }
    }

    if (name.equals(eventName) && !this.startDateTime.isBefore(startDateTime)) {
      return this;
    }

    return null;
  }


}
