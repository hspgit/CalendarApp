package model.calendarentry;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static utils.DateTimeUtils.calculateUntilDateTime;
import static utils.DateTimeUtils.isTimeOverLapping;
import static utils.DateTimeUtils.parseWeekDays;
import static utils.DateTimeUtils.stringToZdt;
import static utils.DateTimeUtils.zdtToString;
import static utils.DateTimeUtils.daysOfWeekToString;

/**
 * Represents a recurring event in the calendar. A recurring event is an event that occurs multiple
 * times over a period of time. The event can be repeated on specific days of the week or at a
 * specific frequency. The event can be edited by changing the start time, end time, days of the
 * week, frequency, or end time. It also stores a list of all the occurrences of the event.
 */
public class RecurringEvent extends CalendarEntry {

  private final List<CalendarEntry> calendarEntries;
  private ZonedDateTime untilDateTime;
  private List<DayOfWeek> daysOfWeek;
  private int frequency;

  /**
   * Constructs a recurring event with untilDateTime given. Generates occurrences based on the
   * startDateTime, endDateTime, untilDateTime, daysOfWeek, and the template event which is the
   * recurring event itself initially
   *
   * @param name          the name of the event
   * @param startDateTime the start date and time of the event
   * @param endDateTime   the end date and time of the event
   * @param description   the description of the event
   * @param location      the location of the event
   * @param isPrivate     whether the event is private
   * @param isAllDay      whether the event is an all-day event
   * @param daysOfWeekStr the days of the week the event occurs on
   * @param untilDateTime the end date and time of the event
   */
  public RecurringEvent(
          String name,
          ZonedDateTime startDateTime,
          ZonedDateTime endDateTime,
          String description,
          String location,
          boolean isPrivate,
          boolean isAllDay,
          String daysOfWeekStr,
          ZonedDateTime untilDateTime) {
    super(name, startDateTime, endDateTime, description, location, isPrivate, isAllDay);
    this.daysOfWeek = parseWeekDays(daysOfWeekStr);
    this.untilDateTime = untilDateTime;
    this.calendarEntries = generateOccurrences(
            startDateTime,
            endDateTime,
            untilDateTime,
            daysOfWeek,
            this);
    this.frequency = this.calendarEntries.size();
    previousState = null;
  }

  /**
   * Constructs a recurring event with frequency given. Generates occurrences based on the
   * startDateTime, endDateTime, frequency, daysOfWeek, and the template event which is the
   * recurring event itself initially
   *
   * @param name          the name of the event
   * @param startDateTime the start date and time of the event
   * @param endDateTime   the end date and time of the event
   * @param description   the description of the event
   * @param location      the location of the event
   * @param isPrivate     whether the event is private
   * @param isAllDay      whether the event is an all-day event
   * @param daysOfWeekStr the days of the week the event occurs on
   * @param frequency     the frequency of the event
   */
  public RecurringEvent(
          String name,
          ZonedDateTime startDateTime,
          ZonedDateTime endDateTime, String description,
          String location,
          boolean isPrivate,
          boolean isAllDay,
          String daysOfWeekStr,
          int frequency) {
    super(name, startDateTime, endDateTime, description, location, isPrivate, isAllDay);
    this.daysOfWeek = parseWeekDays(daysOfWeekStr);
    this.frequency = frequency;
    this.calendarEntries = generateOccurrencesByFrequency(
            startDateTime,
            endDateTime,
            frequency,
            daysOfWeek,
            this
    );
    this.untilDateTime = this.calendarEntries.get(this.calendarEntries.size() - 1).endDateTime;
    previousState = null;
  }

  /**
   * This is a copy constructor for the RecurringEvent class.
   *
   * @param recurringEvent the recurring event to copy.
   */
  private RecurringEvent(RecurringEvent recurringEvent) {
    super(
            recurringEvent.name,
            recurringEvent.startDateTime,
            recurringEvent.endDateTime,
            recurringEvent.description,
            recurringEvent.location,
            recurringEvent.isPrivate,
            recurringEvent.isAllDay
    );

    this.daysOfWeek = parseWeekDays(daysOfWeekToString(recurringEvent.daysOfWeek));
    this.frequency = recurringEvent.frequency;
    this.calendarEntries = copyCalendarEntries(recurringEvent.calendarEntries);
    this.previousState = recurringEvent.previousState;
    this.untilDateTime = recurringEvent.untilDateTime;
  }

