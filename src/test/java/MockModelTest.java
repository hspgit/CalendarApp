import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import controller.BasicCalendarController;
import controller.CalendarController;
import model.CalendarModel;
import model.SingleCalendar;
import model.calendarentry.CalendarEntry;
import view.BasicCalendarView;
import view.CalendarView;

import static org.junit.Assert.assertEquals;

/**
 * This class tests Controller with a mock model.
 */
public class MockModelTest {

  /**
   * This class is a mock model for testing the controller. It logs all the method calls and returns
   * the given strings.
   */
  public static class MockModel implements CalendarModel {
    private final StringBuilder log;

    /**
     * Constructor for the mock model.
     *
     * @param log the log to append the method calls
     */
    public MockModel(StringBuilder log) {
      this.log = log;
    }

    /**
     * Mock method to add a calendar.
     *
     * @param calendarName the name of the calendar
     * @param timeZone     the time zone of the calendar
     */
    @Override
    public void addCalendar(String calendarName, String timeZone) {
      log.append("addCalendar: ").append(calendarName).append(", ").append(timeZone);
    }

    /**
     * Mock method to edit a calendar.
     *
     * @param calendarName the name of the calendar
     * @param property     the property to edit
     * @param value        the value to set
     */
    @Override
    public void editCalendar(String calendarName, String property, String value) {
      log.append("changeTimezone: ").append(calendarName).append(", ").append(property)
              .append(", ").append(value);
    }

    /**
     * Mock method to use a calendar.
     *
     * @param calendarName the name of the calendar
     */
    @Override
    public void useCalendar(String calendarName) {
      log.append("useCalendar: ").append(calendarName);
    }

    /**
     * Mock method to copy a single event.
     *
     * @param eventName          the name of the event
     * @param targetCalendarName the name of the target calendar
     * @param sourceDateTime     the date and time of the event
     * @param targetDateTime     the date and time to copy the event to
     */
    @Override
    public void copySingleEvent(String eventName, String targetCalendarName, String sourceDateTime,
                                String targetDateTime, boolean autoDecline) {
      log.append("copySingleEvent: ").append(eventName).append(", ").append(targetCalendarName)
              .append(", ").append(sourceDateTime).append(", ").append(targetDateTime)
              .append(", ").append(autoDecline);
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
      log.append("copyMultipleEventsRange: ").append(targetCalendarName).append(", ")
              .append(rangeStartDate).append(", ").append(rangeEndDate).append(", ")
              .append(targetDate).append(", ").append(autoDecline);

    }

    /**
     * Copies all events from the current calendar to the target calendar that fall within the given
     * date range. If autoDecline is true, any conflicts caused by the new copies will result in the
     * copy action to fail, resulting in no new events being created.
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
      log.append("copyMultipleEventsOnDay: ").append(targetCalendarName).append(", ")
              .append(rangeStartDate).append(", ").append(targetDate).append(", ")
              .append(autoDecline);

    }

    /**
     * Mock method to get the current calendar.
     *
     * @return the current calendar
     */
    @Override
    public SingleCalendar getCurrentCalendar() {
      log.append("getCurrentCalendar: ");
      return new MockSingleCalendar(log);
    }

    /**
     * Gets the list of all calendars in the system. Name, time zone pairs are returned in a
     * SimpleEntry.
     *
     * @return the list of all calendars.
     */
    @Override
    public Map<String, String> getAllCalendars() {
      log.append("getAllCalendars: ");
      return Map.of();
    }

    /**
     * Gets the name of the current calendar.
     *
     * @return the name of the current calendar.
     */
    @Override
    public String getCurrentCalendarName() {
      log.append("getCurrentCalendarName: ");
      return "";
    }

    /**
     * This class is a mock single calendar for testing the controller. It logs all the method
     * calls.
     */
    public static class MockSingleCalendar extends SingleCalendar {

      StringBuilder log;

      /**
       * Constructor for the mock single calendar.
       *
       * @param log the log to append the method calls
       */
      public MockSingleCalendar(StringBuilder log) {
        this.log = log;
      }

