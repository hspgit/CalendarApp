package controller.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import model.CalendarModel;

import static utils.DateTimeUtils.isInvalidDateString;
import static utils.DateTimeUtils.isInvalidDateTimeString;
import static utils.ParsingUtils.getKeyWordValue;
import static utils.ParsingUtils.updateArgMap;

/**
 * The CopyCommand class is an implementation of the AbstractCommand class. This class is used to
 * copy events from one calendar to another.
 */
public class CopyCommand extends AbstractCommand {

  private final Set<String> copyTypes = Set.of("event", "events");

  private final Set<String> copyKeyWords = Set.of("on", "--target", "to", "and", "between");

  /**
   * Constructor for the CopyCommand class. Initializes the calendarModel field.
   *
   * @param model the calendarModel to be used
   */
  public CopyCommand(CalendarModel model) {
    super(model);
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
    argMap.put("eventName", getKeyWordValue(inputArguments, 1, copyKeyWords));
    updateArgMap(inputArguments, copyKeyWords, argMap);
    return argMap;
  }

  /**
   * Executes the copy command.
   *
   * @param input the input to the command
   */
  @Override
  public void execute(String[] input) {
    if (input.length < 8) {
      throw new IllegalArgumentException(
              "Invalid input length: " + String.join(" ", input));
    }
    final String copyType = input[1];
    if (!copyTypes.contains(copyType)) {
      throw new IllegalArgumentException("Invalid copy type: " + copyType);
    }
    switch (copyType) {
      case "event":
        copySingleEvent(input);
        break;
      case "events":
        copyMultipleEvents(input);
        break;
      default:
        // do nothing
    }
  }

  private void copySingleEvent(String[] input) {
    final Map<String, String> argMap = parseInputArguments(input);
    final String onDate = argMap.get("on");
    final String toDate = argMap.get("to");
    if (onDate == null || isInvalidDateTimeString(onDate)) {
      throw new IllegalArgumentException("Invalid on date time string: " + onDate);
    }
    if (toDate == null || isInvalidDateTimeString(toDate)) {
      throw new IllegalArgumentException("Invalid to date time string: " + toDate);
    }
    calendarModel.copySingleEvent(
            argMap.get("eventName"),
            argMap.get("--target"),
            onDate,
            toDate,
            isCheckConflict // autoDecline always true for Assignment 5
    );
  }

  private void copyMultipleEvents(String[] input) {
    final Map<String, String> argMap = parseInputArguments(input);
    validateParsedArgs(argMap);
    final String targetCalendar = argMap.get("--target");
    final String targetDate = argMap.get("to");
    final String startDate = argMap.get("between");
    final String endDate = argMap.get("and");
    final String onDate = argMap.get("on");

    if (onDate.isEmpty()) {
      calendarModel.copyMultipleEventsRange(
              targetCalendar,
              startDate,
              endDate,
              targetDate,
              isCheckConflict // autoDecline always true for Assignment 5
      );
    } else {
      calendarModel.copyMultipleEventsOnDay(
              targetCalendar,
              onDate,
              targetDate,
              isCheckConflict // autoDecline always true for Assignment 5
      );
    }
  }


  private void validateParsedArgs(Map<String, String> argMap) {
    final String targetCalendar = argMap.get("--target");
    final String targetDate = argMap.get("to");

    if (targetDate == null || isInvalidDateString(targetDate)) {
      throw new IllegalArgumentException("Invalid target date string: " + targetDate);
    }

    if (targetCalendar == null || targetCalendar.isEmpty()) {
      throw new IllegalArgumentException("Target calendar name is required");
    }

    // Check if we're using the 'between...and' format
    if (!argMap.get("between").isEmpty() && !argMap.get("and").isEmpty()) {
      final String startDate = argMap.get("between");
      final String endDate = argMap.get("and");

      if (startDate == null || isInvalidDateString(startDate)) {
        throw new IllegalArgumentException("Invalid start date string: " + startDate);
      }

      if (endDate == null || isInvalidDateString(endDate)) {
        throw new IllegalArgumentException("Invalid end date string: " + endDate);
      }
    } else {
      final String onDate = argMap.get("on");

      if (onDate == null || isInvalidDateString(onDate)) {
        throw new IllegalArgumentException("Invalid on date string: " + onDate);
      }
    }
  }
}

