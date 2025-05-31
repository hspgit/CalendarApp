import org.junit.Test;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import utils.DateTimeUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static utils.DateTimeUtils.calculateUntilDateTime;
import static utils.DateTimeUtils.convertTo12HourFormat;
import static utils.DateTimeUtils.daysOfWeekToString;
import static utils.DateTimeUtils.isInvalidDateOrDateTimeString;
import static utils.DateTimeUtils.isInvalidDateString;
import static utils.DateTimeUtils.isInvalidDateTimeString;
import static utils.DateTimeUtils.isInvalidWeekDays;
import static utils.DateTimeUtils.isTimeOverLapping;
import static utils.DateTimeUtils.parseWeekDays;
import static utils.DateTimeUtils.stringToZdt;
import static utils.DateTimeUtils.zdtToString;

/**
 * Test class for DateTimeUtils.
 */
public class DateTimeUtilsTest {


  @Test
  public void testDateTimeUtils() {
    DateTimeUtils ut = new DateTimeUtils();
    assertNotNull(ut);
  }

  @Test
  public void testIsInvalidDateOrDateTimeString() {
    // Valid cases
    assertFalse(isInvalidDateOrDateTimeString("2023-01-15"));
    assertFalse(isInvalidDateOrDateTimeString("2023-01-15T14:30"));

    // Invalid cases
    assertTrue(isInvalidDateOrDateTimeString("2023/01/15"));
    assertTrue(isInvalidDateOrDateTimeString("2023-1-5"));
    assertTrue(isInvalidDateOrDateTimeString("2023-01-15 14:30"));
    assertTrue(isInvalidDateOrDateTimeString("2023-13-15T14:30"));
    assertTrue(isInvalidDateOrDateTimeString("2023-01-32T14:30"));
    assertTrue(isInvalidDateOrDateTimeString("2023-01-15T24:30"));
    assertTrue(isInvalidDateOrDateTimeString("2023-01-15T14:60"));
  }

  @Test
  public void testIsInvalidDateString() {
    // Valid cases
    assertFalse(isInvalidDateString("2023-01-15"));
    assertFalse(isInvalidDateString("2023-12-31"));

    // Invalid cases
    assertTrue(isInvalidDateString("2023/01/15"));
    assertTrue(isInvalidDateString("23-1-5"));
    assertTrue(isInvalidDateString("2023-13-15"));
    assertTrue(isInvalidDateString("2023-01-32"));
  }

  @Test
  public void testIsInvalidDateTimeString() {
    // Valid cases
    assertFalse(isInvalidDateTimeString("2023-01-15T14:30"));
    assertFalse(isInvalidDateTimeString("2023-12-31T23:59"));

    // Invalid cases
    assertTrue(isInvalidDateTimeString("2023-01-15 14:30"));
    assertTrue(isInvalidDateTimeString("2023-01-15T24:00"));
    assertTrue(isInvalidDateTimeString("2023-01-15T14:60"));
    assertTrue(isInvalidDateTimeString("2023-13-15T14:30"));
  }



  // Week days validation tests
  @Test
  public void testIsInvalidWeekDays() {
    // Valid cases - using MTWRFSU format
    assertFalse(isInvalidWeekDays("M"));
    assertFalse(isInvalidWeekDays("MWF"));
    assertFalse(isInvalidWeekDays("MTWRFSU"));

    // Invalid cases
    assertTrue(isInvalidWeekDays("Monday"));
    assertTrue(isInvalidWeekDays("1234"));
    assertTrue(isInvalidWeekDays("m"));
    assertTrue(isInvalidWeekDays("MW-F"));
    assertTrue(isInvalidWeekDays(""));
  }

