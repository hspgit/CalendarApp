package controller.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import model.CalendarModel;

import static utils.DateTimeUtils.isInvalidDateOrDateTimeString;
import static utils.DateTimeUtils.isInvalidWeekDays;
import static utils.ParsingUtils.getKeyWordValue;
import static utils.ParsingUtils.updateArgMap;

/**
 * Command to create a new calendar entry.
 */
public class CreateCommand extends AbstractCommand {

  /**
   * Set of allowed creation types.
   */
  private final Set<String> allowedCreationTypes = Set.of("event", "calendar");

  /**
   * Set of keywords for creating a calendar entry.
   */
  private final Set<String> createKeyWords = Set.of(
          "from",
          "to",
          "on",
          "repeats",
          "for",
          "until",
          "description",
          "location",
          "--name", // for calendar name
          "--timezone" // for calendar timezone
  );

  /**
   * Constructor for the CreateCommand class. Initializes the calendarModel field.
   *
   * @param calendarModel the calendarModel to be used
   */
  public CreateCommand(CalendarModel calendarModel) {
    super(calendarModel);
  }

  /**
   * Executes the command. Creates a new calendar event based on the input.
   *
   * @param input the input to the command
   *
   * @throws IllegalArgumentException This could be due to invalid input length, invalid creation
   *                                  type, invalid date time, invalid week days, invalid for value,
   *                                  invalid until date time, missing start date time, specifying
   *                                  wrong date time, specifying both on and from/to date time,
   *                                  specifying an invalid creation type.
   */
  @Override
  public void execute(String[] input) {
    if (input.length < 5) {
      throw new IllegalArgumentException(
              "Invalid input length: " + String.join(" ", input));
    }
    final String calendarEntryType = input[1];
    if (!allowedCreationTypes.contains(calendarEntryType)) {
      throw new IllegalArgumentException("Invalid creation type: " + calendarEntryType);
    }
    if ("event".equals(calendarEntryType)) {
      createEvent(input);
    } else if ("calendar".equals(calendarEntryType)) {
      createCalendar(input);
    }

  }

  /**
   * Parses the input arguments into a map of key-value pairs.
   *
   * @param inputArguments the input arguments to be parsed
   *
   * @return a map of key-value pairs
   */
  @Override
  protected Map<String, String> parseInputArguments(String[] inputArguments) {
    final Map<String, String> argMap = new HashMap<>();

    final int nameIndex = inputArguments[2].equals("--autoDecline") ? 2 : 1;
    final String name = getKeyWordValue(inputArguments, nameIndex, createKeyWords);
    argMap.put("name", name);

    argMap.put("isAutoDecline",
            Arrays.asList(inputArguments).contains("--autoDecline") ? "true" : "false");

    // this version of the controller will always check for conflicts by overriding the flag
    // provided by the user.
    if (isCheckConflict) {
      argMap.put("isAutoDecline", "true");
    }

    argMap.put("isPublic",
            Arrays.asList(inputArguments).contains("--public") ? "true" : "false");

    updateArgMap(inputArguments, createKeyWords, argMap);
    return argMap;
  }

  /**
   * Validates the parsed arguments. Checks if the date time, week days, for value, until date time
   * are valid. Also checks if the start date time is missing if the date time is specified wrong if
   * both on and from/to date time is specified if the creation type is invalid.
   *
   * @param argMap the map of key-value pairs to be validated
   *
   * @throws IllegalArgumentException if the parsed arguments are invalid
   */
  private void validateParsedArguments(Map<String, String> argMap) {
    final String fromDateTime = argMap.get("from");
    if (!fromDateTime.isEmpty() && isInvalidDateOrDateTimeString(fromDateTime)) {
      throw new IllegalArgumentException("Invalid from date time: " + argMap.get("from"));
    }
    final String toDateTime = argMap.get("to");
    if (!toDateTime.isEmpty() && isInvalidDateOrDateTimeString(toDateTime)) {
      throw new IllegalArgumentException("Invalid to date time: " + argMap.get("to"));
    }
    final String onDateTime = argMap.get("on");
    if (!onDateTime.isEmpty() && isInvalidDateOrDateTimeString(onDateTime)) {
      throw new IllegalArgumentException("Invalid on date time: " + argMap.get("on"));
    }
    if (fromDateTime.isEmpty() && onDateTime.isEmpty()) {
      throw new IllegalArgumentException(
              "Missing start date time, Specify either from/to or on date time");
    }
    if (!onDateTime.isEmpty()) {
      if (!fromDateTime.isEmpty() || !toDateTime.isEmpty()) {
        throw new IllegalArgumentException("Cannot specify both on and from/to date time");
      }
    }
    final String repeats = argMap.get("repeats");
    if (!repeats.isEmpty() && isInvalidWeekDays(repeats)) {
      throw new IllegalArgumentException("Invalid week days: " + argMap.get("repeats"));
    }
    final String forValue = argMap.get("for");
    if (!forValue.isEmpty() && !forValue.matches("\\d+ times")) {
      throw new IllegalArgumentException("Invalid for value: " + forValue);
    }

    final String until = argMap.get("until");
    if (!until.isEmpty() && isInvalidDateOrDateTimeString(until)) {
      throw new IllegalArgumentException("Invalid until date time: " + argMap.get("until"));
    }
  }