      /**
       * Adds a single event to the calendar. Mock method to add a single event.
       *
       * @param name          the name of the event.
       * @param startDateTime the start date and time of the event.
       * @param endDateTime   the end date and time of the event.
       * @param description   the description of the event.
       * @param location      the location of the event.
       * @param autoDecline   whether the event should be automatically declined.
       * @param isPrivate     whether the event is private.
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
        log.append("addSingleEvent: ").append(name).append(", ").append(startDateTime)
                .append(", ").append(endDateTime).append(", ").append(description)
                .append(", ").append(location).append(", ").append(autoDecline)
                .append(", ").append(isPrivate);
      }

      /**
       * Adds a single event to the calendar.
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
        log.append("addSingleEventAllDay: ").append(name).append(", ").append(startDate)
                .append(", ").append(description).append(", ").append(location)
                .append(", ").append(autoDecline).append(", ").append(isPrivate);

      }

      /**
       * Adds a recurring event to the calendar.
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
        log.append("addRecurringEventUntil: ").append(name).append(", ").append(startDateTime)
                .append(", ").append(endDateTime).append(", ").append(description)
                .append(", ").append(location).append(", ").append(weekDays)
                .append(", ").append(untilDateTime).append(", ").append(autoDecline)
                .append(", ").append(isPrivate);

      }

      /**
       * Adds a recurring event to the calendar.
       *
       * @param name          the name of the event.
       * @param startDateTime the start date and time of the event.
       * @param endDateTime   the end date and time of the event.
       * @param description   the description of the event.
       * @param location      the location of the event.
       * @param weekDays      the days of the week the event occurs on.
       * @param frequency     the frequency of the event.
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
        log.append("addRecurringEventFrequency: ").append(name).append(", ").append(startDateTime)
                .append(", ").append(endDateTime).append(", ").append(description)
                .append(", ").append(location).append(", ").append(weekDays)
                .append(", ").append(frequency).append(", ").append(autoDecline)
                .append(", ").append(isPrivate);

      }

      /**
       * Adds a recurring event to the calendar.
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
        log.append("addRecurringAllDayEventUntil: ").append(name).append(", ").append(startDate)
                .append(", ").append(description).append(", ").append(location)
                .append(", ").append(weekDays).append(", ").append(untilDateTime)
                .append(", ").append(autoDecline).append(", ").append(isPrivate);

      }

      /**
       * Adds a recurring event to the calendar.
       *
       * @param name        the name of the event.
       * @param startDate   the start date and time of the event.
       * @param description the description of the event.
       * @param location    the location of the event.
       * @param weekDays    the days of the week the event occurs on.
       * @param frequency   the frequency of the event.
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
        log.append("addRecurringAllDayEventFrequency: ").append(name).append(", ").append(startDate)
                .append(", ").append(description).append(", ").append(location)
                .append(", ").append(weekDays).append(", ").append(frequency)
                .append(", ").append(autoDecline).append(", ").append(isPrivate);

      }

      /**
       * Edits a single event in the calendar. Mock method to edit a single event.
       *
       * @param eventName     the name of the event to edit.
       * @param startDateTime the start date and time of the event.
       * @param endDateTime   the end date and time of the event.
       * @param propertyName  the property to edit.
       * @param propertyValue the new value of the property.
       * @param autoDecline   whether to auto decline
       */
      @Override
      public void editSingleEvent(
              String eventName,
              String startDateTime,
              String endDateTime,
              String propertyName,
              String propertyValue,
              boolean autoDecline) {
        log.append("editSingleEvent: ").append(eventName).append(", ").append(startDateTime)
                .append(", ").append(endDateTime).append(", ").append(propertyName)
                .append(", ").append(propertyValue).append(", ").append(autoDecline);

      }

      /**
       * Edits multiple events in the calendar.
       *
       * @param eventName     the name of the event to edit.
       * @param startDateTime the start date and time of the event.
       * @param propertyName  the property to edit.
       * @param propertyValue the new value of the property.
       * @param autoDecline   whether to auto decline
       */
      @Override
      public void editMultipleEventsFollowing(
              String eventName,
              String startDateTime,
              String propertyName,
              String propertyValue,
              boolean autoDecline) {
        log.append("editMultipleEventsFollowing: ").append(eventName).append(", ")
                .append(startDateTime).append(", ").append(propertyName).append(", ")
                .append(propertyValue).append(", ").append(autoDecline);

      }

