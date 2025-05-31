import org.junit.Test;

import java.util.Map;

import model.BasicCalendarModel;
import model.CalendarModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for BasicCalendarModel.
 */
public class BasicCalendarModelTest {

  @Test
  public void addSingleEventToDefault() {
    CalendarModel model = new BasicCalendarModel();
    model.getCurrentCalendar().addSingleEvent(
            "event1",
            "2020-01-01T00:00",
            "2020-01-01T01:00",
            "event1 description",
            "event1 location",
            true,
            true);
    Map<String, String>[] events = model.getCurrentCalendar().getAllEvents();
    assertEquals(1, events.length);
    Map<String, String> eventDetails = events[0];
    assertEquals("event1", eventDetails.get("Name"));
    assertEquals("01/01/2020", eventDetails.get("StartDate"));
    assertEquals("12:00 AM", eventDetails.get("StartTime"));
    assertEquals("01/01/2020", eventDetails.get("EndDate"));
    assertEquals("01:00 AM", eventDetails.get("EndTime"));
    assertEquals("event1 description", eventDetails.get("Description"));
    assertEquals("event1 location", eventDetails.get("Location"));
    assertEquals("true", eventDetails.get("IsPrivate"));
    assertEquals("false", eventDetails.get("IsAllDay"));
  }