  /**
   * Creates a new event based on the input arguments. The event can be a single event or a
   * recurring event. The event can be public or private.
   *
   * @param input the input to the command
   */
  private void createEvent(String[] input) {
    final Map<String, String> argMap = parseInputArguments(input);
    validateParsedArguments(argMap);

    final String name = argMap.get("name");
    final String description = argMap.get("description");
    final String location = argMap.get("location");
    final String startDateTime = !argMap.get("on").isEmpty()
            ? argMap.get("on")
            : argMap.get("from");
    final String endDateTime = !argMap.get("on").isEmpty() ? "" : argMap.get("to");
    final String weekDays = argMap.get("repeats");
    final String untilDateTime = argMap.get("until");
    final Integer frequency = argMap.get("for").isEmpty() ? null :
            Integer.parseInt(argMap.get("for").split(" ")[0]); // X times -> X
    final boolean isPrivate = !Boolean.parseBoolean(argMap.get("isPublic"));
    final boolean isAutoDecline = Boolean.parseBoolean(argMap.get("isAutoDecline"));


    if (weekDays.isEmpty()) {
      createSingleEvent(
              name,
              description,
              location,
              startDateTime,
              endDateTime,
              isAutoDecline,
              isPrivate);
    } else if (!untilDateTime.isEmpty()) {
      createRecurringEventUntil(
              name,
              description,
              location,
              startDateTime,
              endDateTime,
              weekDays,
              untilDateTime,
              isAutoDecline,
              isPrivate);
    } else if (frequency != null) {
      createRecurringEventFrequency(
              name,
              description,
              location,
              startDateTime,
              endDateTime,
              weekDays,
              frequency,
              isAutoDecline,
              isPrivate);
    }
  }

  /**
   * Creates a single event (either with time range or all-day).
   */
  private void createSingleEvent(
          String name,
          String description,
          String location,
          String startDateTime,
          String endDateTime,
          boolean isAutoDecline,
          boolean isPrivate) {
    if (!endDateTime.isEmpty()) {
      calendarModel.getCurrentCalendar().addSingleEvent(
              name,
              startDateTime,
              endDateTime,
              description,
              location,
              isAutoDecline,
              isPrivate);
    } else {
      calendarModel.getCurrentCalendar().addSingleEventAllDay(
              name,
              startDateTime,
              description,
              location,
              isAutoDecline,
              isPrivate);
    }
  }

  /**
   * Creates a recurring event that repeats until a specified end date.
   */
  private void createRecurringEventUntil(
          String name,
          String description,
          String location,
          String startDateTime,
          String endDateTime,
          String weekDays,
          String untilDateTime,
          boolean isAutoDecline,
          boolean isPrivate) {
    if (!endDateTime.isEmpty()) {
      calendarModel.getCurrentCalendar().addRecurringEventUntil(
              name,
              startDateTime,
              endDateTime,
              description,
              location,
              weekDays,
              untilDateTime,
              isAutoDecline,
              isPrivate);
    } else {
      calendarModel.getCurrentCalendar().addRecurringAllDayEventUntil(
              name,
              startDateTime,
              description,
              location,
              weekDays,
              untilDateTime,
              isAutoDecline,
              isPrivate);
    }
  }

  /**
   * Creates a recurring event that repeats for a specified number of times.
   */
  private void createRecurringEventFrequency(
          String name,
          String description,
          String location,
          String startDateTime,
          String endDateTime,
          String weekDays,
          Integer frequency,
          boolean isAutoDecline,
          boolean isPrivate) {
    if (!endDateTime.isEmpty()) {
      calendarModel.getCurrentCalendar().addRecurringEventFrequency(
              name,
              startDateTime,
              endDateTime,
              description,
              location,
              weekDays,
              frequency,
              isAutoDecline,
              isPrivate);
    } else {
      calendarModel.getCurrentCalendar().addRecurringAllDayEventFrequency(
              name,
              startDateTime,
              description,
              location,
              weekDays,
              frequency,
              isAutoDecline,
              isPrivate);
    }
  }

  /**
   * Creates a new calendar based on the input arguments.
   *
   * @param input the input to the command
   */
  private void createCalendar(String[] input) {
    final Map<String, String> argMap = parseInputArguments(input);
    final String name = argMap.get("--name");
    final String timezone = argMap.get("--timezone");
    calendarModel.addCalendar(name, timezone);
  }
}