      /**
       * Edits multiple events in the calendar.
       *
       * @param eventName     the name of the event to edit.
       * @param propertyName  the property to edit.
       * @param propertyValue the new value of the property.
       * @param autoDecline   whether to auto decline if there is a conflict
       */
      @Override
      public void editMultipleEventsAll(
              String eventName,
              String propertyName,
              String propertyValue,
              boolean autoDecline) {
        log.append("editMultipleEventsAll: ").append(eventName).append(", ").append(propertyName)
                .append(", ").append(propertyValue).append(", ").append(autoDecline);

      }

      /**
       * Gets the events in the calendar between the given start and end date times. Returns Name,
       * StartDateTime, EndDateTime, Location. If endDateTime is empty, it will return all events on
       * the given start day.
       *
       * @param startDateTime the start date and time.
       * @param endDateTime   the end date and time.
       *
       * @return the events in the calendar between the given start and end date times.
       */
      @Override
      public Map<String, String>[] getEventsRange(String startDateTime, String endDateTime) {
        log.append("getEventsRange: ").append(startDateTime).append(", ").append(endDateTime);
        Map<String, String> mockEvent = Map.of(
                "Name", "Mock Event",
                "StartDateTime", startDateTime,
                "EndDateTime", endDateTime,
                "Location", "Mock Location");
        return new Map[]{mockEvent};
      }

      /**
       * Gets the events in the calendar between the given start and end date times. Returns Name,
       * StartDateTime, EndDateTime, Location. If endDateTime is empty, it will return all events on
       * the given start day.
       *
       * @param startDate the start date and time.
       *
       * @return the events in the calendar between the given start and end date times.
       */
      @Override
      public Map<String, String>[] getEventsOnDate(String startDate) {
        log.append("getEventsOnDate: ").append(startDate);
        Map<String, String> mockEvent = Map.of(
                "Name", "Mock Event",
                "StartDateTime", startDate,
                "EndDateTime", startDate,
                "Location", "Mock Location");
        return new Map[]{mockEvent};
      }

      /**
       * Exports the calendar to a file. Mock method to export the calendar to a file.
       *
       * @return The absolute path of the file.
       */
      @Override
      public Map<String, String>[] getAllEvents() {
        log.append("getAllEvents: ");
        Map<String, String> mockEvent = Map.of(
                "Name", "Mock Event",
                "StartDateTime", "2020-11-10T11:00",
                "EndDateTime", "2020-11-10T12:00",
                "Location", "Mock Location");
        return new Map[]{mockEvent};
      }

      /**
       * Shows the availability status of the calendar. Mock method to show the availability status
       * of the calendar.
       *
       * @param startDateTime the start date and time.
       *
       * @return the availability status of the calendar for the given start date and time.
       */
      @Override
      public String getStatusOnDateTime(String startDateTime) {
        log.append("getStatusOnDateTime: ").append(startDateTime);
        return "";
      }

      /**
       * Mock method to get the status of the calendar in the given range. Shows the availability
       * status of the calendar within the given range.
       *
       * @param rangeStartDateTime the start date and time of the range.
       * @param rangeEndDateTime   the end date and time of the range.
       *
       * @return ""
       */
      @Override
      public String getStatusInRange(String rangeStartDateTime, String rangeEndDateTime) {
        log.append("getStatusInRange: ").append(rangeStartDateTime).append(", ")
                .append(rangeEndDateTime);
        return "";

      }

