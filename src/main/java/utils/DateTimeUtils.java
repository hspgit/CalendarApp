package utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.zone.ZoneRulesException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for date and time operations.
 */
public class DateTimeUtils {

  /**
   * Regular expression for date string of the format YYYY-MM-DD.
   */
  private static final String DATE_STRING_REGEX =
          "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$";

  /**
   * Regular expression for date time string of the format YYYY-MM-DDTHH:MM.
   */
  private static final String DATA_TIME_STRING_REGEX =
          "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])T(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$";

  /**
   * Mapping of characters to day of the week.
   */
  private static final Map<Character, DayOfWeek> DAY_MAPPING =
          Map.of('M', DayOfWeek.MONDAY,
                  'T', DayOfWeek.TUESDAY,
                  'W', DayOfWeek.WEDNESDAY,
                  'R', DayOfWeek.THURSDAY,
                  'F', DayOfWeek.FRIDAY,
                  'S', DayOfWeek.SATURDAY,
                  'U', DayOfWeek.SUNDAY);

  /**
   * Check if the given date or date time string is valid.
   *
   * @param dateTimeString date or date time string
   *
   * @return true if the given date or date time string is invalid, false otherwise
   */
  public static boolean isInvalidDateOrDateTimeString(String dateTimeString) {

    return !dateTimeString.matches(DATA_TIME_STRING_REGEX)
            && !dateTimeString.matches(DATE_STRING_REGEX);
  }

  /**
   * Check if the given date string is valid.
   *
   * @param dateString date string
   *
   * @return true if the given date string is invalid, false otherwise
   */
  public static boolean isInvalidDateString(String dateString) {
    return !dateString.matches(DATE_STRING_REGEX);
  }

  /**
   * Check if the given date time string is valid.
   *
   * @param dateTimeString date time string
   *
   * @return true if the given date time string is invalid, false otherwise
   */
  public static boolean isInvalidDateTimeString(String dateTimeString) {
    return !dateTimeString.matches(DATA_TIME_STRING_REGEX);
  }


  /**
   * Check if the given week days string is valid. The week days string should be of the format
   * MTWRFSU.
   *
   * @param weekDays week days string
   *
   * @return true if the given week days string is invalid, false otherwise
   */
  public static boolean isInvalidWeekDays(String weekDays) {
    return !weekDays.matches("^[MTWRFSU]+$");
  }


  /**
   * Convert a string to a ZonedDateTime object.
   *
   * @param dateTime string representation of the date and time
   * @param timeZone timezone
   *
   * @return ZonedDateTime object
   */
  public static ZonedDateTime stringToZdt(String dateTime, String timeZone) {

    ZonedDateTime zdt;
    try {
      // Parse the timezone
      ZoneId zone = ZoneId.of(timeZone);

      // Parse dates with timezone awareness

      if (dateTime.contains("T")) {
        // Parse as datetime
        LocalDateTime ldt = LocalDateTime.parse(dateTime);
        zdt = ldt.atZone(zone);
      } else {
        // Parse as date
        LocalDate ld = LocalDate.parse(dateTime);
        zdt = ld.atStartOfDay(zone);
      }
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date format: " + e.getMessage());
    } catch (ZoneRulesException e) {
      throw new IllegalArgumentException("Invalid timezone: " + timeZone);
    }

    return zdt;
  }


