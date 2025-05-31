import org.junit.Test;

import java.util.Map;

import model.BasicSingleCalendar;
import model.SingleCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test class for BasicSingleCalendar.
 */
public class BasicSingleCalendarTest {


  private final SingleCalendar singleCal;

  /**
   * Constructor for BasicSingleCalendarTest.
   */
  public BasicSingleCalendarTest() {
    this.singleCal = new BasicSingleCalendar("America/New_York");
  }

  @Test
  public void testAddSingleEvent() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    final Map<String,String>[] events = singleCal
            .getEventsRange("2021-04-01T12:00", "2021-04-01T13:00");
    assertEquals(1, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2021-04-01T12:00", events[0].get("StartDateTime"));
    assertEquals("2021-04-01T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));
  }

  @Test
  public void testAddSingleEventAllDay() {
    singleCal.addSingleEventAllDay("Event1",
            "2021-04-01",
            "Description",
            "Location",
            false,
            false);
    final Map<String,String>[] events = singleCal
            .getEventsOnDate("2021-04-01");
    assertEquals(1, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2021-04-01T00:00", events[0].get("StartDateTime"));
    assertEquals("2021-04-01T23:59", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));
  }

  @Test
  public void testAddSingleEventAllDayIgnoreTime() {
    singleCal.addSingleEventAllDay("Event1",
        "2021-04-01T09:00",
        "Description",
        "Location",
        false,
        false);
    final Map<String, String>[] events = singleCal.getEventsOnDate("2021-04-01");
    assertEquals(1, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2021-04-01T00:00", events[0].get("StartDateTime"));
    assertEquals("2021-04-01T23:59", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));
  }

  @Test
  public void testAddSingleEventConflictNoDecline() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    singleCal.addSingleEvent("Event2",
            "2021-04-01T12:30",
            "2021-04-01T13:30",
            "Description",
            "Location",
            false,
            false);
    final Map<String,String>[] events = singleCal.getEventsOnDate("2021-04-01");
    assertEquals(2, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2021-04-01T12:00", events[0].get("StartDateTime"));
    assertEquals("2021-04-01T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));
    assertEquals("Event2", events[1].get("Name"));
    assertEquals("2021-04-01T12:30", events[1].get("StartDateTime"));
    assertEquals("2021-04-01T13:30", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));

  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddSingleEventConflictWithAutoDecline() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    try {
      singleCal.addSingleEvent("Event2",
              "2021-04-01T12:30",
              "2021-04-01T13:30",
              "Description",
              "Location",
              true,
              false);
    } catch (IllegalArgumentException e) {
      assertEquals("Conflict detected, Event not Created", e.getMessage());
      final Map<String,String>[] events = singleCal.getEventsOnDate("2021-04-01");
      assertEquals(1, events.length);
      assertEquals("Event1", events[0].get("Name"));
      assertEquals("2021-04-01T12:00", events[0].get("StartDateTime"));
      assertEquals("2021-04-01T13:00", events[0].get("EndDateTime"));
      assertEquals("Location", events[0].get("Location"));
      throw e;
    }
  }

  @Test
  public void testAddRecurringEvent() {
    singleCal.addRecurringEventUntil("Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTW",
            "2025-03-12T23:00",
            false,
            true);
    final Map<String,String>[] events = singleCal
            .getEventsRange("2025-03-08T09:00", "2025-03-30T12:00");
    assertEquals(3, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));
    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-11T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-11T13:00", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));
    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-12T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));
  }

  @Test
  public void testAddRecurringEventAllDay() {
    singleCal.addRecurringAllDayEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "Description",
            "Location",
            "MTW",
            "2025-03-12T23:00",
            false,
            true);
    final Map<String,String>[] events = singleCal
            .getEventsRange("2025-03-08T09:00", "2025-03-30T12:00");
    assertEquals(3, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T00:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T23:59", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));
    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-11T00:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-11T23:59", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));
    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-12T00:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-12T23:59", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));
  }

  @Test
  public void testAddRecurringEventFrequency() {
    singleCal.addRecurringEventFrequency("Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTW",
            3,
            false,
            true);
    final Map<String,String>[] events = singleCal
            .getEventsRange("2025-03-08T09:00", "2025-03-30T12:00");
    assertEquals(3, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));
    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-11T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-11T13:00", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));
    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-12T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));
  }

  @Test
  public void testAddRecurringEventAllDayFrequency() {
    singleCal.addRecurringAllDayEventFrequency("Event1",
            "2025-03-08T12:00",
            "Description",
            "Location",
            "MTW",
            3,
            false,
            true);
    final Map<String,String>[] events = singleCal
            .getEventsRange("2025-03-08T09:00", "2025-03-30T12:00");
    assertEquals(3, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T00:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T23:59", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-11T00:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-11T23:59", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-12T00:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-12T23:59", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddRecurringEventConflictWithSingle() {
    singleCal.addSingleEvent("Event1",
            "2025-03-10T12:30",
            "2025-03-10T13:40",
            "Description",
            "Location",
            true,
            true);
    final Map<String,String>[] events = singleCal
            .getEventsRange("2025-03-08T09:00", "2025-03-30T12:00");
    assertEquals(1, events.length);

    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T12:30", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:40", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));

    try {
      singleCal.addRecurringEventUntil("Event1",
              "2025-03-08T12:00",
              "2025-03-08T13:00",
              "Description",
              "Location",
              "MTW",
              "2025-03-12T23:00",
              true,
              true);
    } catch (IllegalArgumentException e) {
      assertEquals("Conflict detected, Event not Created", e.getMessage());
      final Map<String,String>[] events2 = singleCal
              .getEventsRange("2025-03-08T09:00", "2025-03-30T12:00");
      assertEquals(1, events2.length);

      assertEquals("Event1", events2[0].get("Name"));
      assertEquals("2025-03-10T12:30", events2[0].get("StartDateTime"));
      assertEquals("2025-03-10T13:40", events2[0].get("EndDateTime"));
      assertEquals("Location", events2[0].get("Location"));
      throw e;
    }
  }

  @Test
  public void testAddRecurringEventNoConflictWithRec() {
    singleCal.addRecurringEventUntil("Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTW",
            "2025-03-12T23:00",
            false,
            true);
    final Map<String,String>[] events = singleCal
            .getEventsRange("2025-03-08T09:00", "2025-03-30T12:00");
    assertEquals(3, events.length);

    try {
      singleCal.addRecurringEventUntil("Event1",
              "2025-03-08T12:00",
              "2025-03-08T13:00",
              "Description",
              "Location",
              "FSU",
              "2025-03-12T23:00",
              false,
              true);
      final Map<String,String>[] events2 = singleCal
              .getEventsRange("2025-03-08T09:00", "2025-03-30T12:00");
      assertEquals(5, events2.length);
    } catch (IllegalArgumentException e) {
      fail("Conflict detected, Event not Created");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddRecurringInvalidStAndDt() {
    try {
      singleCal.addRecurringEventUntil("Event1",
              "2025-03-08T12:00",
              "2025-03-08T11:00",
              "Description",
              "Location",
              "MTW",
              "2025-03-18T12:00",
              false,
              true);
    } catch (IllegalArgumentException e) {
      assertEquals("End time must be after start time", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddRecurringEventConflictWithRec() {
    singleCal.addRecurringEventUntil("Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTW",
            "2025-03-12T23:00",
            true,
            true);
    final Map<String,String>[] events = singleCal
            .getEventsRange("2025-03-08T09:00", "2025-03-30T12:00");
    assertEquals(3, events.length);

    try {
      singleCal.addRecurringEventUntil("Event1",
              "2025-03-08T12:20",
              "2025-03-08T12:40",
              "Description",
              "Location",
              "W",
              "2025-03-12T23:00",
              true,
              true);
    } catch (IllegalArgumentException e) {
      assertEquals("Conflict detected, Event not Created", e.getMessage());
      final Map<String,String>[] events2 = singleCal
              .getEventsRange("2025-03-08T09:00", "2025-03-30T12:00");
      assertEquals(3, events2.length); // should not change the number of events
      throw e;
    }
  }

  @Test
  public void testEditSingleEventLocation() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Old Location",
            false,
            false);
    final Map<String,String>[] events = singleCal
            .getEventsRange("2021-04-01T12:00", "2021-04-01T13:00");
    assertEquals(1, events.length);

    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2021-04-01T12:00", events[0].get("StartDateTime"));
    assertEquals("2021-04-01T13:00", events[0].get("EndDateTime"));
    assertEquals("Old Location", events[0].get("Location"));
    singleCal.editSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "location",
            "New Location",
            false);
    final Map<String,String>[] events2 = singleCal
            .getEventsRange("2021-04-01T12:00", "2021-04-01T13:00");
    assertEquals(1, events2.length);

    assertEquals("Event1", events2[0].get("Name"));
    assertEquals("2021-04-01T12:00", events2[0].get("StartDateTime"));
    assertEquals("2021-04-01T13:00", events2[0].get("EndDateTime"));
    assertEquals("New Location", events2[0].get("Location"));

  }

  @Test
  public void testEditSingleEventName() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    final Map<String,String>[] events = singleCal
            .getEventsRange("2021-04-01T12:00", "2021-04-01T13:00");
    assertEquals(1, events.length);

    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2021-04-01T12:00", events[0].get("StartDateTime"));
    assertEquals("2021-04-01T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));
    singleCal.editSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "name",
            "New EvName",
            false);
    final Map<String,String>[] events2 = singleCal
            .getEventsRange("2021-04-01T12:00", "2021-04-01T13:00");
    assertEquals(1, events2.length);

    assertEquals("New EvName", events2[0].get("Name"));
    assertEquals("2021-04-01T12:00", events2[0].get("StartDateTime"));
    assertEquals("2021-04-01T13:00", events2[0].get("EndDateTime"));
  }

  @Test
  public void testEditSingleEventPublic() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            true);
    singleCal.editSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "public",
            "true",
            false);
    final Map<String,String>[] events2 = singleCal.getAllEvents();
    assertEquals(1, events2.length);
    assertEquals("Event1", events2[0].get("Name"));
    assertEquals("04/01/2021", events2[0].get("StartDate"));
    assertEquals("12:00 PM", events2[0].get("StartTime"));
    assertEquals("04/01/2021", events2[0].get("EndDate"));
    assertEquals("01:00 PM", events2[0].get("EndTime"));
    assertEquals("Location", events2[0].get("Location"));
    assertEquals("false", events2[0].get("IsPrivate"));
    assertEquals("false", events2[0].get("IsAllDay"));
    assertEquals("Description", events2[0].get("Description"));
  }

  @Test
  public void testEditSingleEventPrivate() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    singleCal.editSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "private",
            "true",
            false);
    final Map<String,String>[] events2 = singleCal.getAllEvents();
    assertEquals(1, events2.length);
    assertEquals("Event1", events2[0].get("Name"));
    assertEquals("04/01/2021", events2[0].get("StartDate"));
    assertEquals("12:00 PM", events2[0].get("StartTime"));
    assertEquals("04/01/2021", events2[0].get("EndDate"));
    assertEquals("01:00 PM", events2[0].get("EndTime"));
    assertEquals("Location", events2[0].get("Location"));
    assertEquals("true", events2[0].get("IsPrivate"));
    assertEquals("false", events2[0].get("IsAllDay"));
    assertEquals("Description", events2[0].get("Description"));
  }

  @Test
  public void testEditSingleEventDescription() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    singleCal.editSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "description",
            "this is a new description",
            false);
    final Map<String,String>[] events2 = singleCal.getAllEvents();

    assertEquals(1, events2.length);
    assertEquals("Event1", events2[0].get("Name"));
    assertEquals("04/01/2021", events2[0].get("StartDate"));
    assertEquals("12:00 PM", events2[0].get("StartTime"));
    assertEquals("04/01/2021", events2[0].get("EndDate"));
    assertEquals("01:00 PM", events2[0].get("EndTime"));
    assertEquals("Location", events2[0].get("Location"));
    assertEquals("false", events2[0].get("IsPrivate"));
    assertEquals("false", events2[0].get("IsAllDay"));
    assertEquals("this is a new description", events2[0].get("Description"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditSingleEventInvalidProperty() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    try {
      singleCal.editSingleEvent("Event1",
              "2021-04-01T12:00",
              "2021-04-01T13:00",
              "invalidProperty",
              "this is a new description",
              false);
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid property name: invalidProperty", e.getMessage());
      final Map<String,String>[] events2 = singleCal.getAllEvents();

      assertEquals(1, events2.length);
      assertEquals("Event1", events2[0].get("Name"));
      assertEquals("04/01/2021", events2[0].get("StartDate"));
      assertEquals("12:00 PM", events2[0].get("StartTime"));
      assertEquals("04/01/2021", events2[0].get("EndDate"));
      assertEquals("01:00 PM", events2[0].get("EndTime"));
      assertEquals("Location", events2[0].get("Location"));
      assertEquals("false", events2[0].get("IsPrivate"));
      assertEquals("false", events2[0].get("IsAllDay"));
      assertEquals("Description", events2[0].get("Description"));
      throw e;
    }
  }

  @Test
  public void testEditSingleEventStartDateTime() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    final Map<String,String>[] events = singleCal
            .getEventsRange("2021-04-01T12:00", "2021-04-01T13:00");
    assertEquals(1, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2021-04-01T12:00", events[0].get("StartDateTime"));
    assertEquals("2021-04-01T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));
    singleCal.editSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "startDateTime",
            "2021-04-01T12:01",
            false);
    final Map<String,String>[] events2 = singleCal
            .getEventsRange("2021-04-01T12:00", "2021-04-01T13:00");
    assertEquals(1, events2.length);
    assertEquals("Event1", events2[0].get("Name"));
    assertEquals("2021-04-01T12:01", events2[0].get("StartDateTime"));
    assertEquals("2021-04-01T13:00", events2[0].get("EndDateTime"));
    assertEquals("Location", events2[0].get("Location"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditSingleEventStartDateTimeInvalid() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    final Map<String,String>[] events = singleCal
            .getEventsRange("2021-04-01T12:00", "2021-04-01T13:00");
    assertEquals(1, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2021-04-01T12:00", events[0].get("StartDateTime"));
    assertEquals("2021-04-01T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));

    try {
      singleCal.editSingleEvent("Event1",
              "2021-04-01T12:00",
              "2021-04-01T13:00",
              "startDateTime",
              "2021-04-01T14:00",  // invalid startDateTime
              false);
    } catch (IllegalArgumentException e) {
      assertEquals("Start date time cannot be after end date time", e.getMessage());
      final Map<String,String>[] events2 = singleCal
              .getEventsRange("2021-04-01T12:00", "2021-04-01T13:00");
      assertEquals(1, events2.length);
      assertEquals("Event1", events2[0].get("Name"));
      assertEquals("2021-04-01T12:00", events2[0].get("StartDateTime"));
      assertEquals("2021-04-01T13:00", events2[0].get("EndDateTime"));
      assertEquals("Location", events2[0].get("Location"));
      throw e;
    }
  }

  @Test
  public void testEditSingleEventEndDateTime() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    final Map<String,String>[] events = singleCal
            .getEventsRange("2021-04-01T12:00", "2021-04-01T13:00");
    assertEquals(1, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2021-04-01T12:00", events[0].get("StartDateTime"));
    assertEquals("2021-04-01T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));
    singleCal.editSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "endDateTime",
            "2021-04-01T13:01",
            false);
    final Map<String,String>[] events2 = singleCal
            .getEventsRange("2021-04-01T12:00", "2021-04-01T13:00");
    assertEquals(1, events2.length);
    assertEquals("Event1", events2[0].get("Name"));
    assertEquals("2021-04-01T12:00", events2[0].get("StartDateTime"));
    assertEquals("2021-04-01T13:01", events2[0].get("EndDateTime"));
    assertEquals("Location", events2[0].get("Location"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditSingleEventEndDateTimeInvalid() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    final Map<String,String>[] events = singleCal
            .getEventsRange("2021-04-01T12:00", "2021-04-01T13:00");
    assertEquals(1, events.length);

    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2021-04-01T12:00", events[0].get("StartDateTime"));
    assertEquals("2021-04-01T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));

    try {
      singleCal.editSingleEvent("Event1",
              "2021-04-01T12:00",
              "2021-04-01T13:00",
              "endDateTime",
              "2021-04-01T11:00", // invalid endDateTime
              false);
    } catch (IllegalArgumentException e) {
      assertEquals("End date time cannot be before start date time", e.getMessage());
      final Map<String,String>[] events2 = singleCal
              .getEventsRange("2021-04-01T12:00", "2021-04-01T13:00");
      assertEquals(1, events2.length);
      assertEquals("Event1", events2[0].get("Name"));
      assertEquals("2021-04-01T12:00", events2[0].get("StartDateTime"));
      assertEquals("2021-04-01T13:00", events2[0].get("EndDateTime"));
      assertEquals("Location", events2[0].get("Location"));
      throw e;
    }
  }

  @Test
  public void testGetStatusOnDateTimeSingle() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    singleCal.addSingleEvent("Event2",
            "2021-04-01T14:00",
            "2021-04-01T15:00",
            "Description",
            "Location",
            false,
            false);
    singleCal.addSingleEvent("Event3",
            "2021-04-01T16:00",
            "2021-04-01T17:00",
            "Description",
            "Location",
            false,
            false);
    final String status = singleCal.getStatusOnDateTime("2021-04-01T13:30");
    assertEquals("Available", status);
    final String status2 = singleCal.getStatusOnDateTime("2021-04-01T14:30");
    assertEquals("Busy", status2);
  }

  @Test
  public void testGetStatusOnDateTimeRecurringAllDay() {
    singleCal.addRecurringEventUntil("Event1",
            "2025-03-09T12:00",
            "",
            "Description",
            "Location",
            "MTW",
            "2025-03-15T23:00",
            false,
            true);
    final String status = singleCal.getStatusOnDateTime("2025-03-13T13:30");
    assertEquals("Available", status);
    final String status2 = singleCal.getStatusOnDateTime("2025-03-10T14:30");
    assertEquals("Busy", status2);
  }

  @Test
  public void testGetStatusOnDateTimeRecAndSingle() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    singleCal.addRecurringAllDayEventUntil("Event2",
            "2025-03-09T12:00",
            "Description",
            "Location",
            "MTW",
            "2025-03-15T23:00",
            false,
            true);
    final String status = singleCal.getStatusOnDateTime("2021-04-01T12:30");
    assertEquals("Busy", status);
    final String status2 = singleCal.getStatusOnDateTime("2025-03-13T13:30");
    assertEquals("Available", status2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetStatusOnDateTimeInvalidStartDateTime1() {
    try {
      singleCal.getStatusOnDateTime("");
    } catch (IllegalArgumentException e) {
      assertEquals("Start date time cannot be empty", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetStatusOnDateTimeInvalidStartDateTime2() {
    try {
      singleCal.getStatusOnDateTime(null);
    } catch (IllegalArgumentException e) {
      assertEquals("Start date time cannot be empty", e.getMessage());
      throw e;
    }
  }

  @Test
  public void testGetStatusRange() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    final String status = singleCal
            .getStatusInRange("2021-03-01T12:00", "2021-04-03T12:00");
    assertEquals("Busy", status);
  }

  @Test
  public void testGetStatusRangeAvailable() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    final String status = singleCal
            .getStatusInRange("2021-03-01T12:00", "2021-03-03T12:00");
    assertEquals("Available", status);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetStatusRangeInvalidStartDateTime1() {
    try {
      singleCal.getStatusInRange("", "2021-04-01T12:00");
    } catch (IllegalArgumentException e) {
      assertEquals("Start date time cannot be empty", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetStatusRangeInvalidStartDateTime2() {
    try {
      singleCal.getStatusInRange(null, "2021-04-01T12:00");
    } catch (IllegalArgumentException e) {
      assertEquals("Start date time cannot be empty", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetStatusRangeInvalidEndDateTime1() {
    try {
      singleCal.getStatusInRange("2021-04-01T12:00", "");
    } catch (IllegalArgumentException e) {
      assertEquals("End date time cannot be empty", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetStatusRangeInvalidEndDateTime2() {
    try {
      singleCal.getStatusInRange("2021-04-01T12:00", null);
    } catch (IllegalArgumentException e) {
      assertEquals("End date time cannot be empty", e.getMessage());
      throw e;
    }
  }

  @Test
  public void testGetEvents() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    singleCal.addSingleEvent("Event2",
            "2021-04-01T14:00",
            "2021-04-01T15:00",
            "Description",
            "Location",
            false,
            false);
    singleCal.addSingleEvent("Event3",
            "2021-04-01T16:00",
            "2021-04-01T17:00",
            "Description",
            "Location",
            false,
            false);
    final Map<String,String>[] events = singleCal
            .getEventsRange("2021-04-01T12:00", "2021-04-01T17:00");
    assertEquals(3, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2021-04-01T12:00", events[0].get("StartDateTime"));
    assertEquals("2021-04-01T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));
    assertEquals("Event2", events[1].get("Name"));
    assertEquals("2021-04-01T14:00", events[1].get("StartDateTime"));
    assertEquals("2021-04-01T15:00", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));
    assertEquals("Event3", events[2].get("Name"));
    assertEquals("2021-04-01T16:00", events[2].get("StartDateTime"));
    assertEquals("2021-04-01T17:00", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));


    final Map<String,String>[] events2 = singleCal
            .getEventsRange("2021-04-01T12:00", "2021-04-01T15:00");
    assertEquals(2, events2.length); // only 2 events

    final Map<String,String>[] events3 = singleCal
            .getEventsRange("2021-04-01T12:00", "2021-04-01T13:59");
    assertEquals(1, events3.length); // only 1 event

    final Map<String,String>[] events4 = singleCal
            .getEventsRange("2021-04-01T11:00", "2021-04-01T11:30");
    assertEquals(0, events4.length); // no events
  }

  @Test
  public void testGetEventsAllDay() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    singleCal.addSingleEvent("Event2",
            "2021-04-01T14:00",
            "2021-04-01T15:00",
            "Description",
            "Location",
            false,
            false);
    singleCal.addSingleEvent("Event3",
            "2021-04-01T16:00",
            "2021-04-01T17:00",
            "Description",
            "Location",
            false,
            false);
    final Map<String,String>[] events = singleCal.getEventsOnDate("2021-04-01T12:00");
    assertEquals(3, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2021-04-01T12:00", events[0].get("StartDateTime"));
    assertEquals("2021-04-01T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));
    assertEquals("Event2", events[1].get("Name"));
    assertEquals("2021-04-01T14:00", events[1].get("StartDateTime"));
    assertEquals("2021-04-01T15:00", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));
    assertEquals("Event3", events[2].get("Name"));
    assertEquals("2021-04-01T16:00", events[2].get("StartDateTime"));
    assertEquals("2021-04-01T17:00", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));
    final Map<String,String>[] events2 = singleCal.getEventsOnDate("2021-04-02T12:00");
    assertEquals(0, events2.length); // no events on this day
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetEventsInvalidDateRange() {
    try {
      singleCal.getEventsRange("2021-04-01T12:00", "2021-04-01T11:00");
    } catch (IllegalArgumentException e) {
      assertEquals("End time must be after start time", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetEventsInvalidDateRange2() {
    try {
      singleCal.getEventsRange("", "2021-04-01T11:00");
    } catch (IllegalArgumentException e) {
      assertEquals("Start and end date time cannot be empty", e.getMessage());
      throw e;
    }
  }

  @Test
  public void testGetAllEvents() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    singleCal.addSingleEvent("Event2",
            "2021-04-01T14:00",
            "2021-04-01T15:00",
            "Description",
            "Location",
            false,
            false);
    singleCal.addSingleEvent("Event3",
            "2021-04-01T16:00",
            "2021-04-01T17:00",
            "Description",
            "Location",
            false,
            false);
    final Map<String,String>[] result = singleCal.getAllEvents();
    assertEquals(3, result.length);
  }

  @Test
  public void testEditRecAll() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            false,
            true); //this will create form 4-10 to 4-17 only because 4-8 is Saturday

    final Map<String,String>[] events1 = singleCal.getEventsOnDate("2025-03-08");
    assertEquals(0, events1.length);
    final Map<String,String>[] events2 = singleCal
            .getEventsRange("2025-03-10T00:00", "2025-03-17T23:00");
    assertEquals(6, events2.length);

    singleCal.editMultipleEventsAll(
            "Event1",
            "location",
            "New Location",
            false);
    final Map<String,String>[] events = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-18T12:00");
    assertEquals(6, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events[0].get("EndDateTime"));
    assertEquals("New Location", events[0].get("Location"));
    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-11T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-11T13:00", events[1].get("EndDateTime"));
    assertEquals("New Location", events[1].get("Location"));
    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-12T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events[2].get("EndDateTime"));
    assertEquals("New Location", events[2].get("Location"));
    assertEquals("Event1", events[3].get("Name"));
    assertEquals("2025-03-13T12:00", events[3].get("StartDateTime"));
    assertEquals("2025-03-13T13:00", events[3].get("EndDateTime"));
    assertEquals("New Location", events[3].get("Location"));
    assertEquals("Event1", events[4].get("Name"));
    assertEquals("2025-03-14T12:00", events[4].get("StartDateTime"));
    assertEquals("2025-03-14T13:00", events[4].get("EndDateTime"));
    assertEquals("New Location", events[4].get("Location"));
    assertEquals("Event1", events[5].get("Name"));
    assertEquals("2025-03-17T12:00", events[5].get("StartDateTime"));
    assertEquals("2025-03-17T13:00", events[5].get("EndDateTime"));
    assertEquals("New Location", events[5].get("Location"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditRecAllInvalid() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            false,
            true);
    final Map<String,String>[] events1 = singleCal
            .getEventsRange("2025-03-8T00:00", "2025-03-30T00:00");
    assertEquals(6, events1.length);
    try {
      singleCal.editMultipleEventsAll(
              "Event2",
              "location",
              "New Location",
              false);
    } catch (IllegalArgumentException e) {
      assertEquals("Could not find any recurring event Event2", e.getMessage());
      throw e;
    }
  }

  @Test
  public void testEditRecurringFromDT() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            false,
            true);
    final Map<String,String>[] events1 = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-28T12:00");
    assertEquals(6, events1.length);
    singleCal.editMultipleEventsFollowing(
            "Event1",
            "2025-03-14T12:00", // should edit only last two events
            "location",
            "New Location",
            false);
    final Map<String,String>[] events = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-18T12:00");
    assertEquals(6, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));
    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-11T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-11T13:00", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));
    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-12T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));
    assertEquals("Event1", events[3].get("Name"));
    assertEquals("2025-03-13T12:00", events[3].get("StartDateTime"));
    assertEquals("2025-03-13T13:00", events[3].get("EndDateTime"));
    assertEquals("Location", events[3].get("Location"));
    assertEquals("Event1", events[4].get("Name"));
    assertEquals("2025-03-14T12:00", events[4].get("StartDateTime"));
    assertEquals("2025-03-14T13:00", events[4].get("EndDateTime"));
    assertEquals("New Location", events[4].get("Location"));
    assertEquals("Event1", events[5].get("Name"));
    assertEquals("2025-03-17T12:00", events[5].get("StartDateTime"));
    assertEquals("2025-03-17T13:00", events[5].get("EndDateTime"));
    assertEquals("New Location", events[5].get("Location"));
  }

  @Test
  public void testEditRecurringSingleOccurrence() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            false,
            true);
    final Map<String,String>[] events1 = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-18T12:00");
    assertEquals(6, events1.length);
    singleCal.editSingleEvent(
            "Event1",
            "2025-03-12T12:00", // should edit only one event
            "2025-03-12T13:00",
            "name",
            "NewEventName",
            false);
    final Map<String,String>[] events = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-18T12:00");
    assertEquals(6, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));
    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-11T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-11T13:00", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));
    assertEquals("NewEventName", events[2].get("Name")); // only one edited
    assertEquals("2025-03-12T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));
    assertEquals("Event1", events[3].get("Name"));
    assertEquals("2025-03-13T12:00", events[3].get("StartDateTime"));
    assertEquals("2025-03-13T13:00", events[3].get("EndDateTime"));
    assertEquals("Location", events[3].get("Location"));
    assertEquals("Event1", events[4].get("Name"));
    assertEquals("2025-03-14T12:00", events[4].get("StartDateTime"));
    assertEquals("2025-03-14T13:00", events[4].get("EndDateTime"));
    assertEquals("Location", events[4].get("Location"));
    assertEquals("Event1", events[5].get("Name"));
    assertEquals("2025-03-17T12:00", events[5].get("StartDateTime"));
    assertEquals("2025-03-17T13:00", events[5].get("EndDateTime"));
    assertEquals("Location", events[5].get("Location"));
  }

  @Test
  public void testEditRecurringSingleOccurrenceThenEditAll() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            false,
            true);
    final Map<String,String>[] events1 = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-28T12:00");
    assertEquals(6, events1.length);

    singleCal.editSingleEvent(
            "Event1",
            "2025-03-12T12:00", // should edit only one event
            "2025-03-12T13:00",
            "location",
            "NewEventLocation",
            false);
    final Map<String,String>[] events = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-18T12:00");
    assertEquals(6, events.length);

    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-11T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-11T13:00", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-12T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events[2].get("EndDateTime"));
    assertEquals("NewEventLocation", events[2].get("Location"));

    assertEquals("Event1", events[3].get("Name"));
    assertEquals("2025-03-13T12:00", events[3].get("StartDateTime"));
    assertEquals("2025-03-13T13:00", events[3].get("EndDateTime"));
    assertEquals("Location", events[3].get("Location"));

    assertEquals("Event1", events[4].get("Name"));
    assertEquals("2025-03-14T12:00", events[4].get("StartDateTime"));
    assertEquals("2025-03-14T13:00", events[4].get("EndDateTime"));
    assertEquals("Location", events[4].get("Location"));

    assertEquals("Event1", events[5].get("Name"));
    assertEquals("2025-03-17T12:00", events[5].get("StartDateTime"));
    assertEquals("2025-03-17T13:00", events[5].get("EndDateTime"));
    assertEquals("Location", events[5].get("Location"));

    singleCal.editMultipleEventsAll(
            "Event1",
            "location",
            "Final Location",
            false);
    final Map<String,String>[] events2 = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-18T12:00");
    assertEquals(6, events2.length);

    assertEquals("Event1", events2[0].get("Name"));
    assertEquals("2025-03-10T12:00", events2[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events2[0].get("EndDateTime"));
    assertEquals("Final Location", events2[0].get("Location"));

    assertEquals("Event1", events2[1].get("Name"));
    assertEquals("2025-03-11T12:00", events2[1].get("StartDateTime"));
    assertEquals("2025-03-11T13:00", events2[1].get("EndDateTime"));
    assertEquals("Final Location", events2[1].get("Location"));

    assertEquals("Event1", events2[2].get("Name"));
    assertEquals("2025-03-12T12:00", events2[2].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events2[2].get("EndDateTime"));
    assertEquals("Final Location", events2[2].get("Location"));

    assertEquals("Event1", events2[3].get("Name"));
    assertEquals("2025-03-13T12:00", events2[3].get("StartDateTime"));
    assertEquals("2025-03-13T13:00", events2[3].get("EndDateTime"));
    assertEquals("Final Location", events2[3].get("Location"));

    assertEquals("Event1", events2[4].get("Name"));
    assertEquals("2025-03-14T12:00", events2[4].get("StartDateTime"));
    assertEquals("2025-03-14T13:00", events2[4].get("EndDateTime"));
    assertEquals("Final Location", events2[4].get("Location"));

    assertEquals("Event1", events2[5].get("Name"));
    assertEquals("2025-03-17T12:00", events2[5].get("StartDateTime"));
    assertEquals("2025-03-17T13:00", events2[5].get("EndDateTime"));
    assertEquals("Final Location", events2[5].get("Location"));
  }

  @Test
  public void testEditRecurringFromDTThenEditAll() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            false,
            true);
    final Map<String,String>[] events1 = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-28T12:00");
    assertEquals(6, events1.length);
    singleCal.editMultipleEventsFollowing(
            "Event1",
            "2025-03-14T12:00", // should edit only the last two events
            "location",
            "New Location",
            false);
    final Map<String,String>[] events = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-18T12:00");
    assertEquals(6, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-11T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-11T13:00", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-12T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));

    assertEquals("Event1", events[3].get("Name"));
    assertEquals("2025-03-13T12:00", events[3].get("StartDateTime"));
    assertEquals("2025-03-13T13:00", events[3].get("EndDateTime"));
    assertEquals("Location", events[3].get("Location"));

    assertEquals("Event1", events[4].get("Name"));
    assertEquals("2025-03-14T12:00", events[4].get("StartDateTime"));
    assertEquals("2025-03-14T13:00", events[4].get("EndDateTime"));
    assertEquals("New Location", events[4].get("Location"));

    assertEquals("Event1", events[5].get("Name"));
    assertEquals("2025-03-17T12:00", events[5].get("StartDateTime"));
    assertEquals("2025-03-17T13:00", events[5].get("EndDateTime"));
    assertEquals("New Location", events[5].get("Location"));
    singleCal.editMultipleEventsAll(
            "Event1",
            "location",
            "Final Location",
            false);
    final Map<String,String>[] events2 = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-18T12:00");
    assertEquals(6, events2.length);

    assertEquals("Event1", events2[0].get("Name"));
    assertEquals("2025-03-10T12:00", events2[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events2[0].get("EndDateTime"));
    assertEquals("Final Location", events2[0].get("Location"));

    assertEquals("Event1", events2[1].get("Name"));
    assertEquals("2025-03-11T12:00", events2[1].get("StartDateTime"));
    assertEquals("2025-03-11T13:00", events2[1].get("EndDateTime"));
    assertEquals("Final Location", events2[1].get("Location"));

    assertEquals("Event1", events2[2].get("Name"));
    assertEquals("2025-03-12T12:00", events2[2].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events2[2].get("EndDateTime"));
    assertEquals("Final Location", events2[2].get("Location"));

    assertEquals("Event1", events2[3].get("Name"));
    assertEquals("2025-03-13T12:00", events2[3].get("StartDateTime"));
    assertEquals("2025-03-13T13:00", events2[3].get("EndDateTime"));
    assertEquals("Final Location", events2[3].get("Location"));

    assertEquals("Event1", events2[4].get("Name"));
    assertEquals("2025-03-14T12:00", events2[4].get("StartDateTime"));
    assertEquals("2025-03-14T13:00", events2[4].get("EndDateTime"));
    assertEquals("Final Location", events2[4].get("Location"));

    assertEquals("Event1", events2[5].get("Name"));
    assertEquals("2025-03-17T12:00", events2[5].get("StartDateTime"));
    assertEquals("2025-03-17T13:00", events2[5].get("EndDateTime"));
    assertEquals("Final Location", events2[5].get("Location"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditRecToConflictWithSingle() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    singleCal.addRecurringEventUntil("Event2",
            "2021-04-01T14:00",
            "2021-04-01T14:30",
            "Description",
            "Location",
            "MTWRF",
            "2021-04-02T23:00",
            false,
            true);
    final Map<String, String>[] events1 = singleCal
            .getEventsRange("2021-04-01T10:00", "2021-04-11T13:00");
    try {
      singleCal.editMultipleEventsAll("Event2",
              "startDateTime",
              "2021-04-01T12:30",
              true);
    } catch (IllegalArgumentException e) {
      assertEquals("Event conflicts with existing event", e.getMessage());
      throw e;
    }
  }

  @Test
  public void testEditRecWhenSingleExists() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    singleCal.addRecurringEventUntil("Event1",
            "2021-04-01T14:00",
            "2021-04-01T14:30",
            "Description",
            "Location",
            "MTWRF",
            "2021-04-02T23:00",
            false,
            true);
    final Map<String, String>[] events1 = singleCal
            .getEventsRange("2021-04-01T10:00", "2021-04-11T13:00");
    assertEquals(3, events1.length);
    singleCal.editMultipleEventsFollowing("Event1",
            "2021-04-02T14:00",
            "startDateTime",
            "2021-04-02T14:15",
            false);
    final Map<String, String>[] events = singleCal
            .getEventsRange("2021-04-01T10:00", "2021-04-11T13:00");
    assertEquals(3, events.length);

    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2021-04-01T12:00", events[0].get("StartDateTime"));
    assertEquals("2021-04-01T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2021-04-01T14:00", events[1].get("StartDateTime"));
    assertEquals("2021-04-01T14:30", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2021-04-02T14:15", events[2].get("StartDateTime"));
    assertEquals("2021-04-02T14:30", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));
  }

  @Test
  public void testEditMultipleBeforeRecStart() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    singleCal.addRecurringEventUntil("Event1",
            "2021-04-01T14:00",
            "2021-04-01T14:30",
            "Description",
            "Location",
            "MTWRF",
            "2021-04-02T23:00",
            false,
            true);
    final Map<String, String>[] events1 = singleCal
            .getEventsRange("2021-04-01T10:00", "2021-04-11T13:00");
    assertEquals(3, events1.length);
    singleCal.editMultipleEventsFollowing("Event1",
            "2021-04-02T10:00",
            "startDateTime",
            "2021-04-02T14:15",
            false);
    final Map<String, String>[] events = singleCal
            .getEventsRange("2021-04-01T10:00", "2021-04-11T13:00");
    assertEquals(3, events.length);

    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2021-04-01T12:00", events[0].get("StartDateTime"));
    assertEquals("2021-04-01T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2021-04-01T14:00", events[1].get("StartDateTime"));
    assertEquals("2021-04-01T14:30", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2021-04-02T14:15", events[2].get("StartDateTime"));
    assertEquals("2021-04-02T14:30", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));
  }

  @Test
  public void testEditMultipleComplexParameterWithSingleAndRec() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    singleCal.addRecurringEventUntil("Event1",
            "2021-04-01T14:00",
            "2021-04-01T14:30",
            "Description",
            "Location",
            "MTWRF",
            "2021-04-02T23:00",
            false,
            true);
    final Map<String, String>[] events1 = singleCal
            .getEventsRange("2021-04-01T10:00", "2021-04-11T13:00");
    assertEquals(3, events1.length);
    singleCal.editMultipleEventsFollowing("Event1",
            "2021-04-01T10:00",
            "startDateTime",
            "2021-04-01T14:15",
            false);
    final Map<String, String>[] events = singleCal
            .getEventsRange("2021-04-01T10:00", "2021-04-11T13:00");
    assertEquals(3, events.length);

    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2021-04-01T12:00", events[0].get("StartDateTime"));
    assertEquals("2021-04-01T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2021-04-01T14:15", events[1].get("StartDateTime"));
    assertEquals("2021-04-01T14:30", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2021-04-02T14:15", events[2].get("StartDateTime"));
    assertEquals("2021-04-02T14:30", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));
  }

  @Test
  public void testEditMultipleSimpleParameterWithSingleAndRec() {
    singleCal.addSingleEvent("Event1",
            "2021-04-01T12:00",
            "2021-04-01T13:00",
            "Description",
            "Location",
            false,
            false);
    singleCal.addRecurringEventUntil("Event1",
            "2021-04-01T14:00",
            "2021-04-01T14:30",
            "Description",
            "Location",
            "MTWRF",
            "2021-04-02T23:00",
            false,
            true);
    final Map<String, String>[] events1 = singleCal
            .getEventsRange("2021-04-01T10:00", "2021-04-11T13:00");
    assertEquals(3, events1.length);
    singleCal.editMultipleEventsFollowing("Event1",
            "2021-03-31T10:00",
            "location",
            "NewLocation",
            false);
    final Map<String, String>[] events = singleCal
            .getEventsRange("2021-03-31T10:00", "2021-04-11T13:00");
    assertEquals(3, events.length);

    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2021-04-01T12:00", events[0].get("StartDateTime"));
    assertEquals("2021-04-01T13:00", events[0].get("EndDateTime"));
    assertEquals("NewLocation", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2021-04-01T14:00", events[1].get("StartDateTime"));
    assertEquals("2021-04-01T14:30", events[1].get("EndDateTime"));
    assertEquals("NewLocation", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2021-04-02T14:00", events[2].get("StartDateTime"));
    assertEquals("2021-04-02T14:30", events[2].get("EndDateTime"));
    assertEquals("NewLocation", events[2].get("Location"));
  }

  @Test
  public void testEditRecurringWeekDaysForAllEvents() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            false,
            true);
    final Map<String, String>[] events1 = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-28T12:00");
    assertEquals(6, events1.length);

    singleCal.editMultipleEventsAll(
            "Event1",
            "weekDays",
            "MWF",
            false);
    final Map<String, String>[] events = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-18T12:00");
    assertEquals(4, events.length);

    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-12T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-14T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-14T13:00", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));

    assertEquals("Event1", events[3].get("Name"));
    assertEquals("2025-03-17T12:00", events[3].get("StartDateTime"));
    assertEquals("2025-03-17T13:00", events[3].get("EndDateTime"));
    assertEquals("Location", events[3].get("Location"));
  }

  @Test
  public void testEditFrequencyForAllEvents() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            false,
            true);
    final Map<String, String>[] events1 = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-28T12:00");
    assertEquals(6, events1.length);

    singleCal.editMultipleEventsAll(
            "Event1",
            "frequency",
            "8",
            false);
    final Map<String, String>[] events = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-20T12:00");
    assertEquals(8, events.length);

    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-11T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-11T13:00", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-12T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));

    assertEquals("Event1", events[3].get("Name"));
    assertEquals("2025-03-13T12:00", events[3].get("StartDateTime"));
    assertEquals("2025-03-13T13:00", events[3].get("EndDateTime"));
    assertEquals("Location", events[3].get("Location"));

    assertEquals("Event1", events[4].get("Name"));
    assertEquals("2025-03-14T12:00", events[4].get("StartDateTime"));
    assertEquals("2025-03-14T13:00", events[4].get("EndDateTime"));
    assertEquals("Location", events[4].get("Location"));

    assertEquals("Event1", events[5].get("Name"));
    assertEquals("2025-03-17T12:00", events[5].get("StartDateTime"));
    assertEquals("2025-03-17T13:00", events[5].get("EndDateTime"));
    assertEquals("Location", events[5].get("Location"));

    assertEquals("Event1", events[6].get("Name"));
    assertEquals("2025-03-18T12:00", events[6].get("StartDateTime"));
    assertEquals("2025-03-18T13:00", events[6].get("EndDateTime"));
    assertEquals("Location", events[6].get("Location"));

    assertEquals("Event1", events[7].get("Name"));
    assertEquals("2025-03-19T12:00", events[7].get("StartDateTime"));
    assertEquals("2025-03-19T13:00", events[7].get("EndDateTime"));
    assertEquals("Location", events[7].get("Location"));


  }

  @Test
  public void testEditRecurringUntilDateForAllEvents() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            false,
            true);
    final Map<String, String>[] events1 = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-28T12:00");
    assertEquals(6, events1.length);

    singleCal.editMultipleEventsAll(
            "Event1",
            "untilDateTime",
            "2025-03-19T23:00",
            false);
    final Map<String, String>[] events = singleCal
            .getEventsRange("2025-03-06T12:00", "2025-03-20T12:00");
    assertEquals(8, events.length);

    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-11T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-11T13:00", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-12T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));

    assertEquals("Event1", events[3].get("Name"));
    assertEquals("2025-03-13T12:00", events[3].get("StartDateTime"));
    assertEquals("2025-03-13T13:00", events[3].get("EndDateTime"));
    assertEquals("Location", events[3].get("Location"));

    assertEquals("Event1", events[4].get("Name"));
    assertEquals("2025-03-14T12:00", events[4].get("StartDateTime"));
    assertEquals("2025-03-14T13:00", events[4].get("EndDateTime"));
    assertEquals("Location", events[4].get("Location"));

    assertEquals("Event1", events[5].get("Name"));
    assertEquals("2025-03-17T12:00", events[5].get("StartDateTime"));
    assertEquals("2025-03-17T13:00", events[5].get("EndDateTime"));
    assertEquals("Location", events[5].get("Location"));

    assertEquals("Event1", events[6].get("Name"));
    assertEquals("2025-03-18T12:00", events[6].get("StartDateTime"));
    assertEquals("2025-03-18T13:00", events[6].get("EndDateTime"));
    assertEquals("Location", events[6].get("Location"));

    assertEquals("Event1", events[7].get("Name"));
    assertEquals("2025-03-19T12:00", events[7].get("StartDateTime"));
    assertEquals("2025-03-19T13:00", events[7].get("EndDateTime"));
    assertEquals("Location", events[7].get("Location"));
  }

  @Test
  public void testEditRecurringStartDateForAllEvents() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            false,
            true);
    final Map<String, String>[] events1 = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-28T12:00");
    assertEquals(6, events1.length);

    singleCal.editMultipleEventsAll(
            "Event1",
            "startDateTime",
            "2025-03-06T12:00",
            false);
    final Map<String, String>[] events = singleCal
            .getEventsRange("2025-03-06T12:00", "2025-03-20T12:00");
    assertEquals(8, events.length);

    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-06T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-06T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-07T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-07T13:00", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-10T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));

    assertEquals("Event1", events[3].get("Name"));
    assertEquals("2025-03-11T12:00", events[3].get("StartDateTime"));
    assertEquals("2025-03-11T13:00", events[3].get("EndDateTime"));
    assertEquals("Location", events[3].get("Location"));

    assertEquals("Event1", events[4].get("Name"));
    assertEquals("2025-03-12T12:00", events[4].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events[4].get("EndDateTime"));
    assertEquals("Location", events[4].get("Location"));

    assertEquals("Event1", events[5].get("Name"));
    assertEquals("2025-03-13T12:00", events[5].get("StartDateTime"));
    assertEquals("2025-03-13T13:00", events[5].get("EndDateTime"));
    assertEquals("Location", events[5].get("Location"));

    assertEquals("Event1", events[6].get("Name"));
    assertEquals("2025-03-14T12:00", events[6].get("StartDateTime"));
    assertEquals("2025-03-14T13:00", events[6].get("EndDateTime"));
    assertEquals("Location", events[6].get("Location"));

    assertEquals("Event1", events[7].get("Name"));
    assertEquals("2025-03-17T12:00", events[7].get("StartDateTime"));
    assertEquals("2025-03-17T13:00", events[7].get("EndDateTime"));
    assertEquals("Location", events[7].get("Location"));
  }

  @Test
  public void testEditRecurringEndDateForAllEvents() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            false,
            true);
    final Map<String, String>[] events1 = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-28T12:00");
    assertEquals(6, events1.length);

    singleCal.editMultipleEventsAll(
            "Event1",
            "endDateTime",
            "2025-03-19T14:00",
            false);
    final Map<String, String>[] events = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-20T12:00");
    assertEquals(6, events.length);

    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T14:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-11T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-11T14:00", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-12T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-12T14:00", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));

    assertEquals("Event1", events[3].get("Name"));
    assertEquals("2025-03-13T12:00", events[3].get("StartDateTime"));
    assertEquals("2025-03-13T14:00", events[3].get("EndDateTime"));
    assertEquals("Location", events[3].get("Location"));

    assertEquals("Event1", events[4].get("Name"));
    assertEquals("2025-03-14T12:00", events[4].get("StartDateTime"));
    assertEquals("2025-03-14T14:00", events[4].get("EndDateTime"));
    assertEquals("Location", events[4].get("Location"));

    assertEquals("Event1", events[5].get("Name"));
    assertEquals("2025-03-17T12:00", events[5].get("StartDateTime"));
    assertEquals("2025-03-17T14:00", events[5].get("EndDateTime"));
    assertEquals("Location", events[5].get("Location"));
  }

  @Test
  public void testEditRecurringEndDateForSomeEvents() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            false,
            true);
    final Map<String, String>[] events1 = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-28T12:00");
    assertEquals(6, events1.length);

    singleCal.editMultipleEventsFollowing(
            "Event1",
            "2025-03-14T12:00",
            "endDateTime",
            "2025-03-19T14:00",
            false);
    final Map<String, String>[] events = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-20T12:00");
    assertEquals(6, events.length);

    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-11T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-11T13:00", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-12T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));

    assertEquals("Event1", events[3].get("Name"));
    assertEquals("2025-03-13T12:00", events[3].get("StartDateTime"));
    assertEquals("2025-03-13T13:00", events[3].get("EndDateTime"));
    assertEquals("Location", events[3].get("Location"));

    assertEquals("Event1", events[4].get("Name"));
    assertEquals("2025-03-14T12:00", events[4].get("StartDateTime"));
    assertEquals("2025-03-14T14:00", events[4].get("EndDateTime"));
    assertEquals("Location", events[4].get("Location"));

    assertEquals("Event1", events[5].get("Name"));
    assertEquals("2025-03-17T12:00", events[5].get("StartDateTime"));
    assertEquals("2025-03-17T14:00", events[5].get("EndDateTime"));
    assertEquals("Location", events[5].get("Location"));
  }

  @Test
  public void testEditRecurringStartDateForSomeEvents() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            false,
            true);
    final Map<String, String>[] events1 = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-28T12:00");
    assertEquals(6, events1.length);

    singleCal.editMultipleEventsFollowing(
            "Event1",
            "2025-03-14T12:00",
            "startDateTime",
            "2025-03-14T11:00",
            false);
    final Map<String, String>[] events = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-20T12:00");
    assertEquals(6, events.length);

    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-11T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-11T13:00", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-12T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));

    assertEquals("Event1", events[3].get("Name"));
    assertEquals("2025-03-13T12:00", events[3].get("StartDateTime"));
    assertEquals("2025-03-13T13:00", events[3].get("EndDateTime"));
    assertEquals("Location", events[3].get("Location"));

    assertEquals("Event1", events[4].get("Name"));
    assertEquals("2025-03-14T11:00", events[4].get("StartDateTime"));
    assertEquals("2025-03-14T13:00", events[4].get("EndDateTime"));
    assertEquals("Location", events[4].get("Location"));

    assertEquals("Event1", events[5].get("Name"));
    assertEquals("2025-03-17T11:00", events[5].get("StartDateTime"));
    assertEquals("2025-03-17T13:00", events[5].get("EndDateTime"));
    assertEquals("Location", events[5].get("Location"));
  }

  @Test
  public void testEditRecurringWeekDaysForSomeEvents() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            false,
            true);
    final Map<String, String>[] events1 = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-28T12:00");
    assertEquals(6, events1.length);

    singleCal.editMultipleEventsFollowing(
            "Event1",
            "2025-03-14T12:00",
            "weekDays",
            "MWFSU",
            false);
    final Map<String, String>[] events = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-18T12:00");
    assertEquals(8, events.length);

    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-11T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-11T13:00", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-12T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));

    assertEquals("Event1", events[3].get("Name"));
    assertEquals("2025-03-13T12:00", events[3].get("StartDateTime"));
    assertEquals("2025-03-13T13:00", events[3].get("EndDateTime"));
    assertEquals("Location", events[3].get("Location"));

    assertEquals("Event1", events[4].get("Name"));
    assertEquals("2025-03-14T12:00", events[4].get("StartDateTime"));
    assertEquals("2025-03-14T13:00", events[4].get("EndDateTime"));
    assertEquals("Location", events[4].get("Location"));

    assertEquals("Event1", events[5].get("Name"));
    assertEquals("2025-03-15T12:00", events[5].get("StartDateTime"));
    assertEquals("2025-03-15T13:00", events[5].get("EndDateTime"));
    assertEquals("Location", events[5].get("Location"));

    assertEquals("Event1", events[6].get("Name"));
    assertEquals("2025-03-16T12:00", events[6].get("StartDateTime"));
    assertEquals("2025-03-16T13:00", events[6].get("EndDateTime"));
    assertEquals("Location", events[6].get("Location"));

    assertEquals("Event1", events[7].get("Name"));
    assertEquals("2025-03-17T12:00", events[7].get("StartDateTime"));
    assertEquals("2025-03-17T13:00", events[7].get("EndDateTime"));
    assertEquals("Location", events[7].get("Location"));
  }

  @Test
  public void testEditNoRecurringEvents() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            false,
            true);
    final Map<String, String>[] events1 = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-28T12:00");
    assertEquals(6, events1.length);

    try {
      singleCal.editMultipleEventsFollowing(
              "Event1",
              "2025-03-14T22:00",
              "weekDays",
              "MWFSU",
              false);
    } catch (IllegalArgumentException e) {
      fail("Should not throw exception");
    }
    final Map<String, String>[] events = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-18T12:00");
    assertEquals(6, events.length);

    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-11T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-11T13:00", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-12T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));

    assertEquals("Event1", events[3].get("Name"));
    assertEquals("2025-03-13T12:00", events[3].get("StartDateTime"));
    assertEquals("2025-03-13T13:00", events[3].get("EndDateTime"));
    assertEquals("Location", events[3].get("Location"));

    assertEquals("Event1", events[4].get("Name"));
    assertEquals("2025-03-14T12:00", events[4].get("StartDateTime"));
    assertEquals("2025-03-14T13:00", events[4].get("EndDateTime"));
    assertEquals("Location", events[4].get("Location"));

    assertEquals("Event1", events[5].get("Name"));
    assertEquals("2025-03-17T12:00", events[5].get("StartDateTime"));
    assertEquals("2025-03-17T13:00", events[5].get("EndDateTime"));
    assertEquals("Location", events[5].get("Location"));
  }

  @Test
  public void testEditLastRecurringEvents() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            false,
            true);
    final Map<String, String>[] events1 = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-28T12:00");
    assertEquals(6, events1.length);
    singleCal.editMultipleEventsFollowing(
            "Event1",
            "2025-03-17T12:00",
            "frequency",
            "2",
            false);
    final Map<String, String>[] events = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-19T12:00");
    assertEquals(7, events.length);

    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-11T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-11T13:00", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-12T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));

    assertEquals("Event1", events[3].get("Name"));
    assertEquals("2025-03-13T12:00", events[3].get("StartDateTime"));
    assertEquals("2025-03-13T13:00", events[3].get("EndDateTime"));
    assertEquals("Location", events[3].get("Location"));

    assertEquals("Event1", events[4].get("Name"));
    assertEquals("2025-03-14T12:00", events[4].get("StartDateTime"));
    assertEquals("2025-03-14T13:00", events[4].get("EndDateTime"));
    assertEquals("Location", events[4].get("Location"));

    assertEquals("Event1", events[5].get("Name"));
    assertEquals("2025-03-17T12:00", events[5].get("StartDateTime"));
    assertEquals("2025-03-17T13:00", events[5].get("EndDateTime"));
    assertEquals("Location", events[5].get("Location"));

    assertEquals("Event1", events[6].get("Name"));
    assertEquals("2025-03-18T12:00", events[6].get("StartDateTime"));
    assertEquals("2025-03-18T13:00", events[6].get("EndDateTime"));
    assertEquals("Location", events[6].get("Location"));
  }

  @Test
  public void testEditFirstRecurringEvents() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            false,
            true);
    final Map<String, String>[] events1 = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-28T12:00");
    assertEquals(6, events1.length);
    singleCal.editMultipleEventsFollowing(
            "Event1",
            "2025-03-10T12:00",
            "frequency",
            "7",
            false);
    final Map<String, String>[] events = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-19T12:00");
    assertEquals(7, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-11T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-11T13:00", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-12T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));

    assertEquals("Event1", events[3].get("Name"));
    assertEquals("2025-03-13T12:00", events[3].get("StartDateTime"));
    assertEquals("2025-03-13T13:00", events[3].get("EndDateTime"));
    assertEquals("Location", events[3].get("Location"));

    assertEquals("Event1", events[4].get("Name"));
    assertEquals("2025-03-14T12:00", events[4].get("StartDateTime"));
    assertEquals("2025-03-14T13:00", events[4].get("EndDateTime"));
    assertEquals("Location", events[4].get("Location"));

    assertEquals("Event1", events[5].get("Name"));
    assertEquals("2025-03-17T12:00", events[5].get("StartDateTime"));
    assertEquals("2025-03-17T13:00", events[5].get("EndDateTime"));
    assertEquals("Location", events[5].get("Location"));

    assertEquals("Event1", events[6].get("Name"));
    assertEquals("2025-03-18T12:00", events[6].get("StartDateTime"));
    assertEquals("2025-03-18T13:00", events[6].get("EndDateTime"));
    assertEquals("Location", events[6].get("Location"));

  }

  @Test
  public void testEditFirstSingleAndRecurringEvents() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            false,
            true);
    final Map<String, String>[] events1 = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-28T12:00");
    assertEquals(6, events1.length);
    singleCal.editSingleEvent(
            "Event1",
            "2025-03-10T12:00",
            "2025-03-10T13:00",
            "name",
            "Event2",
            false);

    Map<String, String>[] events = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-19T12:00");
    assertEquals(6, events.length);

    assertEquals("Event2", events[0].get("Name"));
    assertEquals("2025-03-10T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-11T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-11T13:00", events[1].get("EndDateTime"));
    assertEquals("Location", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-12T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events[2].get("EndDateTime"));
    assertEquals("Location", events[2].get("Location"));

    assertEquals("Event1", events[3].get("Name"));
    assertEquals("2025-03-13T12:00", events[3].get("StartDateTime"));
    assertEquals("2025-03-13T13:00", events[3].get("EndDateTime"));
    assertEquals("Location", events[3].get("Location"));

    assertEquals("Event1", events[4].get("Name"));
    assertEquals("2025-03-14T12:00", events[4].get("StartDateTime"));
    assertEquals("2025-03-14T13:00", events[4].get("EndDateTime"));
    assertEquals("Location", events[4].get("Location"));

    assertEquals("Event1", events[5].get("Name"));
    assertEquals("2025-03-17T12:00", events[5].get("StartDateTime"));
    assertEquals("2025-03-17T13:00", events[5].get("EndDateTime"));
    assertEquals("Location", events[5].get("Location"));


    singleCal.editMultipleEventsAll(
            "Event1",
            "location",
            "Boston",
            false);
    events = singleCal.getEventsRange("2025-03-08T12:00", "2025-03-19T12:00");
    assertEquals(6, events.length);

    assertEquals("Event2", events[0].get("Name"));
    assertEquals("2025-03-10T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events[0].get("EndDateTime"));
    assertEquals("Boston", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-11T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-11T13:00", events[1].get("EndDateTime"));
    assertEquals("Boston", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-12T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events[2].get("EndDateTime"));
    assertEquals("Boston", events[2].get("Location"));

    assertEquals("Event1", events[3].get("Name"));
    assertEquals("2025-03-13T12:00", events[3].get("StartDateTime"));
    assertEquals("2025-03-13T13:00", events[3].get("EndDateTime"));
    assertEquals("Boston", events[3].get("Location"));

    assertEquals("Event1", events[4].get("Name"));
    assertEquals("2025-03-14T12:00", events[4].get("StartDateTime"));
    assertEquals("2025-03-14T13:00", events[4].get("EndDateTime"));
    assertEquals("Boston", events[4].get("Location"));

    assertEquals("Event1", events[5].get("Name"));
    assertEquals("2025-03-17T12:00", events[5].get("StartDateTime"));
    assertEquals("2025-03-17T13:00", events[5].get("EndDateTime"));
    assertEquals("Boston", events[5].get("Location"));

    singleCal.editMultipleEventsAll(
            "Event1",
            "frequency",
            "7",
            false);
    events = singleCal.getEventsRange("2025-03-08T12:00", "2025-03-19T12:00");
    assertEquals(7, events.length);


  }

  @Test
  public void testEditFirstRecurringEventsDaysOfWeek() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            false,
            true);
    final Map<String, String>[] events1 = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-28T12:00");
    assertEquals(6, events1.length);

    singleCal.editMultipleEventsAll(
            "Event1",
            "location",
            "Boston",
            false);
    singleCal.editMultipleEventsAll(
            "Event1",
            "weekDays",
            "MWF",
            false);
    Map<String, String>[] events = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-19T12:00");
    assertEquals(4, events.length);

    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events[0].get("EndDateTime"));
    assertEquals("Boston", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-12T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events[1].get("EndDateTime"));
    assertEquals("Boston", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-14T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-14T13:00", events[2].get("EndDateTime"));
    assertEquals("Boston", events[2].get("Location"));

    assertEquals("Event1", events[3].get("Name"));
    assertEquals("2025-03-17T12:00", events[3].get("StartDateTime"));
    assertEquals("2025-03-17T13:00", events[3].get("EndDateTime"));
    assertEquals("Boston", events[3].get("Location"));

    singleCal.editMultipleEventsAll(
            "Event1",
            "frequency",
            "7",
            false);
    events = singleCal.getEventsRange("2025-03-08T12:00", "2025-03-25T12:00");
    assertEquals(7, events.length);

    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-03-10T12:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T13:00", events[0].get("EndDateTime"));
    assertEquals("Boston", events[0].get("Location"));

    assertEquals("Event1", events[1].get("Name"));
    assertEquals("2025-03-12T12:00", events[1].get("StartDateTime"));
    assertEquals("2025-03-12T13:00", events[1].get("EndDateTime"));
    assertEquals("Boston", events[1].get("Location"));

    assertEquals("Event1", events[2].get("Name"));
    assertEquals("2025-03-14T12:00", events[2].get("StartDateTime"));
    assertEquals("2025-03-14T13:00", events[2].get("EndDateTime"));
    assertEquals("Boston", events[2].get("Location"));

    assertEquals("Event1", events[3].get("Name"));
    assertEquals("2025-03-17T12:00", events[3].get("StartDateTime"));
    assertEquals("2025-03-17T13:00", events[3].get("EndDateTime"));
    assertEquals("Boston", events[3].get("Location"));

    assertEquals("Event1", events[4].get("Name"));
    assertEquals("2025-03-19T12:00", events[4].get("StartDateTime"));
    assertEquals("2025-03-19T13:00", events[4].get("EndDateTime"));
    assertEquals("Boston", events[4].get("Location"));

    assertEquals("Event1", events[5].get("Name"));
    assertEquals("2025-03-21T12:00", events[5].get("StartDateTime"));
    assertEquals("2025-03-21T13:00", events[5].get("EndDateTime"));
    assertEquals("Boston", events[5].get("Location"));

    assertEquals("Event1", events[6].get("Name"));
    assertEquals("2025-03-24T12:00", events[6].get("StartDateTime"));
    assertEquals("2025-03-24T13:00", events[6].get("EndDateTime"));
    assertEquals("Boston", events[6].get("Location"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditCausingConflicts() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            true,
            true);
    singleCal.addSingleEvent(
            "Event2",
            "2025-03-10T11:00",
            "2025-03-10T11:30",
            "Description",
            "Location",
            true,
            false);
    try {
      singleCal.editSingleEvent(
              "Event2",
              "2025-03-10T11:00",
              "2025-03-10T11:30",
              "endDateTime",
              "2025-03-10T12:10",
              true);
      fail("Should throw exception");
    } catch (IllegalArgumentException e) {
      assertEquals("Event conflicts with existing event", e.getMessage());
      Map<String, String>[] events = singleCal
              .getEventsRange("2025-03-08T12:00", "2025-03-25T12:00");
      assertEquals(7, events.length);
      assertEquals("Event2", events[0].get("Name"));
      assertEquals("2025-03-10T11:00", events[0].get("StartDateTime"));
      assertEquals("2025-03-10T11:30", events[0].get("EndDateTime"));
      assertEquals("Location", events[0].get("Location"));
      throw e;
    }
  }

  @Test
  public void testEditWithConflictsNoError() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            true,
            true);
    singleCal.addSingleEvent(
            "Event2",
            "2025-03-10T11:00",
            "2025-03-10T11:30",
            "Description",
            "Location",
            true,
            false);
    singleCal.editSingleEvent(
            "Event2",
            "2025-03-10T11:00",
            "2025-03-10T11:30",
            "endDateTime",
            "2025-03-10T12:10",
            false);
    Map<String, String>[] events = singleCal
            .getEventsRange("2025-03-08T12:00", "2025-03-25T12:00");
    assertEquals(7, events.length);
    assertEquals("Event2", events[0].get("Name"));
    assertEquals("2025-03-10T11:00", events[0].get("StartDateTime"));
    assertEquals("2025-03-10T12:10", events[0].get("EndDateTime"));
    assertEquals("Location", events[0].get("Location"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditMultipleCausingConflicts() {
    singleCal.addRecurringEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-17T23:00",
            false,
            true);
    singleCal.addSingleEvent(
            "Event2",
            "2025-03-10T11:00",
            "2025-03-10T11:30",
            "Description",
            "Location",
            false,
            false);
    try {
      singleCal.editMultipleEventsFollowing(
              "Event1",
              "2025-03-10",
              "startDateTime",
              "2025-03-10T11:20",
              true);
      fail("Should throw exception");
    } catch (IllegalArgumentException e) {
      assertEquals("Event conflicts with existing event", e.getMessage());
      Map<String, String>[] events = singleCal
              .getEventsRange("2025-03-08T12:00", "2025-03-25T12:00");
      assertEquals(7, events.length);
      assertEquals("Event2", events[0].get("Name"));
      assertEquals("2025-03-10T11:00", events[0].get("StartDateTime"));
      assertEquals("2025-03-10T11:30", events[0].get("EndDateTime"));
      assertEquals("Location", events[0].get("Location"));
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void addRecurringEventFrequencyAutoDecline() {
    singleCal.addRecurringEventFrequency(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T13:00",
            "Description",
            "Location",
            "MTWRF",
            3,
            false,
            true);
    try {
      singleCal.addRecurringEventFrequency(
              "Event1",
              "2025-03-08T12:00",
              "2025-03-08T13:00",
              "Description",
              "Location",
              "MTWRF",
              1,
              true,
              true);
    } catch (IllegalArgumentException e) {
      assertEquals("Conflict detected, Event not Created", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void addRecurringEventAllDayUntilAutoDecline() {
    singleCal.addRecurringAllDayEventUntil(
            "Event1",
            "2025-03-08T12:00",
            "Description",
            "Location",
            "MTWRF",
            "2025-03-18T12:00",
            false,
            true);
    try {
      singleCal.addRecurringAllDayEventUntil(
              "Event2",
              "2025-03-08T12:00",
              "Description",
              "Location",
              "MTWRF",
              "2025-03-18T12:00",
              true,
              true);
    } catch (IllegalArgumentException e) {
      assertEquals("Conflict detected, Event not Created", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void addRecurringEventAllDayFreqAutoDecline() {
    singleCal.addRecurringAllDayEventFrequency(
            "Event1",
            "2025-03-08T12:00",
            "Description",
            "Location",
            "MTWRF",
            3,
            false,
            true);
    try {
      singleCal.addRecurringAllDayEventFrequency(
              "Event2",
              "2025-03-08T12:00",
              "Description",
              "Location",
              "MTWRF",
              1,
              true,
              true);
    } catch (IllegalArgumentException e) {
      assertEquals("Conflict detected, Event not Created", e.getMessage());
      throw e;
    }
  }

  @Test
  public void testChangeStartToAllDay() {
    singleCal.addSingleEvent(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T23:59",
            "Description",
            "Location",
            false,
            true);
    singleCal.editSingleEvent(
            "Event1",
            "2025-03-08T12:00",
            "2025-03-08T23:59",
            "startDateTime",
            "2025-03-08T00:00",
            true);
    Map<String, String>[] events = singleCal.getAllEvents();
    assertEquals(1, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("03/08/2025", events[0].get("StartDate"));
    assertEquals("12:00 AM", events[0].get("StartTime"));
    assertEquals("03/08/2025", events[0].get("EndDate"));
    assertEquals("11:59 PM", events[0].get("EndTime"));
  }

  @Test
  public void testChangeEndToAllDay() {
    singleCal.addSingleEvent(
            "Event1",
            "2025-03-08T00:00",
            "2025-03-08T13:59",
            "Description",
            "Location",
            false,
            true);
    singleCal.editSingleEvent(
            "Event1",
            "2025-03-08T00:00",
            "2025-03-08T13:59",
            "endDateTime",
            "2025-03-08T23:59",
            true);
    Map<String, String>[] events = singleCal.getAllEvents();
    assertEquals(1, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("03/08/2025", events[0].get("StartDate"));
    assertEquals("12:00 AM", events[0].get("StartTime"));
    assertEquals("03/08/2025", events[0].get("EndDate"));
    assertEquals("11:59 PM", events[0].get("EndTime"));
  }

  @Test
  public void testGetExactEvent() {
    singleCal.addSingleEvent(
            "Event1",
            "2025-03-08T00:00",
            "2025-03-08T13:59",
            "Description",
            "Location",
            false,
            true);

    singleCal.addSingleEvent(
            "Event2",
            "2025-03-09T00:00",
            "2025-03-09T13:59",
            "Description2",
            "Location2",
            false,
            false);

    Map<String, String> result = singleCal.getExactEvent(
            "Event1",
            "2025-03-08T00:00");

    assertEquals("Event1", result.get("Name"));
    assertEquals("03/08/2025 12:00 AM", result.get("Start Date Time"));
    assertEquals("03/08/2025 01:59 PM", result.get("End Date Time"));
    assertEquals("Description", result.get("Description"));
    assertEquals("Location", result.get("Location"));
    assertEquals("true", result.get("Private"));
    assertEquals("false", result.get("All Day"));
  }

  @Test
  public void testGetExactEvent2() {
    singleCal.addSingleEvent(
            "Event1",
            "2025-03-08T00:00",
            "2025-03-08T13:59",
            "Description",
            "Location",
            false,
            true);

    singleCal.addSingleEventAllDay(
            "Event2",
            "2025-03-09T00:00",
            "Description 2",
            "Location 2",
            false,
            false);

    Map<String, String> result = singleCal.getExactEvent(
            "Event2",
            "2025-03-09T00:00");

    assertEquals("Event2", result.get("Name"));
    assertEquals("03/09/2025 12:00 AM", result.get("Start Date Time"));
    assertEquals("03/09/2025 11:59 PM", result.get("End Date Time"));
    assertEquals("Description 2", result.get("Description"));
    assertEquals("Location 2", result.get("Location"));
    assertEquals("false", result.get("Private"));
    assertEquals("true", result.get("All Day"));
  }

  @Test
  public void testGetExactEvent3() {
    singleCal.addSingleEvent(
            "Event1",
            "2025-03-08T00:00",
            "2025-03-08T13:59",
            "Description",
            "Location",
            false,
            true);

    singleCal.addSingleEventAllDay(
            "Event2",
            "2025-03-09T00:00",
            "Description2",
            "Location2",
            false,
            false);

    Map<String, String> result = singleCal.getExactEvent(
            "Event3",
            "2025-03-09T00:00");

    assertEquals(0, result.size());
  }
}