      /**
       * Mock method to change the timezone.
       *
       * @param value the value to set
       */
      @Override
      public void changeTimezone(String value) {
        log.append("changeTimezone: ").append(value);

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
      protected List<CalendarEntry> getCalendarEntriesWithinRange(
              String startDate,
              String endDate) {
        log.append("getCalendarEntriesWithinRange: ")
                .append(startDate).append(", ").append(endDate);
        return List.of();
      }


      /**
       * Gets the events in the calendar on the specific day. Returns list of events that are within
       * the given range.
       *
       * @param startDate The Start date of the range.
       *
       * @return the list of events that come within the given day.
       */
      @Override
      public List<CalendarEntry> getCalendarEntriesOnDay(String startDate) {
        log.append("getCalendarEntriesOnDay: ").append(startDate);
        return List.of();
      }

      /**
       * Mock method to get the event with the start date time.
       *
       * @param eventName     the name of the event
       * @param startDateTime the start date time
       *
       * @return the event with the start date time
       */
      @Override
      public CalendarEntry getEventWithStartDateTime(String eventName, String startDateTime) {
        log.append("getEventWithStartDateTime: ").append(eventName).append(", ")
                .append(startDateTime);
        return null;
      }

      /**
       * Mock method to add events.
       *
       * @param event       the list of events
       * @param autoDecline whether to auto decline
       */
      @Override
      public void addEvents(List<CalendarEntry> event, boolean autoDecline) {
        log.append("addEvents: ").append(event).append(", ").append(autoDecline);
      }

      /**
       * Mock method to get the event with the exact date time.
       *
       * @param eventName     the name of the event
       * @param startDateTime the start date time
       *
       * @return the event with the exact date time
       */
      @Override
      public Map<String, String> getExactEvent(
              String eventName,
              String startDateTime) {

        log.append("getExactEvent: ").append(eventName).append(", ").append(startDateTime);
        return Map.of();
      }
    }
  }

  private static final String ANSI_GREEN_MESSAGE = "\u001B[32m";
  private static final String ANSI_BLUE_DATA = "\u001B[34m";
  private static final String ANSI_RESET_USER_INPUT = "\u001B[0m";
  private static final String ANSI_RED_ERROR = "\u001B[31m";

