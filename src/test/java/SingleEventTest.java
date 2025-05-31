import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static utils.DateTimeUtils.stringToZdt;

import java.time.ZonedDateTime;
import java.util.List;

import java.util.Map;
import model.calendarentry.CalendarEntry;
import model.calendarentry.SingleEvent;

import org.junit.Test;

/**
 * Test class for the SingleEvent class.
 */
public class SingleEventTest {

  @Test
  public void isConflict() {
    ZonedDateTime start1 = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end1 = stringToZdt("2023-12-01T11:00:00", "UTC");
    SingleEvent event1 = new SingleEvent("Event 1", start1, end1, "Description 1",
            "Location 1", false, false);

    ZonedDateTime start2 = stringToZdt("2023-12-01T10:30:00", "UTC");
    ZonedDateTime end2 = stringToZdt("2023-12-01T11:30:00", "UTC");
    SingleEvent event2 = new SingleEvent("Event 2", start2, end2, "Description 2",
            "Location 2", false, false);

    boolean conflict = event1.isConflict(event2);

    assertTrue(conflict);
  }

  @Test
  public void getEventDetails() {
    ZonedDateTime start = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end = stringToZdt("2023-12-01T11:00:00", "UTC");
    SingleEvent event = new SingleEvent("Event", start, end, "Description",
            "Location", false, false);

    List<Map<String,String>> details = event.getEventDetails(
            start.minusHours(1), end.plusHours(1), false);

    assertEquals(1, details.size());
    assertEquals("Event", details.get(0).get("Name"));
  }

  @Test
  public void getAllEventDetails() {
    ZonedDateTime start = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end = stringToZdt("2023-12-01T11:00:00", "UTC");
    SingleEvent event = new SingleEvent("Event", start, end, "Description",
            "Location", false, false);

    List<Map<String,String>> csvRow = event.getAllEventDetails();

    assertEquals("Event", csvRow.get(0).get("Name"));
    assertEquals("12/01/2023", csvRow.get(0).get("StartDate"));
  }

  @Test
  public void matchesSingleOccurrence() {
    ZonedDateTime start = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end = stringToZdt("2023-12-01T11:00:00", "UTC");
    SingleEvent event = new SingleEvent("Event", start, end, "Description",
            "Location", false, false);

    CalendarEntry match = event.matchesSingleOccurrence("Event", start, end);

    assertNotNull(match);
  }

  @Test
  public void isConflict_NoConflict() {
    ZonedDateTime start1 = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end1 = stringToZdt("2023-12-01T11:00:00", "UTC");
    SingleEvent event1 = new SingleEvent("Event 1", start1, end1, "Description 1",
            "Location 1", false, false);

    ZonedDateTime start2 = stringToZdt("2023-12-01T11:30:00", "UTC");
    ZonedDateTime end2 = stringToZdt("2023-12-01T12:30:00", "UTC");
    SingleEvent event2 = new SingleEvent("Event 2", start2, end2, "Description 2",
            "Location 2", false, false);

    boolean conflict = event1.isConflict(event2);

    assertFalse(conflict);
  }

  @Test
  public void testMatchesRecurring() {
    ZonedDateTime start = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end = stringToZdt("2023-12-01T11:00:00", "UTC");
    SingleEvent event = new SingleEvent("Event", start, end, "Description",
            "Location", false, false);

    CalendarEntry entry = event.matchesRecurringEvent("Event", start);

    assertEquals(entry, event);
  }

  @Test
  public void testMatchesRecurringStartNull() {
    ZonedDateTime start = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end = stringToZdt("2023-12-01T11:00:00", "UTC");
    SingleEvent event = new SingleEvent("Event", start, end, "Description",
            "Location", false, false);

    CalendarEntry entry = event.matchesRecurringEvent("Event", null);

    assertEquals(entry, event);
  }

  @Test
  public void testGetEventsWithinRange() {
    ZonedDateTime start = stringToZdt("2023-12-01T10:00:00", "UTC");
    ZonedDateTime end = stringToZdt("2023-12-01T11:00:00", "UTC");
    SingleEvent event = new SingleEvent("Event", start, end, "Description",
            "Location", false, false);

    List<CalendarEntry> events = event
            .getEventsIfWithinRange(start.minusHours(1), end.plusHours(1));

    assertEquals(1, events.size());
  }

  @Test
  public void testGetEventsWithinRangeNotMatched() {
    ZonedDateTime start = stringToZdt("2023-12-01T10:00", "UTC");
    ZonedDateTime end = stringToZdt("2023-12-01T11:00", "UTC");
    SingleEvent event = new SingleEvent("Event", start, end, "Description",
            "Location", false, false);

    List<CalendarEntry> events = event.getEventsIfWithinRange(start.minusHours(2),
            start.minusHours(1));

    assertNull(events);
  }
}