  /**
   * Convert a ZonedDateTime object to a string.
   *
   * @param zdt         ZonedDateTime object
   * @param includeTime true if the time should be included in the string, false otherwise
   *
   * @return string representation of the ZonedDateTime object
   */
  public static String zdtToString(ZonedDateTime zdt, boolean includeTime) {
    if (zdt == null) {
      throw new IllegalArgumentException("ZonedDateTime cannot be null");
    }

    DateTimeFormatter formatter;

    if (includeTime) {
      // Format with date and time (ISO_LOCAL_DATE_TIME)
      formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    } else {
      // Format with date only (ISO_LOCAL_DATE)
      formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    return zdt.format(formatter);
  }

  /**
   * Convert a list of DayOfWeek objects to a string.
   *
   * @param daysOfWeek list of DayOfWeek objects
   *
   * @return string representation of the list of DayOfWeek objects
   */
  public static String daysOfWeekToString(List<DayOfWeek> daysOfWeek) {
    final StringBuilder sb = new StringBuilder();
    for (DayOfWeek day : daysOfWeek) {
      switch (day) {
        case MONDAY:
          sb.append("M");
          break;
        case TUESDAY:
          sb.append("T");
          break;
        case WEDNESDAY:
          sb.append("W");
          break;
        case THURSDAY:
          sb.append("R");
          break;
        case FRIDAY:
          sb.append("F");
          break;
        case SATURDAY:
          sb.append("S");
          break;
        case SUNDAY:
          sb.append("U");
          break;
      }
    }
    return sb.toString();
  }

  /**
   * Check if the time range of two events overlap. This is to be used for recurring events when the
   * possibility of different date ranges and days of week has been removed.
   *
   * @param start1 start time of the first event
   * @param end1   end time of the first event
   * @param start2 start time of the second event
   * @param end2   end time of the second event
   *
   * @return true if the time range of the two events overlap, false otherwise
   */
  public static boolean isTimeOverLapping(
          ZonedDateTime start1,
          ZonedDateTime end1,
          ZonedDateTime start2,
          ZonedDateTime end2) {
    return start1.isBefore(end2) && end1.isAfter(start2);
  }

  /**
   * Parse the week days string into a list of DayOfWeek objects.
   *
   * @param daysOfWeekStr week days string
   *
   * @return list of DayOfWeek objects
   */
  public static List<DayOfWeek> parseWeekDays(String daysOfWeekStr) {
    final List<DayOfWeek> days = new ArrayList<>();
    for (final char dayChar : daysOfWeekStr.toCharArray()) {
      DayOfWeek day = DAY_MAPPING.get(dayChar);
      if (day != null) {
        days.add(day); // we might in the future add illegal exception here.
      }
    }
    return days;
  }

  /**
   * Process the start and end date time of an event.
   *
   * @param startDateTime start date time
   * @param endDateTime   end date time
   *
   * @return DateTimeProcessingResult object
   */
  public static DateTimeProcessingResult processEventDateTime(
          String startDateTime,
          String endDateTime,
          String timeZone) {
    if (startDateTime.isEmpty()) {
      throw new IllegalArgumentException("Start date time cannot be empty");
    }
    boolean isAllDay = false;
    if (endDateTime.isEmpty()) {
      endDateTime = startDateTime.split("T")[0] + "T23:59"; // all day -> end time = 23:59
      isAllDay = true;
    }
    ZonedDateTime startZonedDateTime = stringToZdt(startDateTime, timeZone);
    ZonedDateTime endZonedDateTime = stringToZdt(endDateTime, timeZone);
    if (endZonedDateTime.isBefore(startZonedDateTime)) {
      throw new IllegalArgumentException("End time must be after start time");
    }

    return new DateTimeProcessingResult(startZonedDateTime, endZonedDateTime, isAllDay);
  }

  /**
   * Class to hold the result of processing the start and end date time of an event.
   */
  public static class DateTimeProcessingResult {
    public final ZonedDateTime startDateTime;
    public final ZonedDateTime endDateTime;
    public final boolean isAllDay;

    /**
     * Constructor for DateTimeProcessingResult.
     *
     * @param startDateTime start date time
     * @param endDateTime   end date time
     * @param isAllDay      true if the event is an all-day event, false otherwise
     */
    public DateTimeProcessingResult(
            ZonedDateTime startDateTime,
            ZonedDateTime endDateTime,
            boolean isAllDay) {
      this.startDateTime = startDateTime;
      this.endDateTime = endDateTime;
      this.isAllDay = isAllDay;
    }
  }

  /**
   * Check if the given time zone is invalid.
   *
   * @param timeZone time zone
   *
   * @return true if the given time zone is invalid, false otherwise
   */
  public static boolean isInvalidTimeZone(String timeZone) {
    try {
      ZoneId test = ZoneId.of(timeZone);
    } catch (Exception e) {
      return true;
    }
    return false;
  }

  /**
   * Calculate the number of days between two dates.
   *
   * @param firstDate the first date in the format YYYY-MM-DD
   * @param endDate   the end date in the format YYYY-MM-DD
   *
   * @return the number of days between the two dates
   */
  public static int offsetDaysBetweenDates(String firstDate, String endDate) {
    Temporal start = LocalDate.parse(firstDate);
    Temporal end = LocalDate.parse(endDate);
    return (int) ChronoUnit.DAYS.between(start, end);
  }

  /**
   * Calculate the untilDateTime based on the startDateTime, frequency, and daysOfWeek.
   *
   * @param startDateTime the startDateTime of the event.
   * @param frequency     the expected frequency of the event.
   * @param daysOfWeek    the days of week the event occurs on.
   *
   * @return the end date time of the event.
   */
  public static ZonedDateTime calculateUntilDateTime(
          ZonedDateTime startDateTime,
          int frequency,
          List<DayOfWeek> daysOfWeek) {
    ZonedDateTime untilDateTime = startDateTime;
    int occurrenceCount = 0;

    while (occurrenceCount <= frequency) {
      if (daysOfWeek.contains(untilDateTime.getDayOfWeek())) {
        occurrenceCount++;
      }
      if (occurrenceCount <= frequency) {
        untilDateTime = untilDateTime.plusDays(1);
      }
    }
    return untilDateTime;
  }

  /**
   * Convert a 24-hour time string to a 12-hour format string.
   *
   * @param time24 the 24-hour time string
   *
   * @return the 12-hour format string
   */
  public static String convertTo12HourFormat(String time24) {
    if (time24 == null || time24.isBlank()) {
      return "";
    }

    try {
      // Parse the input datetime string
      LocalDateTime dateTime = LocalDateTime.parse(time24,
              DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

      // Format to a more readable format with 12-hour time
      return dateTime.format(
              DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"));
    } catch (DateTimeParseException e) {
      // Return the original string if parsing fails
      return time24;
    }
  }

  /**
   * Get all available time zones in a sorted order.
   *
   * @return an array of all available time zones
   */
  public static String[] getAllAvailableTimezones() {
    return ZoneId.getAvailableZoneIds().stream()
            .sorted()
            .toArray(String[]::new);
  }

  /**
   * Converts date format from "MM/dd/yyyy hh:mm a" to "yyyy-MM-dd'T'HH:mm".
   *
   * @param dateTimeStr the date time string in UI format
   *
   * @return the date time string in model format
   */
  public static String convertDateTimeFormat(String dateTimeStr) {
    try {
      // Using regex to parse MM/dd/yyyy hh:mm a format
      String regex = "(\\d{1,2})/(\\d{1,2})/(\\d{4})\\s+(\\d{1,2}):(\\d{2})\\s+([AP]M)";
      Pattern pattern = Pattern.compile(regex);
      String convertedDateTime = getConvertedDateTime(dateTimeStr, pattern);

      // Check if this is a valid date time in the model's expected format
      if (isInvalidDateTimeString(convertedDateTime)) {
        throw new IllegalArgumentException("Invalid date format after conversion");
      }

      return convertedDateTime;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
              "Invalid date format. Expected format: MM/dd/yyyy hh:mm AM/PM"
      );
    }
  }

  private static String getConvertedDateTime(String dateTimeStr, Pattern pattern) {
    Matcher matcher = pattern.matcher(dateTimeStr);

    if (!matcher.matches()) {
      throw new IllegalArgumentException(
              "Invalid date format. Expected format: MM/dd/yyyy hh:mm AM/PM");
    }

    int month = Integer.parseInt(matcher.group(1));
    int day = Integer.parseInt(matcher.group(2));
    int year = Integer.parseInt(matcher.group(3));
    int hour = Integer.parseInt(matcher.group(4));
    int minute = Integer.parseInt(matcher.group(5));
    String ampm = matcher.group(6);

    // Convert 12-hour format to 24-hour format
    if (ampm.equals("PM") && hour < 12) {
      hour += 12;
    } else if (ampm.equals("AM") && hour == 12) {
      hour = 0;
    }

    // Format to yyyy-MM-dd'T'HH:mm
    return String.format("%04d-%02d-%02dT%02d:%02d", year, month, day, hour, minute);
  }
}