  @Test
  public void testCreateCalendar() {
    InputStream in = new ByteArrayInputStream((
            "create calendar --name \"Calendar name\" --timezone America/New_York"
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals("addCalendar: Calendar name, America/New_York", log.toString());
  }

  @Test
  public void testCreateCalendarSingle() {
    InputStream in = new ByteArrayInputStream((
            "create calendar --name New --timezone America/New_York"
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals("addCalendar: New, America/New_York", log.toString());
  }

  @Test
  public void testEditCalendar() {
    InputStream in = new ByteArrayInputStream((
            "edit calendar --name \"Calendar name\" --property name \"New Calendar name\""
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals("changeTimezone: Calendar name, name, New Calendar name", log.toString());
  }

  @Test
  public void testEditCalendarWithSingleWord() {
    InputStream in = new ByteArrayInputStream((
            "edit calendar --name CalendarName --property timezone NewPropValue"
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals("changeTimezone: CalendarName, timezone, NewPropValue", log.toString());
  }

  @Test
  public void testEditCalendarInvalidNoProperty() {
    InputStream in = new ByteArrayInputStream((
            "edit calendar --name CalendarName timezone NewPropValue"
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals(ANSI_GREEN_MESSAGE
                    + "You are in interactive mode, please enter your commands"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_RED_ERROR
                    + "ERROR: Invalid command: edit calendar --name CalendarName "
                    + "timezone NewPropValue"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_GREEN_MESSAGE
                    + "Goodbye!" + ANSI_RESET_USER_INPUT + System.lineSeparator(),
            out.toString());
  }

  @Test
  public void testEditCalendarInvalidPropertyType() {
    InputStream in = new ByteArrayInputStream((
            "edit calendar --name CalendarName --property InvalidPropertyType NewPropValue"
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals(ANSI_GREEN_MESSAGE
                    + "You are in interactive mode, please enter your commands"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_RED_ERROR
                    + "ERROR: Invalid property name: InvalidPropertyType"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_GREEN_MESSAGE
                    + "Goodbye!" + ANSI_RESET_USER_INPUT + System.lineSeparator(),
            out.toString());
  }

  @Test
  public void testUseCalendar() {
    InputStream in = new ByteArrayInputStream((
            "use calendar --name CalendarName"
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals("useCalendar: CalendarName", log.toString());
  }

  @Test
  public void testUseCalendarInvalidLength() {
    InputStream in = new ByteArrayInputStream((
            "use calendar"
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals(ANSI_GREEN_MESSAGE
                    + "You are in interactive mode, please enter your commands"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_RED_ERROR
                    + "ERROR: Invalid input length: use calendar"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_GREEN_MESSAGE
                    + "Goodbye!" + ANSI_RESET_USER_INPUT + System.lineSeparator(),
            out.toString());
  }

  @Test
  public void testUseCalendarInvalidUseType() {
    InputStream in = new ByteArrayInputStream((
            "use inValidUseType --name CalendarName"
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals(ANSI_GREEN_MESSAGE
                    + "You are in interactive mode, please enter your commands"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_RED_ERROR
                    + "ERROR: Invalid use type: inValidUseType"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_GREEN_MESSAGE
                    + "Goodbye!" + ANSI_RESET_USER_INPUT + System.lineSeparator(),
            out.toString());
  }

  @Test
  public void testUseCalendarMultiWord() {
    InputStream in = new ByteArrayInputStream((
            "use calendar --name \"Calendar Name\""
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals("useCalendar: Calendar Name", log.toString());
  }

  @Test
  public void testCopySingleEvent() {
    InputStream in = new ByteArrayInputStream((
            "copy event \"Event Name\" --target \"Target Calendar\" on 2020-11-10T11:00 "
                    + "to 2020-10-10T11:00"
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals("copySingleEvent: Event Name, Target Calendar, 2020-11-10T11:00,"
                    + " 2020-10-10T11:00, true",
            log.toString());
  }

  @Test
  public void testCopySingleEventInvalidCopyType() {
    InputStream in = new ByteArrayInputStream((
            "copy inValid \"Event Name\" --target \"Target Calendar\" on 2020-11-10T11:00 "
                    + "to 2020-10-10T11:00"
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals(ANSI_GREEN_MESSAGE
                    + "You are in interactive mode, please enter your commands"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_RED_ERROR
                    + "ERROR: Invalid copy type: inValid"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_GREEN_MESSAGE
                    + "Goodbye!" + ANSI_RESET_USER_INPUT + System.lineSeparator(),
            out.toString());
  }

  @Test
  public void testCopySingleEventInvalidOnDTS() {
    InputStream in = new ByteArrayInputStream((
            "copy event \"Event Name\" --target \"Target Calendar\" on 2020-19-10T11:00 "
                    + "to 2020-10-10T11:00"
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals(ANSI_GREEN_MESSAGE
                    + "You are in interactive mode, please enter your commands"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_RED_ERROR
                    + "ERROR: Invalid on date time string: 2020-19-10T11:00"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_GREEN_MESSAGE
                    + "Goodbye!" + ANSI_RESET_USER_INPUT + System.lineSeparator(),
            out.toString());
  }

  @Test
  public void testCopySingleEventInvalidToDTS() {
    InputStream in = new ByteArrayInputStream((
            "copy event \"Event Name\" --target \"Target Calendar\" on 2020-10-10T11:00 "
                    + "to 2020-10-100T11:00"
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals(ANSI_GREEN_MESSAGE
                    + "You are in interactive mode, please enter your commands"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_RED_ERROR
                    + "ERROR: Invalid to date time string: 2020-10-100T11:00"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_GREEN_MESSAGE
                    + "Goodbye!" + ANSI_RESET_USER_INPUT + System.lineSeparator(),
            out.toString());
  }

  @Test
  public void testCopyEventsOn() {
    InputStream in = new ByteArrayInputStream((
            "copy events on 2020-11-10 --target \"Target Calendar\" to 2020-12-10"
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals("copyMultipleEventsOnDay: Target Calendar, 2020-11-10, 2020-12-10, true",
            log.toString());
  }

  @Test
  public void testCopyEventsInvalid() {
    InputStream in = new ByteArrayInputStream((
            "copy events on <dateString> --target <calendarName> to "
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals(ANSI_GREEN_MESSAGE
                    + "You are in interactive mode, please enter your commands"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_RED_ERROR
                    + "ERROR: Invalid input length: copy events "
                    + "on <dateString> --target <calendarName> to"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_GREEN_MESSAGE
                    + "Goodbye!" + ANSI_RESET_USER_INPUT + System.lineSeparator(),
            out.toString());
  }

  @Test
  public void testCopyEventsInvalidOnDTS() {
    InputStream in = new ByteArrayInputStream((
            "copy events on 2025-10-100 --target <calendarName> to 2025-10-10"
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals(ANSI_GREEN_MESSAGE
                    + "You are in interactive mode, please enter your commands"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_RED_ERROR
                    + "ERROR: Invalid on date string: 2025-10-100"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_GREEN_MESSAGE
                    + "Goodbye!" + ANSI_RESET_USER_INPUT + System.lineSeparator(),
            out.toString());
  }

  @Test
  public void testCopyEventsInvalidToDTS() {
    InputStream in = new ByteArrayInputStream((
            "copy events on 2025-10-10 --target <calendarName> to 20205-10-10"
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals(ANSI_GREEN_MESSAGE
                    + "You are in interactive mode, please enter your commands"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_RED_ERROR
                    + "ERROR: Invalid target date string: 20205-10-10"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_GREEN_MESSAGE
                    + "Goodbye!" + ANSI_RESET_USER_INPUT + System.lineSeparator(),
            out.toString());
  }

  @Test
  public void testCopyEventsExact8() {
    InputStream in = new ByteArrayInputStream((
            "copy events on 2020-11-10 --target New to 2020-12-10"
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals("copyMultipleEventsOnDay: New, 2020-11-10, 2020-12-10, true",
            log.toString());
  }

  @Test
  public void testCopyEventsBetween() {
    InputStream in = new ByteArrayInputStream((
            "copy events between 2020-11-10 and 2020-11-20 --target \"Target Calendar\" "
                    + "to 2020-12-10"
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals("copyMultipleEventsRange: Target Calendar, 2020-11-10, 2020-11-20"
                    + ", 2020-12-10, true",
            log.toString());
  }

  @Test
  public void testCopyEventsBetweenInvalidRangeStart() {
    InputStream in = new ByteArrayInputStream((
            "copy events between 2020-111-10 and 2020-11-20 --target \"Target Calendar\" "
                    + "to 2020-12-10"
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals(ANSI_GREEN_MESSAGE
                    + "You are in interactive mode, please enter your commands"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_RED_ERROR
                    + "ERROR: Invalid start date string: 2020-111-10"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_GREEN_MESSAGE
                    + "Goodbye!" + ANSI_RESET_USER_INPUT + System.lineSeparator(),
            out.toString());
  }

  @Test
  public void testCopyEventsBetweenInvalidRangeEnd() {
    InputStream in = new ByteArrayInputStream((
            "copy events between 2020-11-10 and 2020-11-200 --target \"Target Calendar\" "
                    + "to 2020-12-10"
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals(ANSI_GREEN_MESSAGE
                    + "You are in interactive mode, please enter your commands"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_RED_ERROR
                    + "ERROR: Invalid end date string: 2020-11-200"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_GREEN_MESSAGE
                    + "Goodbye!" + ANSI_RESET_USER_INPUT + System.lineSeparator(),
            out.toString());
  }

  // --target is required
  @Test
  public void testCopyEventsBetweenInvalidNoTarget() {
    InputStream in = new ByteArrayInputStream((
            "copy events between 2020-11-10 and 2020-11-200 \"Target Calendar\" "
                    + "to 2020-12-10"
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals(ANSI_GREEN_MESSAGE
                    + "You are in interactive mode, please enter your commands"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_RED_ERROR
                    + "ERROR: Target calendar name is required"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_GREEN_MESSAGE
                    + "Goodbye!" + ANSI_RESET_USER_INPUT + System.lineSeparator(),
            out.toString());
  }

  @Test
  public void testAddSingleEvent() {
    InputStream in = new ByteArrayInputStream(("create event \"eventN with space\" "
            + "from 2021-03-01T12:00 "
            + "to 2021-03-01T13:00 location \"my home\" description \"for testing\" "
            + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals("getCurrentCalendar: addSingleEvent: eventN with space, "
                    + "2021-03-01T12:00, 2021-03-01T13:00, for testing, my home, true, true",
            log.toString());
  }

  @Test
  public void testAddRecurringEvent() {
    InputStream in = new ByteArrayInputStream(("create event --autoDecline EvName "
            + "from 2021-03-01T12:00 "
            + "to 2021-03-01T13:00 location \"my home\" description \"for testing\" "
            + "repeats MTWRFSU for 5 times"
            + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals("getCurrentCalendar: addRecurringEventFrequency: EvName, 2021-03-01T12:00, "
                    + "2021-03-01T13:00, for testing, my home, MTWRFSU, 5, true, true",
            log.toString());
  }

  @Test
  public void testEditSingleEvent() {
    InputStream in = new ByteArrayInputStream(("edit event location \"some evName\" "
            + "from 2021-03-01T12:00 "
            + "to 2021-03-02T12:00 with \"new loc\""
            + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals("getCurrentCalendar: editSingleEvent: some evName, 2021-03-01T12:00, "
            + "2021-03-02T12:00, location, new loc, true", log.toString());
  }

  @Test
  public void testEditMultipleEvents() {
    InputStream in =
            new ByteArrayInputStream(("edit events location \"some evName\" from 2021-03-01T12:00 "
                    + "with \"new loc\""
                    + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals("getCurrentCalendar: editMultipleEventsFollowing: some evName,"
            + " 2021-03-01T12:00,"
            + " location, new loc, true", log.toString());
  }

  @Test
  public void testGetEvents() {
    InputStream in = new ByteArrayInputStream(("print events on 2021-03-01"
            + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals("getCurrentCalendar: getEventsOnDate: 2021-03-01", log.toString());
  }

  @Test
  public void testGetEventsBetween() {
    InputStream in = new ByteArrayInputStream(("print events from 2021-03-01T00:00 to "
            + "2021-03-02T23:59"
            + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals("getCurrentCalendar: getEventsRange: 2021-03-01T00:00, 2021-03-02T23:59",
            log.toString());
  }

  @Test
  public void testGetAllEvents() {
    InputStream in = new ByteArrayInputStream(("export cal fileName.csv"
            + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals("getCurrentCalendar: getAllEvents: ", log.toString());
  }

  @Test
  public void testGetAvailabilityStatus() {
    InputStream in = new ByteArrayInputStream(("show status on 2021-03-01T12:00"
            + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals("getCurrentCalendar: getStatusOnDateTime: 2021-03-01T12:00",
            log.toString());
  }

  @Test
  public void testEditInvalid() {
    InputStream in = new ByteArrayInputStream(("edit events location \"some evName\" from "
            + "2021-03-01T12:00 "
            + "to 2021-03-02T12:99 with \"new loc\""
            + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals(
            ANSI_GREEN_MESSAGE
                    + "You are in interactive mode, please enter your commands"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_RED_ERROR
                    + "ERROR: Invalid to date time: 2021-03-02T12:99"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_GREEN_MESSAGE
                    + "Goodbye!" + ANSI_RESET_USER_INPUT + System.lineSeparator(),
            out.toString());
  }

  @Test
  public void testCreateEventAddSingleAllDay() {
    InputStream in = new ByteArrayInputStream(("create event  EvName "
            + "from 2021-03-01 "
            + "location \"my home\" description \"for testing\" "
            + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals("getCurrentCalendar: addSingleEventAllDay: EvName, 2021-03-01, "
            + "for testing, my home, true, true", log.toString());
  }

  @Test
  public void testCreateEventAddRecAllDayUntil() {
    InputStream in = new ByteArrayInputStream(("create event EvName "
            + "from 2021-03-01 "
            + "location \"my home\" description \"for testing\" "
            + "repeats MTWRFSU until 2021-03-10T00:00"
            + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals("getCurrentCalendar: addRecurringAllDayEventUntil: EvName, "
                    + "2021-03-01, for testing, my home, MTWRFSU, 2021-03-10T00:00, true, true",
            log.toString());
  }

  @Test
  public void testCreateEventAddRecAllDayFreq() {
    InputStream in = new ByteArrayInputStream(("create event EvName "
            + "from 2021-03-01 "
            + "location \"my home\" description \"for testing\" "
            + "repeats MTWRFSU for 5 times"
            + System.lineSeparator() + "exit").getBytes());
    StringBuilder log = new StringBuilder();
    CalendarModel model = new MockModel(log);
    StringBuilder out = new StringBuilder();
    CalendarView view = new BasicCalendarView(out);
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals("getCurrentCalendar: addRecurringAllDayEventFrequency: EvName, "
            + "2021-03-01, for testing, my home, MTWRFSU, 5, true, true", log.toString());
  }

}
