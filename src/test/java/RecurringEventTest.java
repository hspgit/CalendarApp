import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static utils.DateTimeUtils.stringToZdt;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import model.calendarentry.CalendarEntry;
import model.calendarentry.RecurringEvent;

import org.junit.Test;

/**
 * Test class for RecurringEvent.
 */
public class RecurringEventTest {

  @Test
  public void isConflict() {

    ZonedDateTime start1 = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end1 = stringToZdt("2023-12-01T11:00:00", "UTC");
    RecurringEvent event1 = new RecurringEvent("Event 1", start1, end1, "Description 1",
            "Location 1", false, false, "MWF", 10);

    ZonedDateTime start2 = stringToZdt("2023-12-02T10:30:00", "UTC");
    ZonedDateTime end2 = stringToZdt("2023-12-02T11:30:00", "UTC");
    RecurringEvent event2 = new RecurringEvent("Event 2", start2, end2, "Description 2",
            "Location 2", false, false, "TR", 10);

    boolean conflict = event1.isConflict(event2);

    assertFalse(conflict);
  }

  @Test
  public void getEventDetailsNoEvent() {

    ZonedDateTime start = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end = stringToZdt("2023-12-01T11:00:00", "UTC");
    RecurringEvent event = new RecurringEvent("Event", start, end, "Description", "Location", false,
            false, "M", 5);
    ZonedDateTime until = stringToZdt("2023-12-21T22:00:00", "UTC");

    var details = event.getEventDetails(start.minusHours(1), until, true);

    assertFalse(details.isEmpty());
    assertEquals("Event", details.get(0).get("Name"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void editNoRecurringEvent() {
    ZonedDateTime start = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end = stringToZdt("2023-12-01T11:00:00", "UTC");
    RecurringEvent event = new RecurringEvent("Event", start, end, "Description", "Location", false,
            false, "UM", 1);

    ZonedDateTime checkStart = end.plusMonths(1);
    try {
      event.editRecurringEvent("Event", checkStart, "name", "Updated Event");
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("No such calendar entry found", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void editNoRecurringEvent2() {

    ZonedDateTime start = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end = stringToZdt("2023-12-01T11:00:00", "UTC");
    RecurringEvent event = new RecurringEvent("Event", start, end, "Description", "Location", false,
            false, "UM", 5);

    try {
      event.editRecurringEvent("Event2", null, "name", "Updated Event");
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("No such calendar entry found", e.getMessage());
      throw e;
    }
  }


  @Test
  public void matchesSingleOccurrence() {

    ZonedDateTime start = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end = stringToZdt("2023-12-01T11:00:00", "UTC");
    RecurringEvent event = new RecurringEvent("Event", start, end, "Description", "Location", false,
            false, "MF", 5);

    CalendarEntry match = event.matchesSingleOccurrence("Event", start, end);

    assertNotNull(match);
  }

  @Test
  public void matchesNoSingleEvent() {

    ZonedDateTime start = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end = stringToZdt("2023-12-01T11:00:00", "UTC");
    RecurringEvent event = new RecurringEvent("Event", start, end, "Description", "Location", false,
            false, "M", 5);

    CalendarEntry match = event.matchesSingleOccurrence("Event", start, end);

    assertNull(match);
  }

  @Test
  public void testIsEventStartWith() {
    ZonedDateTime start = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end = stringToZdt("2023-12-01T11:00:00", "UTC");
    RecurringEvent event = new RecurringEvent("Event", start, end, "Description", "Location", false,
            false, "MTWRFSU", 5);

    CalendarEntry entry = event.isEventStartWith("Event", start).getKey();
    assertNotNull(entry);

  }

  @Test
  public void testIsEventStartWithNameNotEqual() {
    ZonedDateTime start = stringToZdt("2025-03-22T10:00", "America/New_York");
    ZonedDateTime end = stringToZdt("2025-03-22T11:00", "America/New_York");
    RecurringEvent event = new RecurringEvent("Event", start, end, "Description", "Location", false,
            false, "MTWRFSU", 5);

    CalendarEntry entry = event.isEventStartWith("Event1", start).getKey();
    assertNull(entry);

  }

  @Test
  public void testIsEventStartWithTimeNotEqual() {
    ZonedDateTime start = stringToZdt("2025-03-22T10:00", "America/New_York");
    ZonedDateTime end = stringToZdt("2025-03-22T11:00", "America/New_York");
    RecurringEvent event = new RecurringEvent("Event", start, end, "Description", "Location", false,
            false, "MTWRFSU", 5);

    CalendarEntry entry = event.isEventStartWith("Event", start.plusHours(2)).getKey();
    assertNull(entry);
  }

  @Test
  public void testAddOffsetDays() {
    ZonedDateTime start = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end = stringToZdt("2023-12-01T11:00:00", "UTC");
    RecurringEvent event = new RecurringEvent("Event", start, end, "Description", "Location", false,
            false, "MTWRFSU", 5);

    event.addOffsetDays(2);
    CalendarEntry entry = event.isEventStartWith("Event", start).getKey();
    assertNull(entry);
    List<Map<String,String>> eventList = event.getAllEventDetails();
    Map<String, String> eventDetails = eventList.get(0);
    assertEquals("Event", eventDetails.get("Name"));
    assertEquals("12/03/2023", eventDetails.get("StartDate"));
    assertEquals("10:00 AM", eventDetails.get("StartTime"));
    assertEquals("12/03/2023", eventDetails.get("EndDate"));
    assertEquals("11:00 AM", eventDetails.get("EndTime"));

  }

  @Test
  public void testUpdateDateTime() {
    ZonedDateTime start = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end = stringToZdt("2023-12-01T11:00:00", "UTC");
    RecurringEvent event = new RecurringEvent("Event", start, end, "Description", "Location", false,
            false, "MTWRFSU", 5);

    ZonedDateTime target = stringToZdt("2023-12-07T10:00:00", "UTC");
    event.updateDateTime(target);
    List<Map<String,String>> eventList = event.getAllEventDetails();
    assertEquals("Event", eventList.get(0).get("Name"));
    assertEquals("12/07/2023", eventList.get(0).get("StartDate"));
    assertEquals("10:00 AM", eventList.get(0).get("StartTime"));
  }

  @Test
  public void testCopy() {
    ZonedDateTime start = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end = stringToZdt("2023-12-01T11:00:00", "UTC");
    RecurringEvent event = new RecurringEvent("Event", start, end, "Description", "Location", false,
            false, "MTWRFSU", 5);

    CalendarEntry copy = event.copy();
    assertNotNull(copy);
  }

  @Test
  public void testGetEventsIfWithinRange() {
    ZonedDateTime start = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end = stringToZdt("2023-12-01T11:00:00", "UTC");
    RecurringEvent event = new RecurringEvent("Event", start, end, "Description", "Location", false,
            false, "MTWRFSU", 5);

    ZonedDateTime startRange = stringToZdt("2023-12-01T09:00:00", "UTC");
    ZonedDateTime endRange = stringToZdt("2023-12-10T12:00:00", "UTC");
    List<CalendarEntry> events = event.getEventsIfWithinRange(startRange, endRange);
    assertEquals(5, events.size());
  }


  @Test
  public void testUndoEdit() {
    ZonedDateTime start = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end = stringToZdt("2023-12-01T11:00:00", "UTC");
    RecurringEvent event = new RecurringEvent("Event", start, end, "Description", "Location", false,
            false, "MTWRFSU", 2);
    event.editRecurringEvent("Event", start, "name", "NewEvent");
    event.editRecurringEvent("NewEvent", start, "name", "NewName");
    event.editRecurringEvent("NewName", start, "name", "New New");
    List<Map<String,String>> eventList1 = event.getAllEventDetails();
    assertEquals(2, eventList1.size());
    Map<String, String> eventDetails = eventList1.get(0);
    assertEquals("New New", eventDetails.get("Name"));

    event.undoEdit();
    List<Map<String,String>> eventList = event.getAllEventDetails();
    assertEquals(2, eventList.size());
    Map<String, String> eventDetails1 = eventList.get(0);
    assertEquals("NewName", eventDetails1.get("Name"));
  }

  @Test
  public void testUndoEdit1() {
    ZonedDateTime start = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end = stringToZdt("2023-12-01T11:00:00", "UTC");
    RecurringEvent event = new RecurringEvent("Event", start, end, "Description", "Location", false,
            false, "MTWRFSU", 2);
    event.editRecurringEvent("Event", null, "name", "NewEvent");
    event.editRecurringEvent("NewEvent", null, "name", "NewName");
    event.editRecurringEvent("NewName", null, "name", "New New");
    List<Map<String,String>> eventList1 = event.getAllEventDetails();
    Map<String, String> eventDetails = eventList1.get(0);
    assertEquals("New New", eventDetails.get("Name"));

    event.undoEdit();
    List<Map<String,String>> eventList = event.getAllEventDetails();
    Map<String, String> eventDetails1 = eventList.get(0);
    assertEquals("NewName", eventDetails1.get("Name"));

  }


  @Test
  public void testUndoEditComplex() {
    ZonedDateTime start = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end = stringToZdt("2023-12-01T11:00:00", "UTC");
    RecurringEvent event = new RecurringEvent("New New", start, end, "Description", "Location",
            false,
            false, "MTWRFSU", 2);

    event.editRecurringEvent("New New", null, "frequency", "7");
    List<Map<String,String>> eventList1 = event.getAllEventDetails();
    assertEquals(7, eventList1.size());
    event.undoEdit();
    List<Map<String,String>> eventList = event.getAllEventDetails();
    assertEquals(2, eventList.size());
  }
}