  @Test
  public void testStringToZdt() {
    // Test date string
    ZonedDateTime date = stringToZdt("2023-05-15", "America/New_York");
    assertEquals(2023, date.getYear());
    assertEquals(5, date.getMonthValue());
    assertEquals(15, date.getDayOfMonth());
    assertEquals(0, date.getHour());
    assertEquals(0, date.getMinute());

    // Test datetime string
    ZonedDateTime datetime = stringToZdt("2023-05-15T14:30", "UTC");
    assertEquals(2023, datetime.getYear());
    assertEquals(5, datetime.getMonthValue());
    assertEquals(15, datetime.getDayOfMonth());
    assertEquals(14, datetime.getHour());
    assertEquals(30, datetime.getMinute());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStringToZdtInvalidFormat() {
    stringToZdt("2023/05/15", "UTC");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStringToZdtInvalidTimezone() {
    stringToZdt("2023-05-15", "InvalidZone");
  }

  @Test
  public void testZdtToString() {
    ZonedDateTime zdt = ZonedDateTime.of(2023, 5, 15, 14, 30, 0, 0, ZoneId.of("UTC"));

    // Test with time included
    assertEquals("2023-05-15T14:30", zdtToString(zdt, true));

    // Test without time
    assertEquals("2023-05-15", zdtToString(zdt, false));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testZdtToStringNullInput() {
    zdtToString(null, true);
  }

  @Test
  public void testDaysOfWeekToString() {
    // Single day
    List<DayOfWeek> oneDay = new ArrayList<>();
    oneDay.add(DayOfWeek.MONDAY);
    assertEquals("M", daysOfWeekToString(oneDay));

    // Multiple days
    List<DayOfWeek> weekdays = new ArrayList<>();
    weekdays.add(DayOfWeek.MONDAY);
    weekdays.add(DayOfWeek.WEDNESDAY);
    weekdays.add(DayOfWeek.FRIDAY);
    assertEquals("MWF", daysOfWeekToString(weekdays));

    // All days
    List<DayOfWeek> allDays = new ArrayList<>();
    allDays.add(DayOfWeek.MONDAY);
    allDays.add(DayOfWeek.TUESDAY);
    allDays.add(DayOfWeek.WEDNESDAY);
    allDays.add(DayOfWeek.THURSDAY);
    allDays.add(DayOfWeek.FRIDAY);
    allDays.add(DayOfWeek.SATURDAY);
    allDays.add(DayOfWeek.SUNDAY);
    assertEquals("MTWRFSU", daysOfWeekToString(allDays));
  }

  @Test
  public void testIsTimeOverlapping() {
    ZoneId zone = ZoneId.of("UTC");
    // Case 1: Events overlap
    ZonedDateTime start1 = ZonedDateTime.of(2023, 5, 15, 10, 0, 0, 0, zone);
    ZonedDateTime end1 = ZonedDateTime.of(2023, 5, 15, 12, 0, 0, 0, zone);
    ZonedDateTime start2 = ZonedDateTime.of(2023, 5, 15, 11, 0, 0, 0, zone);
    ZonedDateTime end2 = ZonedDateTime.of(2023, 5, 15, 13, 0, 0, 0, zone);
    assertTrue(isTimeOverLapping(start1, end1, start2, end2));

    // Case 2: Events don't overlap
    start2 = ZonedDateTime.of(2023, 5, 15, 14, 0, 0, 0, zone);
    end2 = ZonedDateTime.of(2023, 5, 15, 16, 0, 0, 0, zone);
    assertFalse(isTimeOverLapping(start1, end1, start2, end2));

    // Case 3: One event contained within another
    start2 = ZonedDateTime.of(2023, 5, 15, 10, 30, 0, 0, zone);
    end2 = ZonedDateTime.of(2023, 5, 15, 11, 30, 0, 0, zone);
    assertTrue(isTimeOverLapping(start1, end1, start2, end2));
  }

  @Test
  public void testParseWeekDays() {
    // Single day
    List<DayOfWeek> monday = parseWeekDays("M");
    assertEquals(1, monday.size());
    assertEquals(DayOfWeek.MONDAY, monday.get(0));

    // Multiple days
    List<DayOfWeek> mwf = parseWeekDays("MWF");
    assertEquals(3, mwf.size());
    assertEquals(DayOfWeek.MONDAY, mwf.get(0));
    assertEquals(DayOfWeek.WEDNESDAY, mwf.get(1));
    assertEquals(DayOfWeek.FRIDAY, mwf.get(2));

    // All days
    List<DayOfWeek> all = parseWeekDays("MTWRFSU");
    assertEquals(7, all.size());
    assertEquals(DayOfWeek.THURSDAY, all.get(3)); // R represents Thursday
    assertEquals(DayOfWeek.SUNDAY, all.get(6));   // U represents Sunday
  }

  @Test
  public void testCalculateUntilDateTime() {
    ZoneId zone = ZoneId.of("America/New_York");
    ZonedDateTime start = ZonedDateTime.of(2025, 3, 3, 10, 0, 0, 0, zone);
    List<DayOfWeek> mondaysOnly = List.of(DayOfWeek.MONDAY);

    // Frequency of 3 should give us 3 Mondays (excluding the start date)
    ZonedDateTime until = calculateUntilDateTime(start, 3, mondaysOnly);
    assertEquals(2025, until.getYear());
    assertEquals(3, until.getMonthValue());
    assertEquals(24, until.getDayOfMonth());
  }

  @Test
  public void testConvertTo12HourFormatNull() {
    String time12Hour = convertTo12HourFormat(null);
    assertEquals("", time12Hour);
  }

  @Test
  public void testConvertTo12HourFormatEmpty() {
    String time12Hour = convertTo12HourFormat("    ");
    assertEquals("", time12Hour);
  }

  @Test
  public void testConvertTo12HourFormatValid() {
    String time12Hour = convertTo12HourFormat("2023-01-15T14:30");
    assertEquals("Jan 15, 2023 2:30 PM", time12Hour);
  }

  @Test
  public void testConvertTo12HourFormatInValid() {
    String time12Hour = convertTo12HourFormat("invalid-date-time");
    assertEquals("invalid-date-time", time12Hour);
  }
}