  /**
   * Checks if the recurring event conflicts with another calendar entry.
   *
   * @param other the other calendar entry
   *
   * @return true if the recurring event conflicts with the other calendar entry, false otherwise
   */
  @Override
  public boolean isConflict(CalendarEntry other) {
    return other.isConflictWithRecurringEvent(this);
  }

  /**
   * Checks if the recurring event conflicts with a single event. The recurring event conflicts with
   * the single event if any of the occurrences of the recurring event overlaps with the single.
   *
   * @param other the single event
   *
   * @return true if the recurring event conflicts with the single event, false otherwise
   */
  @Override
  boolean isConflictWithSingleEvent(SingleEvent other) {
    for (CalendarEntry entry : calendarEntries) {
      if (isTimeOverLapping(
              entry.startDateTime,
              entry.endDateTime,
              other.startDateTime,
              other.endDateTime)
      ) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if the recurring event conflicts with another recurring event. The recurring event
   * conflicts with the other recurring event if any of the occurrences of the recurring event
   * overlaps with any of the occurrences of the other recurring event.
   *
   * @param other the other recurring event
   *
   * @return true if the recurring event conflicts with the other recurring event, false otherwise
   */
  @Override
  boolean isConflictWithRecurringEvent(RecurringEvent other) {
    for (CalendarEntry entry : calendarEntries) {
      for (CalendarEntry otherEntry : other.calendarEntries) {
        if (isTimeOverLapping(
                entry.startDateTime,
                entry.endDateTime,
                otherEntry.startDateTime,
                otherEntry.endDateTime)
        ) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Checks if there is an event with the given name and start date time and return the event.
   *
   * @param eventName the name of the event.
   * @param startZDT  the start date time of the event.
   *
   * @return returns null if there is no matching event and the event otherwise.
   */
  @Override
  public AbstractMap.SimpleEntry<CalendarEntry, Boolean> isEventStartWith(
          String eventName,
          ZonedDateTime startZDT) {
    for (CalendarEntry entry : calendarEntries) {
      if (entry.name.equals(eventName) && entry.startDateTime.equals(startZDT)) {
        return new AbstractMap.SimpleEntry<>(entry.copy(), true);
      }
    }
    return new AbstractMap.SimpleEntry<>(null, true);
  }

  /**
   * Moves all the events in the calendar by the given number of days.
   *
   * @param offsetDays the number of days to move the event by.
   */
  @Override
  public void addOffsetDays(int offsetDays) {
    startDateTime = startDateTime.plusDays(offsetDays);
    endDateTime = endDateTime.plusDays(offsetDays);
    untilDateTime = untilDateTime.plusDays(offsetDays);

    for (CalendarEntry entry : calendarEntries) {
      entry.addOffsetDays(offsetDays);
    }
  }

  /**
   * Edits the single instance of a recurring event if there is a single occurrence with the given
   * name, start date time, and end date time. Otherwise, edits nothing.
   *
   * @param propertyName  the name of the property to edit.
   * @param propertyValue the new value of the property.
   */
  @Override
  public void editSingleEvent(
          String eventName,
          ZonedDateTime startDateTime,
          ZonedDateTime endDateTime,
          String propertyName,
          String propertyValue) {
    previousState = new RecurringEvent(this);
    for (CalendarEntry entry : calendarEntries) {
      if (entry.matchesSingleOccurrence(eventName, startDateTime, endDateTime) != null) {
        entry.updateSimpleParameters(propertyName, propertyValue);
      }
    }
  }

  /**
   * Returns the details of the recurring event that overlaps with the given time range. Goes
   * through all the occurrences of the recurring event and returns the details of the occurrences
   * that overlap with the given time range.
   *
   * @param startTime the start time of the event
   * @param endTime   the end time of the event
   *
   * @return the details of the recurring event
   */
  @Override
  public List<Map<String,String>> getEventDetails(
          ZonedDateTime startTime,
          ZonedDateTime endTime,
          boolean isRecurring) {
    List<Map<String,String>> result = new LinkedList<>();
    for (CalendarEntry entry : calendarEntries) {
      List<Map<String,String>> events = entry.getEventDetails(startTime, endTime, true);
      if (events != null) {
        result.addAll(events);
      }
    }

    return result;
  }

  /**
   * Returns the details of the recurring event that overlaps with the given time range. Goes
   * through all the occurrences of the recurring event and returns the details of the occurrences.
   *
   * @return the details of the recurring event.
   */
  @Override
  public List<Map<String,String>> getAllEventDetails() {
    List<Map<String,String>> result = new LinkedList<>();
    for (CalendarEntry entry : calendarEntries) {
      result.addAll(entry.getAllEventDetails());
    }
    return result;
  }

  /**
   * Updates the time zone of the recurring event and all its occurrences.
   *
   * @param timeZone the new time zone
   */
  @Override
  public void updateZone(ZoneId timeZone) {
    startDateTime = startDateTime.withZoneSameInstant(timeZone);
    endDateTime = endDateTime.withZoneSameInstant(timeZone);
    untilDateTime = untilDateTime.withZoneSameInstant(timeZone);
    for (CalendarEntry entry : calendarEntries) {
      entry.updateZone(timeZone);
    }
  }


  /**
   * Updates the date time of the calendar entry such that it starts on the target date time in the
   * provided time zone. Also updates the untilDateTime to maintain the same duration between
   * events.
   *
   * @param targetZDT the new date time along with the time zone.
   */
  @Override
  public void updateDateTime(ZonedDateTime targetZDT) {
    final TemporalAmount timeShift = Duration.between(startDateTime, targetZDT);
    final TemporalAmount eventDuration = Duration.between(startDateTime, endDateTime);

    startDateTime = targetZDT;
    endDateTime = targetZDT.plus(eventDuration);

    untilDateTime = untilDateTime.plus(timeShift);

    for (CalendarEntry entry : calendarEntries) {
      entry.updateDateTime(entry.startDateTime.plus(timeShift));
    }
  }

  /**
   * Edits the recurring event by changing the start time, end time, days of the week, frequency, or
   * end time. If the target start time is null, the edit is applied to all occurrences of the
   * recurring event. Otherwise, the edit is applied to the occurrence with the target start time.
   *
   * @param eventName       the name of the event
   * @param targetStartTime the start time of the occurrence to edit
   * @param propertyName    the name of the property to edit
   * @param propertyValue   the new value of the property
   */
  @Override
  public void editRecurringEvent(
          String eventName,
          ZonedDateTime targetStartTime,

          String propertyName, String propertyValue) {
    boolean editAll = (targetStartTime == null);
    previousState = new RecurringEvent(this);

    switch (propertyName) {
      case "startDateTime":
        editStartDateTime(eventName, targetStartTime, propertyValue);
        break;
      case "untilDateTime":
        editUntilDateTime(eventName, targetStartTime, propertyValue);
        break;
      case "weekDays":
        editWeekDays(eventName, targetStartTime, propertyValue);
        break;
      case "frequency":
        editFrequency(eventName, targetStartTime, propertyValue);
        break;
      default:
        updateFollowingEvents(
                findFirstMatchingEvent(eventName, targetStartTime),
                propertyName,
                propertyValue);
        if (editAll) {
          updateSimpleParameters(propertyName, propertyValue);
        }
    }
  }

  /**
   * Matches a single event with the given name, start date time, and end date time. Returns true if
   * it matches the given name, start date time, and end date time. Otherwise, returns null.
   *
   * @param eventName     the name of the event
   * @param startDateTime the start date time of the event
   * @param endDateTime   the end date time of the event
   *
   * @return CalendarEntry if a single event matches false otherwise.
   */
  @Override
  public CalendarEntry matchesSingleOccurrence(
          String eventName,
          ZonedDateTime startDateTime,
          ZonedDateTime endDateTime) {
    for (CalendarEntry entry : calendarEntries) {
      if (entry.name.equals(eventName)
              && entry.startDateTime.isEqual(startDateTime)
              && entry.endDateTime.isEqual(endDateTime)) {
        return entry;
      }
    }

    return null;
  }

  /**
   * Reverse the edit by reverting the calendar entry to its previous state.
   */
  @Override
  public void undoEdit() {
    super.undoEdit();
    if (previousState instanceof RecurringEvent) {
      RecurringEvent prevState = (RecurringEvent) previousState;
      this.untilDateTime = prevState.untilDateTime;
      this.daysOfWeek = parseWeekDays(daysOfWeekToString(prevState.daysOfWeek));
      this.frequency = prevState.frequency;
      this.calendarEntries.clear();
      this.calendarEntries.addAll(copyCalendarEntries(prevState.calendarEntries));
      this.previousState = prevState.previousState;
    }
  }

  /**
   * Matches a recurring event with the given name and target date time. Returns the recurring event
   * if it matches the given name and target date time. Otherwise, returns null.
   *
   * @param eventName      the name of the event
   * @param targetDateTime the target date time
   *
   * @return the matching recurring event, or null if no match is found
   */
  @Override
  public CalendarEntry matchesRecurringEvent(String eventName, ZonedDateTime targetDateTime) {
    if (targetDateTime == null) {
      if (name.equals(eventName)) {
        return this;
      }
    }
    for (CalendarEntry entry : calendarEntries) {
      if (entry.name.equals(eventName) && (!entry.startDateTime.isBefore(targetDateTime))) {
        return this;
      }
    }

    return null;
  }

  /**
   * Returns a copy of the recurring event.
   *
   * @return a copy of the recurring event
   */
  @Override
  public CalendarEntry copy() {
    return new RecurringEvent(this);
  }

  /**
   * Searched for all the occurrences and returns the instances that overlap with the given time
   * range.
   *
   * @param startZDT the start date of the time range
   * @param endZDT   the end date of the time range
   *
   * @return the list of occurrences that overlap with the given time range.
   */
  @Override
  public List<CalendarEntry> getEventsIfWithinRange(ZonedDateTime startZDT, ZonedDateTime endZDT) {
    List<CalendarEntry> result = new LinkedList<>();
    for (CalendarEntry entry : calendarEntries) {
      if (isTimeOverLapping(entry.startDateTime, entry.endDateTime, startZDT, endZDT)) {
        result.add(entry.copy());
      }
    }
    return result;
  }

  /**
   * Find the first matching event in the child calendar entries. If targetStartTime is null, return
   * the first matching event. Otherwise, return the matching event with the target start time.
   *
   * @param eventName       the name of the event.
   * @param targetStartTime the target start time of the event.
   *
   * @return index of the first matching event.
   */
  private int findFirstMatchingEvent(String eventName, ZonedDateTime targetStartTime) {
    if (targetStartTime == null) {
      if (this.name.equals(eventName)) {
        return 0;
      }
    }
    for (int i = 0; i < calendarEntries.size(); i++) {
      ZonedDateTime entryTime = calendarEntries.get(i).startDateTime;
      String entryName = calendarEntries.get(i).name;
      if (entryName.equals(eventName)
              && (targetStartTime == null || !entryTime.isBefore(targetStartTime))) {
        return i;
      }
    }

    return -1;
  }

  /**
   * Update the simple parameters of the following events starting from an index.
   *
   * @param startIndex    the first index of event that needs to be updated.
   * @param propertyName  the name of the property that needs to be updated.
   * @param propertyValue the value of the property that needs to be updated.
   */
  private void updateFollowingEvents(int startIndex, String propertyName, String propertyValue) {
    if (startIndex == -1) {
      throw new IllegalArgumentException("No such calendar entry found");
    }

    for (int i = startIndex; i < calendarEntries.size(); i++) {
      CalendarEntry event = calendarEntries.get(i);
      if (propertyName.equals("endDateTime")) {
        ZonedDateTime endDateTime = event.endDateTime;
        ZonedDateTime newTime = stringToZdt(propertyValue, endDateTime.getZone().toString());
        newTime = endDateTime.withHour(newTime.getHour()).withMinute(newTime.getMinute());
        propertyValue = zdtToString(newTime, true);
      }

      event.updateSimpleParameters(propertyName, propertyValue);
    }
  }

  /**
   * Generate occurrences of the recurring event based on the startDateTime, endDateTime,
   * newUntilDateTime, daysOfWeek, and the template event.
   *
   * @param startDateTime    start date time of the event.
   * @param endDateTime      end date time of the event.
   * @param newUntilDateTime new end date time of the event.
   * @param daysOfWeek       Days of the week the event occurs on.
   * @param templateEvent    the template event.
   *
   * @return a list of occurrences of the event.
   */
  private List<CalendarEntry> generateOccurrences(
          ZonedDateTime startDateTime,
          ZonedDateTime endDateTime,
          ZonedDateTime newUntilDateTime,
          List<DayOfWeek> daysOfWeek,
          CalendarEntry templateEvent) {
    List<CalendarEntry> allOccurrences = new ArrayList<>();
    ZonedDateTime occurrenceStartDT = startDateTime;

    while (occurrenceStartDT.isBefore(newUntilDateTime)) {
      if (daysOfWeek.contains(occurrenceStartDT.getDayOfWeek())) {
        ZonedDateTime occurrenceEndDT = occurrenceStartDT
                .withHour(endDateTime.getHour())
                .withMinute(endDateTime.getMinute());

        allOccurrences.add(new SingleEvent(
                templateEvent.name,
                occurrenceStartDT,
                occurrenceEndDT,
                templateEvent.description,
                templateEvent.location,
                templateEvent.isPrivate,
                templateEvent.isAllDay));
      }

      occurrenceStartDT = occurrenceStartDT.plusDays(1);
    }

    return allOccurrences;
  }

  /**
   * Generate occurrences of the recurring event based on the startDateTime, endDateTime,
   * newFrequency, daysOfWeek, and the template event.
   *
   * @param startDateTime start date time of the event.
   * @param endDateTime   end date time of the event.
   * @param newFrequency  frequency of the event.
   * @param daysOfWeek    days of the week the event occurs on.
   * @param templateEvent template event.
   *
   * @return a list of occurrences of the event.
   */
  private List<CalendarEntry> generateOccurrencesByFrequency(
          ZonedDateTime startDateTime,
          ZonedDateTime endDateTime,
          int newFrequency,
          List<DayOfWeek> daysOfWeek,
          CalendarEntry templateEvent) {
    ZonedDateTime newUntilDateTime = calculateUntilDateTime(
            startDateTime,
            newFrequency,
            daysOfWeek);

    return generateOccurrences(
            startDateTime,
            endDateTime,
            newUntilDateTime,
            daysOfWeek,
            templateEvent);
  }


  /**
   * Delete all the event with the given name and target start time.
   *
   * @param eventName       name of the event that needs to be deleted
   * @param targetStartTime target start time of the event that needs to be deleted
   */
  private CalendarEntry deleteFromEvent(String eventName, ZonedDateTime targetStartTime) {
    int index = findFirstMatchingEvent(eventName, targetStartTime);
    if (index == -1) {
      throw new IllegalArgumentException("No such calendar entry found");
    }
    CalendarEntry event = (targetStartTime == null) ? this : calendarEntries.get(index);
    calendarEntries.subList(index, calendarEntries.size()).clear();

    return event;
  }

  private void generateOccurrencesAndAddToCalendarEntries(
          ZonedDateTime startDateTime,
          ZonedDateTime endDateTime,
          ZonedDateTime untilDateTime,
          List<DayOfWeek> daysOfWeek,
          CalendarEntry templateEvent) {
    List<CalendarEntry> newOccurrences = generateOccurrences(
            startDateTime,
            endDateTime,
            untilDateTime,
            daysOfWeek,
            templateEvent
    );

    calendarEntries.addAll(newOccurrences);
  }

  /**
   * Edit the start date time of the event based on the new start date time.
   *
   * @param eventName       name of the event that needs to be updated.
   * @param targetStartTime target start time of the event.
   * @param propertyValue   value of the startDateTime that needs to be updated.
   */
  private void editStartDateTime(
          String eventName,
          ZonedDateTime targetStartTime,
          String propertyValue) {
    CalendarEntry deletedEvent = deleteFromEvent(eventName, targetStartTime);
    if (deletedEvent == null) {
      return;
    }
    ZonedDateTime newStartDateTime = stringToZdt(
            propertyValue,
            startDateTime.getZone().toString()
    );
    ZonedDateTime newEndDateTime = newStartDateTime
            .withHour(deletedEvent.endDateTime.getHour())
            .withMinute(deletedEvent.endDateTime.getMinute());

    generateOccurrencesAndAddToCalendarEntries(
            newStartDateTime,
            newEndDateTime,
            untilDateTime,
            daysOfWeek,
            deletedEvent
    );
  }

  /**
   * Edit the until date time of the event based on the new until date time.
   *
   * @param eventName       name of the event that needs to be updated.
   * @param targetStartTime the target start time of the event.
   * @param propertyValue   the value of the endDateTime that needs to be updated.
   */
  private void editUntilDateTime(
          String eventName,
          ZonedDateTime targetStartTime,
          String propertyValue) {
    CalendarEntry deletedEvent = deleteFromEvent(eventName, targetStartTime);
    if (deletedEvent == null) {
      return;
    }

    ZonedDateTime newUntilDateTime = stringToZdt(propertyValue, untilDateTime.getZone().toString());
    generateOccurrencesAndAddToCalendarEntries(
            deletedEvent.startDateTime,
            deletedEvent.endDateTime,
            newUntilDateTime,
            daysOfWeek,
            deletedEvent
    );
    untilDateTime = newUntilDateTime;
  }

  /**
   * Edit the days of the week of the event based on the new days of the week.
   *
   * @param eventName       name of the event that needs to be updated.
   * @param targetStartTime the target start time of the event.
   * @param propertyValue   the value of the weekDays that needs to be updated.
   */
  private void editWeekDays(String eventName, ZonedDateTime targetStartTime, String propertyValue) {
    CalendarEntry deletedEvent = deleteFromEvent(eventName, targetStartTime);
    if (deletedEvent == null) {
      return;
    }
    List<DayOfWeek> newDaysOfWeek = parseWeekDays(propertyValue);
    if (calendarEntries.isEmpty()) {
      daysOfWeek = newDaysOfWeek;
    }

    generateOccurrencesAndAddToCalendarEntries(
            deletedEvent.startDateTime,
            deletedEvent.endDateTime,
            untilDateTime,
            newDaysOfWeek,
            deletedEvent
    );
  }

  /**
   * Edit the frequency of the event based on the new frequency.
   *
   * @param eventName       name of the event that needs to be updated.
   * @param targetStartTime the target start time of the event.
   * @param propertyValue   the value of the weekDays that needs to be updated.
   */
  private void editFrequency(
          String eventName,
          ZonedDateTime targetStartTime,
          String propertyValue) {
    CalendarEntry deletedEvent = deleteFromEvent(eventName, targetStartTime);
    if (deletedEvent == null) {
      return;
    }

    List<CalendarEntry> newOccurrences = generateOccurrencesByFrequency(
            deletedEvent.startDateTime,
            deletedEvent.endDateTime,
            Integer.parseInt(propertyValue),
            daysOfWeek,
            deletedEvent);

    calendarEntries.addAll(newOccurrences);
    frequency = calendarEntries.size();
  }

  /**
   * Copies the list of calendar entries.
   *
   * @param originalEntries the original list of calendar entries.
   *
   * @return the copied list of calendar entries.
   */
  private List<CalendarEntry> copyCalendarEntries(List<CalendarEntry> originalEntries) {
    List<CalendarEntry> copiedEntries = new LinkedList<>();
    for (CalendarEntry entry : originalEntries) {
      copiedEntries.add(entry.copy());
    }
    return copiedEntries;
  }
}