  @Test
  public void testAddCalendar() {
    CalendarModel model = new BasicCalendarModel();
    model.addCalendar("test", "Asia/Singapore");
    model.useCalendar("test");
    model.getCurrentCalendar().addSingleEvent(
            "event1",
            "2020-01-01T00:00",
            "2020-01-01T01:00",
            "event1 description",
            "event1 location",
            true,
            true);
    Map<String, String>[] events = model.getCurrentCalendar().getAllEvents();
    assertEquals(1, events.length);
    Map<String, String> eventDetails = events[0];
    assertEquals("event1", eventDetails.get("Name"));
    assertEquals("01/01/2020", eventDetails.get("StartDate"));
    assertEquals("12:00 AM", eventDetails.get("StartTime"));
    assertEquals("01/01/2020", eventDetails.get("EndDate"));
    assertEquals("01:00 AM", eventDetails.get("EndTime"));
    assertEquals("event1 description", eventDetails.get("Description"));
    assertEquals("event1 location", eventDetails.get("Location"));
    assertEquals("true", eventDetails.get("IsPrivate"));
    assertEquals("false", eventDetails.get("IsAllDay"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddCalendarInvalid() {
    CalendarModel model = new BasicCalendarModel();
    try {
      model.addCalendar("default", "Asia/Singapore");
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Calendar already exists: default", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddCalendarInvalid2() {
    CalendarModel model = new BasicCalendarModel();
    model.addCalendar("default2", "Asia/Singapore");
    try {
      model.addCalendar("default2", "Asia/Singapore");
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Calendar already exists: default2", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddCalendarInvalidNullName() {
    CalendarModel model = new BasicCalendarModel();
    try {
      model.addCalendar(null, "Asia/Singapore");
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Calendar name cannot be empty", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddCalendarInvalidEmptyName() {
    CalendarModel model = new BasicCalendarModel();
    try {
      model.addCalendar("   ", "Asia/Singapore");
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Calendar name cannot be empty", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddCalendarInvalidTimezoneNull() {
    CalendarModel model = new BasicCalendarModel();
    try {
      model.addCalendar("Home", null);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Timezone cannot be empty", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddCalendarInvalidTimezoneEmpty() {
    CalendarModel model = new BasicCalendarModel();
    try {
      model.addCalendar("Home", "   ");
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Timezone cannot be empty", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddCalendarInvalidTimezone() {
    CalendarModel model = new BasicCalendarModel();
    try {
      model.addCalendar("Home", "Invalid");
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid timezone: Invalid", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUseCalendarInvalidDoesNotExist() {
    CalendarModel model = new BasicCalendarModel();
    try {
      model.useCalendar("Home");
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Calendar does not exist: Home", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditTimezoneInvalid() {
    CalendarModel model = new BasicCalendarModel();
    try {
      model.editCalendar("default", "timezone", "Invalid");
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid timezone: Invalid", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditInvalidNameNotExists() {
    CalendarModel model = new BasicCalendarModel();
    try {
      model.editCalendar("DNE", "timezone", "Invalid");
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Calendar does not exist: DNE", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditCalendarInvalidProperty() {
    CalendarModel model = new BasicCalendarModel();
    try {
      model.editCalendar("default", "invalidProperty", "Invalid");
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid property: invalidProperty", e.getMessage());
      throw e;
    }
  }

  @Test
  public void testEditCalendarName() {
    CalendarModel model = new BasicCalendarModel();
    model.addCalendar("OldName", "Asia/Singapore");
    model.useCalendar("OldName");
    model.getCurrentCalendar().addSingleEvent(
            "event1",
            "2020-01-01T00:00",
            "2020-01-01T01:00",
            "event1 description",
            "event1 location",
            true,
            true);
    model.editCalendar("OldName", "name", "NewName");
    model.useCalendar("NewName");
    Map<String, String>[] events = model.getCurrentCalendar().getAllEvents();
    assertEquals(1, events.length);
    Map<String, String> eventDetails = events[0];
    assertEquals("event1", eventDetails.get("Name"));
    assertEquals("01/01/2020", eventDetails.get("StartDate"));
    assertEquals("12:00 AM", eventDetails.get("StartTime"));
    assertEquals("01/01/2020", eventDetails.get("EndDate"));
    assertEquals("01:00 AM", eventDetails.get("EndTime"));
    assertEquals("event1 description", eventDetails.get("Description"));
    assertEquals("event1 location", eventDetails.get("Location"));
    assertEquals("true", eventDetails.get("IsPrivate"));
    assertEquals("false", eventDetails.get("IsAllDay"));
  }

  @Test
  public void testEditCalendarTimezone() {
    CalendarModel model = new BasicCalendarModel();
    model.addCalendar("OldName", "Asia/Singapore");
    model.useCalendar("OldName");
    model.getCurrentCalendar().addSingleEvent(
            "event1",
            "2025-03-20T10:00",
            "2025-03-20T11:00",
            "event1 description",
            "event1 location",
            true,
            true);
    model.editCalendar("OldName", "timezone", "America/New_York");
    Map<String, String>[] events = model.getCurrentCalendar().getAllEvents();
    assertEquals(1, events.length);
    Map<String, String> eventDetails = events[0];
    assertEquals("event1", eventDetails.get("Name"));
    assertEquals("03/19/2025", eventDetails.get("StartDate"));
    assertEquals("10:00 PM", eventDetails.get("StartTime"));
    assertEquals("03/19/2025", eventDetails.get("EndDate"));
    assertEquals("11:00 PM", eventDetails.get("EndTime"));
    assertEquals("event1 description", eventDetails.get("Description"));
    assertEquals("event1 location", eventDetails.get("Location"));
    assertEquals("true", eventDetails.get("IsPrivate"));
    assertEquals("false", eventDetails.get("IsAllDay"));
    // 12-hour time shift
  }

  @Test
  public void testEditCalendarNameThenTimezone() {
    CalendarModel model = new BasicCalendarModel();
    model.addCalendar("OldName", "America/New_York");
    model.useCalendar("OldName");
    model.getCurrentCalendar().addSingleEvent(
            "event1",
            "2025-03-20T10:00",
            "2025-03-20T11:00",
            "event1 description",
            "event1 location",
            true,
            true);
    model.editCalendar("OldName", "name", "NewName");
    model.editCalendar("NewName", "timezone", "Asia/Singapore");
    Map<String, String>[] events = model.getCurrentCalendar().getAllEvents();
    assertEquals(1, events.length);
    Map<String, String> eventDetails = events[0];
    assertEquals("event1", eventDetails.get("Name"));
    assertEquals("03/20/2025", eventDetails.get("StartDate"));
    assertEquals("10:00 PM", eventDetails.get("StartTime"));
    assertEquals("03/20/2025", eventDetails.get("EndDate"));
    assertEquals("11:00 PM", eventDetails.get("EndTime"));
    assertEquals("event1 description", eventDetails.get("Description"));
    assertEquals("event1 location", eventDetails.get("Location"));
    assertEquals("true", eventDetails.get("IsPrivate"));
    assertEquals("false", eventDetails.get("IsAllDay"));
    // 12-hour time shift ahead
  }

  @Test
  public void testEditCalendarNameThenTimezoneThenEvent() {
    CalendarModel model = new BasicCalendarModel();
    model.addCalendar("OldName", "America/New_York");
    model.useCalendar("OldName");
    model.getCurrentCalendar().addSingleEvent(
            "event1",
            "2025-03-20T10:00",
            "2025-03-20T11:00",
            "event1 description",
            "event1 location",
            true,
            true);
    model.editCalendar("OldName", "name", "NewName");
    model.editCalendar("NewName", "timezone", "Asia/Singapore");
    Map<String, String>[] events = model.getCurrentCalendar().getAllEvents();
    assertEquals(1, events.length);
    Map<String, String> eventDetails = events[0];
    assertEquals("event1", eventDetails.get("Name"));
    assertEquals("03/20/2025", eventDetails.get("StartDate"));
    assertEquals("10:00 PM", eventDetails.get("StartTime"));
    assertEquals("03/20/2025", eventDetails.get("EndDate"));
    assertEquals("11:00 PM", eventDetails.get("EndTime"));
    assertEquals("event1 description", eventDetails.get("Description"));
    assertEquals("event1 location", eventDetails.get("Location"));
    assertEquals("true", eventDetails.get("IsPrivate"));
    assertEquals("false", eventDetails.get("IsAllDay"));
    model.getCurrentCalendar().editSingleEvent(
            "event1",
            "2025-03-20T22:00",
            "2025-03-20T23:00",
            "name",
            "NewEventName",
            false);
    Map<String, String>[] events2 = model.getCurrentCalendar().getAllEvents();
    assertEquals(1, events2.length);
    Map<String, String> eventDetails2 = events2[0];
    assertEquals("NewEventName", eventDetails2.get("Name"));
    assertEquals("03/20/2025", eventDetails2.get("StartDate"));
    assertEquals("10:00 PM", eventDetails2.get("StartTime"));
    assertEquals("03/20/2025", eventDetails2.get("EndDate"));
    assertEquals("11:00 PM", eventDetails2.get("EndTime"));
    assertEquals("event1 description", eventDetails2.get("Description"));
    assertEquals("event1 location", eventDetails2.get("Location"));
    assertEquals("true", eventDetails2.get("IsPrivate"));
    assertEquals("false", eventDetails2.get("IsAllDay"));
  }

  @Test
  public void testEditCalendarRecurringEvent() {
    CalendarModel model = new BasicCalendarModel();
    model.addCalendar("OldName", "America/New_York");
    model.useCalendar("OldName");
    model.getCurrentCalendar().addRecurringEventUntil(
            "event1",
            "2025-03-20T10:00",
            "2025-03-20T11:00",
            "event1 description",
            "event1 location",
            "RF",
            "2025-03-29T10:00",
            false,
            false);
    model.editCalendar("OldName", "name", "NewName");
    model.editCalendar("NewName", "timezone", "Asia/Singapore");
    Map<String, String>[] events = model.getCurrentCalendar().getAllEvents();
    assertEquals(4, events.length);
    model.getCurrentCalendar().editMultipleEventsFollowing(
            "event1",
            "2025-03-20T22:00",
            "name",
            "NewEventName",
            false);
    Map<String, String>[] events2 = model.getCurrentCalendar().getAllEvents();
    assertEquals(4, events.length);
    for (Map<String, String> stringStringMap : events2) {
      assertEquals("NewEventName",stringStringMap .get("Name"));
    }

  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopySingleEventTargetCalendarDoesNotExists() {
    CalendarModel model = new BasicCalendarModel();
    model.getCurrentCalendar().addSingleEvent(
            "event1",
            "2025-03-21T10:00",
            "2025-03-21T11:00",
            "event1 description",
            "event1 location",
            true,
            true);
    try {
      model.copySingleEvent(
              "event1",
              "NewIndia",
              "2025-03-21T10:00",
              "2025-03-24T15:00",
              true);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Calendar does not exist: NewIndia", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyMultipleEventTargetCalendarDoesNotExists() {
    CalendarModel model = new BasicCalendarModel();
    model.getCurrentCalendar().addSingleEvent(
            "event1",
            "2025-03-21T10:00",
            "2025-03-21T11:00",
            "event1 description",
            "event1 location",
            true,
            true);
    try {
      model.copyMultipleEventsRange(
              "NewIndia",
              "2025-03-21T10:00",
              "2025-03-21T10:00",
              "2025-03-24T15:00",
              true);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Calendar does not exist: NewIndia", e.getMessage());
      throw e;
    }
  }

  @Test
  public void testCopySingleEvent() {
    CalendarModel model = new BasicCalendarModel();
    model.getCurrentCalendar().addSingleEvent(
            "event1",
            "2025-03-21T10:00",
            "2025-03-21T11:00",
            "event1 description",
            "event1 location",
            true,
            true);
    model.addCalendar("NewIndia", "Asia/Kolkata");
    model.copySingleEvent(
            "event1",
            "NewIndia",
            "2025-03-21T10:00",
            "2025-03-24T15:00",
            true);
    model.useCalendar("NewIndia");
    Map<String, String>[] events = model.getCurrentCalendar().getAllEvents();
    assertEquals(1, events.length);
    Map<String, String> eventDetails = events[0];
    assertEquals("event1", eventDetails.get("Name"));
    assertEquals("03/24/2025", eventDetails.get("StartDate"));
    assertEquals("03:00 PM", eventDetails.get("StartTime"));
    assertEquals("03/24/2025", eventDetails.get("EndDate"));
    assertEquals("04:00 PM", eventDetails.get("EndTime"));
    assertEquals("event1 description", eventDetails.get("Description"));
    assertEquals("event1 location", eventDetails.get("Location"));
    assertEquals("true", eventDetails.get("IsPrivate"));
    assertEquals("false", eventDetails.get("IsAllDay"));
  }

  @Test
  public void testCopySingleEventBackwards() {
    CalendarModel model = new BasicCalendarModel();
    model.getCurrentCalendar().addSingleEvent(
            "event1",
            "2025-03-21T10:00",
            "2025-03-21T11:00",
            "event1 description",
            "event1 location",
            true,
            true);
    model.addCalendar("NewIndia", "Asia/Kolkata");
    model.copySingleEvent(
            "event1",
            "NewIndia",
            "2025-03-21T10:00",
            "2025-03-10T06:00",
            true);
    model.useCalendar("NewIndia");
    Map<String, String>[] events = model.getCurrentCalendar().getAllEvents();
    assertEquals(1, events.length);
    Map<String, String> eventDetails = events[0];
    assertEquals("event1", eventDetails.get("Name"));
    assertEquals("03/10/2025", eventDetails.get("StartDate"));
    assertEquals("06:00 AM", eventDetails.get("StartTime"));
    assertEquals("03/10/2025", eventDetails.get("EndDate"));
    assertEquals("07:00 AM", eventDetails.get("EndTime"));
    assertEquals("event1 description", eventDetails.get("Description"));
    assertEquals("event1 location", eventDetails.get("Location"));
    assertEquals("true", eventDetails.get("IsPrivate"));
    assertEquals("false", eventDetails.get("IsAllDay"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopySingleEventCheckConflict() {
    CalendarModel model = new BasicCalendarModel();
    model.getCurrentCalendar().addSingleEvent(
            "event1",
            "2025-03-21T10:00",
            "2025-03-21T11:00",
            "event1 description",
            "event1 location",
            true,
            true);
    model.addCalendar("NewIndia", "Asia/Kolkata");
    model.useCalendar("NewIndia");
    model.getCurrentCalendar().addSingleEvent(
            "event1India",
            "2025-03-24T03:10",
            "2025-03-24T03:50",
            "event1 description",
            "event1 location",
            true,
            true);
    model.useCalendar("default");
    try {
      model.copySingleEvent(
              "event1",
              "NewIndia",
              "2025-03-21T10:00",
              "2025-03-24T03:00",
              true);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Conflict detected, no events were added", e.getMessage());
      throw e;
    }
  }

  @Test
  public void testCopyRecurringEventToDiffDayOfWeek() {
    CalendarModel model = new BasicCalendarModel();
    model.addCalendar("OriginalCalendar", "America/New_York");
    model.useCalendar("OriginalCalendar");
    model.getCurrentCalendar().addRecurringEventUntil(
            "MWEvent",
            "2025-03-10T10:00", // Monday
            "2025-03-10T11:00",
            "MWEvent description",
            "MWEvent location",
            "MW",
            "2025-04-09T12:00", // 10 iterations
            true,
            true);

    model.addCalendar("NewCalendar", "America/New_York");
    model.copyMultipleEventsRange(
            "NewCalendar",
            "2025-03-10",
            "2025-04-10",
            "2025-03-13", // Next Thursday
            true);
    model.useCalendar("NewCalendar");
    Map<String, String>[] events = model.getCurrentCalendar().getAllEvents();
    assertEquals(10, events.length);
    Map<String, String> eventDetails0 = events[0];
    assertEquals("MWEvent", eventDetails0.get("Name"));
    assertEquals("03/13/2025", eventDetails0.get("StartDate"));
    assertEquals("10:00 AM", eventDetails0.get("StartTime"));
    assertEquals("03/13/2025", eventDetails0.get("EndDate"));
    assertEquals("11:00 AM", eventDetails0.get("EndTime"));

    Map<String, String> eventDetails9 = events[9];
    assertEquals("MWEvent", eventDetails9.get("Name"));
    assertEquals("04/12/2025", eventDetails9.get("StartDate"));
    assertEquals("10:00 AM", eventDetails9.get("StartTime"));
    assertEquals("04/12/2025", eventDetails9.get("EndDate"));
    assertEquals("11:00 AM", eventDetails9.get("EndTime"));

  }

  @Test
  public void testCopySingleOccurrence() {
    CalendarModel model = new BasicCalendarModel();
    model.getCurrentCalendar().addRecurringEventUntil(
            "event1",
            "2025-03-21T10:00",
            "2025-03-21T11:00",
            "event1 description",
            "event1 location",
            "MTWRF",
            "2025-03-30T10:00",
            true,
            true);

    model.addCalendar("NewIndia", "Asia/Kolkata");
    model.copySingleEvent(
            "event1",
            "NewIndia",
            "2025-03-24T10:00",
            "2025-03-24T15:00",
            true);
    model.useCalendar("NewIndia");
    Map<String, String>[] events = model.getCurrentCalendar().getAllEvents();
    Map<String, String> eventDetails = events[0];
    assertEquals("event1", eventDetails.get("Name"));
    assertEquals("03/24/2025", eventDetails.get("StartDate"));
    assertEquals("03:00 PM", eventDetails.get("StartTime"));
    assertEquals("03/24/2025", eventDetails.get("EndDate"));
    assertEquals("04:00 PM", eventDetails.get("EndTime"));
    assertEquals("event1 description", eventDetails.get("Description"));
    assertEquals("event1 location", eventDetails.get("Location"));
    assertEquals("true", eventDetails.get("IsPrivate"));
    assertEquals("false", eventDetails.get("IsAllDay"));
  }

  @Test
  public void testCopySingleAllDayEvent() {
    CalendarModel model = new BasicCalendarModel();
    model.getCurrentCalendar().addSingleEventAllDay(
            "event1",
            "2025-03-21T10:00",
            "event1 description",
            "event1 location",
            true,
            true);
    model.addCalendar("NewIndia", "Asia/Kolkata");
    model.copySingleEvent(
            "event1",
            "NewIndia",
            "2025-03-21T00:00",
            "2025-03-24T15:00",
            true);
    model.useCalendar("NewIndia");
    Map<String, String>[] events = model.getCurrentCalendar().getAllEvents();
    Map<String, String> eventDetails = events[0];
    assertEquals("event1", eventDetails.get("Name"));
    assertEquals("03/24/2025", eventDetails.get("StartDate"));
    assertEquals("03:00 PM", eventDetails.get("StartTime"));
    assertEquals("03/25/2025", eventDetails.get("EndDate"));
    assertEquals("02:59 PM", eventDetails.get("EndTime"));
    assertEquals("event1 description", eventDetails.get("Description"));
    assertEquals("event1 location", eventDetails.get("Location"));
    assertEquals("true", eventDetails.get("IsPrivate"));
    assertEquals("false", eventDetails.get("IsAllDay")); // should be false
  }

  @Test
  public void testCopyMultiple() {
    CalendarModel model = new BasicCalendarModel();
    model.addCalendar("NewBoston", "America/New_York");
    model.useCalendar("NewBoston");
    model.getCurrentCalendar().addSingleEvent(
            "event1",
            "2025-03-21T10:00",
            "2025-03-21T11:00",
            "event1 description",
            "event1 location",
            true,
            true);
    model.getCurrentCalendar().addSingleEvent(
            "event2",
            "2025-03-21T15:00",
            "2025-03-21T17:00",
            "event2 description",
            "event2 location",
            true,
            true);
    model.addCalendar("NewIndia", "Asia/Kolkata");
    model.copyMultipleEventsOnDay(
            "NewIndia",
            "2025-03-21",
            "2025-03-24",
            true);
    model.useCalendar("NewIndia");
    Map<String, String>[] events = model.getCurrentCalendar().getAllEvents();
    assertEquals(2, events.length);
    Map<String, String> eventDetails = events[0];
    assertEquals("event1", eventDetails.get("Name"));
    assertEquals("03/24/2025", eventDetails.get("StartDate"));
    assertEquals("07:30 PM", eventDetails.get("StartTime"));
    assertEquals("03/24/2025", eventDetails.get("EndDate"));
    assertEquals("08:30 PM", eventDetails.get("EndTime"));
    assertEquals("event1 description", eventDetails.get("Description"));
    assertEquals("event1 location", eventDetails.get("Location"));
    assertEquals("true", eventDetails.get("IsPrivate"));
    assertEquals("false", eventDetails.get("IsAllDay"));


    Map<String, String> eventDetails1 = events[1];
    assertEquals("event2", eventDetails1.get("Name"));
    assertEquals("03/25/2025", eventDetails1.get("StartDate"));
    assertEquals("12:30 AM", eventDetails1.get("StartTime"));
    assertEquals("03/25/2025", eventDetails1.get("EndDate"));
    assertEquals("02:30 AM", eventDetails1.get("EndTime"));
    assertEquals("event2 description", eventDetails1.get("Description"));
    assertEquals("event2 location", eventDetails1.get("Location"));
    assertEquals("true", eventDetails1.get("IsPrivate"));
    assertEquals("false", eventDetails1.get("IsAllDay"));
  }

  @Test
  public void testCopyMultiple2() {
    CalendarModel model = new BasicCalendarModel();
    model.addCalendar("NewBoston", "America/New_York");
    model.useCalendar("NewBoston");
    model.getCurrentCalendar().addSingleEvent(
            "event1",
            "2025-03-21T10:00",
            "2025-03-21T11:00",
            "event1 description",
            "event1 location",
            true,
            true);
    model.getCurrentCalendar().addSingleEvent(
            "event2",
            "2025-03-20T15:00",
            "2025-03-21T09:00",
            "event2 description",
            "event2 location",
            true,
            true);
    model.addCalendar("NewIndia", "Asia/Kolkata");
    model.copyMultipleEventsOnDay(
            "NewIndia",
            "2025-03-21",
            "2025-03-24",
            true);
    model.useCalendar("NewIndia");
    Map<String, String>[] events = model.getCurrentCalendar().getAllEvents();
    assertEquals(2, events.length);
    Map<String, String> eventDetails = events[0];
    assertEquals("event1", eventDetails.get("Name"));
    assertEquals("03/24/2025", eventDetails.get("StartDate"));
    assertEquals("07:30 PM", eventDetails.get("StartTime"));
    assertEquals("03/24/2025", eventDetails.get("EndDate"));
    assertEquals("08:30 PM", eventDetails.get("EndTime"));
    assertEquals("event1 description", eventDetails.get("Description"));
    assertEquals("event1 location", eventDetails.get("Location"));
    assertEquals("true", eventDetails.get("IsPrivate"));
    assertEquals("false", eventDetails.get("IsAllDay"));


    Map<String, String> eventDetails1 = events[1];
    assertEquals("event2", eventDetails1.get("Name"));
    assertEquals("03/24/2025", eventDetails1.get("StartDate"));
    assertEquals("12:30 AM", eventDetails1.get("StartTime"));
    assertEquals("03/24/2025", eventDetails1.get("EndDate"));
    assertEquals("06:30 PM", eventDetails1.get("EndTime"));
    assertEquals("event2 description", eventDetails1.get("Description"));
    assertEquals("event2 location", eventDetails1.get("Location"));
    assertEquals("true", eventDetails1.get("IsPrivate"));
    assertEquals("false", eventDetails1.get("IsAllDay"));
  }

  @Test
  public void testCopyMultiple3() {
    CalendarModel model = new BasicCalendarModel();
    model.addCalendar("NewBoston", "America/New_York");
    model.useCalendar("NewBoston");
    model.getCurrentCalendar().addSingleEvent(
            "event1",
            "2025-03-21T10:00",
            "2025-03-21T11:00",
            "event1 description",
            "event1 location",
            true,
            true);
    model.getCurrentCalendar().addSingleEvent(
            "event2",
            "2025-03-19T15:00",
            "2025-03-21T09:00",
            "event2 description",
            "event2 location",
            true,
            true);
    model.addCalendar("NewIndia", "Asia/Kolkata");
    model.copyMultipleEventsOnDay(
            "NewIndia",
            "2025-03-21",
            "2025-03-24",
            true);
    model.useCalendar("NewIndia");
    Map<String, String>[] events = model.getCurrentCalendar().getAllEvents();
    assertEquals(2, events.length);
    Map<String, String> eventDetails = events[0];
    assertEquals("event1", eventDetails.get("Name"));
    assertEquals("03/24/2025", eventDetails.get("StartDate"));
    assertEquals("07:30 PM", eventDetails.get("StartTime"));
    assertEquals("03/24/2025", eventDetails.get("EndDate"));
    assertEquals("08:30 PM", eventDetails.get("EndTime"));
    assertEquals("event1 description", eventDetails.get("Description"));
    assertEquals("event1 location", eventDetails.get("Location"));
    assertEquals("true", eventDetails.get("IsPrivate"));
    assertEquals("false", eventDetails.get("IsAllDay"));


    Map<String, String> eventDetails1 = events[1];
    assertEquals("event2", eventDetails1.get("Name"));
    assertEquals("03/23/2025", eventDetails1.get("StartDate"));
    assertEquals("12:30 AM", eventDetails1.get("StartTime"));
    assertEquals("03/24/2025", eventDetails1.get("EndDate"));
    assertEquals("06:30 PM", eventDetails1.get("EndTime"));
    assertEquals("event2 description", eventDetails1.get("Description"));
    assertEquals("event2 location", eventDetails1.get("Location"));
    assertEquals("true", eventDetails1.get("IsPrivate"));
    assertEquals("false", eventDetails1.get("IsAllDay"));
  }

  @Test
  public void testCopyMultipleBetween() {
    CalendarModel model = new BasicCalendarModel();
    model.addCalendar("NewBoston", "America/New_York");
    model.useCalendar("NewBoston");
    model.getCurrentCalendar().addSingleEvent(
            "event1",
            "2025-03-21T10:00",
            "2025-03-21T11:00",
            "event1 description",
            "event1 location",
            true,
            true);
    model.getCurrentCalendar().addSingleEvent(
            "event2",
            "2025-03-22T10:00",
            "2025-03-22T11:00",
            "event2 description",
            "event2 location",
            true,
            true);
    model.addCalendar("NewIndia", "Asia/Kolkata");
    model.copyMultipleEventsRange(
            "NewIndia",
            "2025-03-21",
            "2025-03-22",
            "2025-03-24",
            true);
    model.useCalendar("NewIndia");
    Map<String, String>[] events = model.getCurrentCalendar().getAllEvents();
    assertEquals(2, events.length);
    Map<String, String> eventDetails = events[0];
    assertEquals("event1", eventDetails.get("Name"));
    assertEquals("03/24/2025", eventDetails.get("StartDate"));
    assertEquals("07:30 PM", eventDetails.get("StartTime"));
    assertEquals("03/24/2025", eventDetails.get("EndDate"));
    assertEquals("08:30 PM", eventDetails.get("EndTime"));
    assertEquals("event1 description", eventDetails.get("Description"));
    assertEquals("event1 location", eventDetails.get("Location"));
    assertEquals("true", eventDetails.get("IsPrivate"));
    assertEquals("false", eventDetails.get("IsAllDay"));


    Map<String, String> eventDetails1 = events[1];
    assertEquals("event2", eventDetails1.get("Name"));
    assertEquals("03/25/2025", eventDetails1.get("StartDate"));
    assertEquals("07:30 PM", eventDetails1.get("StartTime"));
    assertEquals("03/25/2025", eventDetails1.get("EndDate"));
    assertEquals("08:30 PM", eventDetails1.get("EndTime"));
    assertEquals("event2 description", eventDetails1.get("Description"));
    assertEquals("event2 location", eventDetails1.get("Location"));
    assertEquals("true", eventDetails1.get("IsPrivate"));
    assertEquals("false", eventDetails1.get("IsAllDay"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyMultipleOnDayInvalid() {
    CalendarModel model = new BasicCalendarModel();
    try {
      model.copyMultipleEventsOnDay(
              "DNE",
              "2025-03-21",
              "2025-03-24",
              true);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Calendar does not exist: DNE", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopySingleInvalid() {
    CalendarModel model = new BasicCalendarModel();
    try {
      model.copySingleEvent("DNE",
              "default",
              "2025-03-21",
              "2025-03-24",
              true);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Event does not exist: DNE", e.getMessage());
      throw e;
    }
  }

  @Test
  public void testCopyMultipleBetween2() {
    CalendarModel model = new BasicCalendarModel();
    model.addCalendar("NewBoston", "America/New_York");
    model.useCalendar("NewBoston");
    model.getCurrentCalendar().addSingleEvent(
            "event1",
            "2025-03-21T10:00",
            "2025-03-21T11:00",
            "event1 description",
            "event1 location",
            true,
            true);
    model.getCurrentCalendar().addSingleEvent(
            "event2",
            "2025-03-22T15:00",
            "2025-03-22T17:00",
            "event2 description",
            "event2 location",
            true,
            true);
    model.addCalendar("NewIndia", "Asia/Kolkata");
    model.copyMultipleEventsRange(
            "NewIndia",
            "2025-03-21",
            "2025-03-22",
            "2025-03-24",
            true);
    model.useCalendar("NewIndia");
    Map<String, String>[] events = model.getCurrentCalendar().getAllEvents();
    assertEquals(2, events.length);
    Map<String, String> eventDetails = events[0];
    assertEquals("event1", eventDetails.get("Name"));
    assertEquals("03/24/2025", eventDetails.get("StartDate"));
    assertEquals("07:30 PM", eventDetails.get("StartTime"));
    assertEquals("03/24/2025", eventDetails.get("EndDate"));
    assertEquals("08:30 PM", eventDetails.get("EndTime"));
    assertEquals("event1 description", eventDetails.get("Description"));
    assertEquals("event1 location", eventDetails.get("Location"));
    assertEquals("true", eventDetails.get("IsPrivate"));
    assertEquals("false", eventDetails.get("IsAllDay"));


    Map<String, String> eventDetails1 = events[1];
    assertEquals("event2", eventDetails1.get("Name"));
    assertEquals("03/26/2025", eventDetails1.get("StartDate"));
    assertEquals("12:30 AM", eventDetails1.get("StartTime"));
    assertEquals("03/26/2025", eventDetails1.get("EndDate"));
    assertEquals("02:30 AM", eventDetails1.get("EndTime"));
    assertEquals("event2 description", eventDetails1.get("Description"));
    assertEquals("event2 location", eventDetails1.get("Location"));
    assertEquals("true", eventDetails1.get("IsPrivate"));
    assertEquals("false", eventDetails1.get("IsAllDay"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyMultipleBetweenInvalidDueToConflict() {
    CalendarModel model = new BasicCalendarModel();
    model.addCalendar("NewBoston", "America/New_York");
    model.useCalendar("NewBoston");
    model.getCurrentCalendar().addSingleEvent(
            "event1",
            "2025-03-21T10:00",
            "2025-03-21T11:00",
            "event1 description",
            "event1 location",
            true,
            true);
    model.getCurrentCalendar().addSingleEvent(
            "event2",
            "2025-03-22T15:00",
            "2025-03-22T17:00",
            "event2 description",
            "event2 location",
            true,
            true);
    model.addCalendar("NewIndia", "Asia/Kolkata");
    model.useCalendar("NewIndia");
    model.getCurrentCalendar().addSingleEvent(
            "event1India",
            "2025-03-24T19:10",
            "2025-03-24T19:50",
            "event1 description",
            "event1 location",
            true,
            true);
    model.useCalendar("NewBoston");
    try {
      model.copyMultipleEventsRange(
              "NewIndia",
              "2025-03-21",
              "2025-03-22",
              "2025-03-24",
              true);
    } catch (IllegalArgumentException e) {
      assertEquals("Conflict detected, no events were added", e.getMessage());
      model.useCalendar("NewIndia");
      Map<String, String>[] events = model.getCurrentCalendar().getAllEvents();
      assertEquals(1, events.length);
      Map<String, String> eventDetails = events[0];
      assertEquals("event1India", eventDetails.get("Name"));
      assertEquals("03/24/2025", eventDetails.get("StartDate"));
      assertEquals("07:10 PM", eventDetails.get("StartTime"));
      assertEquals("03/24/2025", eventDetails.get("EndDate"));
      assertEquals("07:50 PM", eventDetails.get("EndTime"));
      assertEquals("event1 description", eventDetails.get("Description"));
      assertEquals("event1 location", eventDetails.get("Location"));
      assertEquals("true", eventDetails.get("IsPrivate"));
      assertEquals("false", eventDetails.get("IsAllDay"));
      throw e;
    }
  }

  @Test
  public void testCopyToSameCalendar() {
    CalendarModel model = new BasicCalendarModel();
    model.addCalendar("NewBoston", "America/New_York");
    model.useCalendar("NewBoston");
    model.getCurrentCalendar().addSingleEvent(
            "event1",
            "2025-03-21T10:00",
            "2025-03-21T11:00",
            "event1 description",
            "event1 location",
            true,
            true);
    model.getCurrentCalendar().addSingleEvent(
            "event2",
            "2025-03-22T11:00",
            "2025-03-22T12:00",
            "event1 description",
            "event1 location",
            true,
            true);
    model.copyMultipleEventsRange(
            "NewBoston",
            "2025-03-21",
            "2025-03-22",
            "2025-03-24",
            true);
    Map<String, String>[] events = model.getCurrentCalendar().getAllEvents();
    assertEquals(4, events.length);
    Map<String, String> eventDetails = events[0];
    assertEquals("event1", eventDetails.get("Name"));
    assertEquals("03/21/2025", eventDetails.get("StartDate"));
    assertEquals("10:00 AM", eventDetails.get("StartTime"));
    assertEquals("03/21/2025", eventDetails.get("EndDate"));
    assertEquals("11:00 AM", eventDetails.get("EndTime"));
    assertEquals("event1 description", eventDetails.get("Description"));
    assertEquals("event1 location", eventDetails.get("Location"));
    assertEquals("true", eventDetails.get("IsPrivate"));
    assertEquals("false", eventDetails.get("IsAllDay"));

    Map<String, String> eventDetails3 = events[3];
    assertEquals("event2", eventDetails3.get("Name"));
    assertEquals("03/25/2025", eventDetails3.get("StartDate"));
    assertEquals("11:00 AM", eventDetails3.get("StartTime"));
    assertEquals("03/25/2025", eventDetails3.get("EndDate"));
    assertEquals("12:00 PM", eventDetails3.get("EndTime"));
    assertEquals("event1 description", eventDetails3.get("Description"));
    assertEquals("event1 location", eventDetails3.get("Location"));
    assertEquals("true", eventDetails3.get("IsPrivate"));
    assertEquals("false", eventDetails3.get("IsAllDay"));

  }

  @Test
  public void getCurrentCalendarName() {
    CalendarModel model = new BasicCalendarModel();
    model.addCalendar("NewBoston", "America/New_York");
    model.useCalendar("NewBoston");
    assertEquals("NewBoston", model.getCurrentCalendarName());
  }

  @Test
  public void testGetAllCalendars() {
    CalendarModel model = new BasicCalendarModel();
    model.addCalendar("NewBoston", "America/New_York");
    model.addCalendar("NewIndia", "Asia/Kolkata");
    Map<String, String> calendars = model.getAllCalendars();
    assertEquals(3, calendars.size()); // default calendar + 2 new calendars
    assertTrue(calendars.containsKey("NewBoston"));
    assertTrue(calendars.containsKey("NewIndia"));
    assertEquals("America/New_York", calendars.get("NewBoston"));
    assertEquals("Asia/Kolkata", calendars.get("NewIndia"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void tesShouldNotEditCurrentCalendarToExistingCalendarName() {
    CalendarModel model = new BasicCalendarModel();
    model.addCalendar("NewBoston", "America/New_York");
    model.addCalendar("NewIndia", "Asia/Kolkata");
    try {
      model.editCalendar("NewBoston", "name", "NewIndia");
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Calendar already exists: NewIndia", e.getMessage());
      throw e;
    }
  }
}