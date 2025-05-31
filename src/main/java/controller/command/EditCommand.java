package controller.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import model.CalendarModel;

import static utils.DateTimeUtils.isInvalidDateOrDateTimeString;
import static utils.ParsingUtils.getKeyWordValue;
import static utils.ParsingUtils.updateArgMap;

/**
 * EditCommand is a class that represents the command to edit an event or a series of events.
 */
public class EditCommand extends AbstractCommand {

  /**
   * The set of allowed edit types.
   */
  private final Set<String> allowedEditTypes = Set.of("event", "events", "calendar");

  /**
   * The set of edit keywords.
   */
  private final Set<String> editKeyWords = Set.of(
          "from",
          "to",
          "with",
          "--name",
          "--property"
  );

  /**
   * The set of editable properties for event.
   */
  private final Set<String> eventEditableProperties = Set.of(
          "name",
          "startDateTime",
          "endDateTime",
          "weekDays",
          "frequency",
          "untilDateTime",
          "description",
          "location",
          "public",
          "private",
          "allDay"
  );

  /**
   * The set of editable properties for calendar.
   */
  private final Set<String> calendarEditableProperties = Set.of(
          "name",
          "timezone"
  );

  /**
   * Constructs an instance of EditCommand.
   *
   * @param calendarModel The calendar model to be used.
   */
  public EditCommand(CalendarModel calendarModel) {
    super(calendarModel);
  }

  /**
   * Executes the edit command. Parses the input arguments and edits the event or series of events
   * based on the input.
   *
   * @param input The input arguments for the command.
   *
   * @throws IllegalArgumentException If the input arguments are invalid. For example, if the input
   *                                  arguments are not in the correct format, or if the edit type
   *                                  is not allowed, or if the property name is not editable, or if
   *                                  the date time strings are invalid.
   */
  @Override
  public void execute(String[] input) {
    if (input.length < 5) {
      throw new IllegalArgumentException(
              "Invalid input length: " + String.join(" ", input));
    }
    final String calendarEntryType = input[1];
    if (!allowedEditTypes.contains(calendarEntryType)) {
      throw new IllegalArgumentException("Invalid edit type: " + calendarEntryType);
    }
    switch (calendarEntryType) {
      case "event":
        editSingleEvent(input);
        break;
      case "events":
        editRecurringEvents(input);
        break;
      case "calendar":
        editCalendar(input);
        break;
      default:
        // do nothing
    }
  }

  /**
   * Parses the input arguments for the edit command.
   *
   * @param inputArguments The input arguments for the command.
   *
   * @return A map containing the parsed input arguments.
   */
  @Override
  protected Map<String, String> parseInputArguments(String[] inputArguments) {
    final Map<String, String> argMap = new HashMap<>();
    argMap.put("propertyName", inputArguments[2]);
    if (noKeyWords(inputArguments)) {
      manuallyAddKeyWords(inputArguments, argMap); // need to do this because getKeywordValue
      // depends on the existence of keyWords
    } else {
      argMap.put("eventName", getKeyWordValue(inputArguments, 2, editKeyWords));
      updateArgMap(inputArguments, editKeyWords, argMap);
    }
    return argMap;
  }

  /**
   * Checks if there are no keywords in the input arguments. Need this to handle the case where the
   * user does not provide the keywords in the input arguments.
   *
   * @param inputArguments The input arguments for the command.
   *
   * @return True if there are no keywords in the input arguments, false otherwise.
   */
  private boolean noKeyWords(String[] inputArguments) {
    return Arrays.stream(inputArguments).noneMatch(editKeyWords::contains);
  }

  /*
   * Manually add the keywords to the argMap if the user does not provide them in the input
   * arguments. Used in case of edit all recurring events.
   *
   * @param inputArguments The input arguments for the command.
   * @param argMap The map containing the parsed input arguments.
   *
   */
  private void manuallyAddKeyWords(String[] inputArguments, Map<String, String> argMap) {
    parseNameValue(inputArguments, argMap);
    parseWithValue(inputArguments, argMap);
    argMap.put("from", "");
    argMap.put("to", "");
  }

  /*
   * Parse the name value pair for the event name.
   *
   * @param inputArguments The input arguments for the command.
   *
   * @param argMap The map containing the parsed input arguments.
   *
   */
  private void parseNameValue(String[] inputArguments, Map<String, String> argMap) {
    // Check if we have a quoted string scenario
    if (!inputArguments[3].startsWith("\"")) {
      argMap.put("eventName", inputArguments[3]);
      return;
    }

    argMap.put("eventName", getKeyWordValue(inputArguments, 2, editKeyWords));
  }


  /*
   * Parse the with value for the event.
   *
   * @param inputArguments The input arguments for the command.
   *
   * @param argMap The map containing the parsed input arguments.
   *
   */
  private void parseWithValue(String[] inputArguments, Map<String, String> argMap) {
    // Check if we have a quoted string scenario
    if (inputArguments[inputArguments.length - 1].endsWith("\"")) {
      // Find the starting quote position
      for (int i = inputArguments.length - 1; i >= 0; i--) {
        if (inputArguments[i].startsWith("\"")) {
          argMap.put("with", getKeyWordValue(inputArguments, i - 1, editKeyWords));
          return;
        }
      }
    }

    // Fall back to using the last argument if no quoted string is found
    argMap.put("with", getKeyWordValue(inputArguments,
            inputArguments.length - 2,
            editKeyWords));
  }

  /*
   * Edits the calendar with the given input arguments.
   *
   * @param input The input arguments for the command.
   *
   */
  private void editCalendar(String[] input) {
    final Map<String, String> argMap = parseInputArguments(input);
    final int index = Arrays.asList(input).indexOf("--property");
    if (index == -1) {
      throw new IllegalArgumentException("Invalid command: " + String.join(" ", input));
    }
    final String propertyName = input[index + 1];
    if (!calendarEditableProperties.contains(propertyName)) {
      throw new IllegalArgumentException("Invalid property name: " + propertyName);
    }
    parseWithValue(input, argMap);
    calendarModel.editCalendar(
            argMap.get("--name"),
            propertyName,
            argMap.get("with")
    );
  }

  /*
   * Edits a single event.
   *
   * @param input The input arguments for the command.
   *
   */
  private void editSingleEvent(String[] input) {
    final Map<String, String> argMap = parseInputArguments(input);
    validateParsedArguments(argMap);
    calendarModel.getCurrentCalendar().editSingleEvent(
            argMap.get("eventName"),
            argMap.get("from"),
            argMap.get("to"),
            argMap.get("propertyName"),
            argMap.get("with"),
            isCheckConflict // autoDecline always true for Assignment 5
    );
  }

  /*
   * Edits recurring events with the given input arguments.
   *
   * @param input The input arguments for the command.
   *
   */
  private void editRecurringEvents(String[] input) {
    final Map<String, String> argMap = parseInputArguments(input);
    validateParsedArguments(argMap);
    final String fromDateTime = argMap.get("from");
    if (!fromDateTime.isEmpty()) {
      calendarModel.getCurrentCalendar().editMultipleEventsFollowing(
              argMap.get("eventName"),
              argMap.get("from"),
              argMap.get("propertyName"),
              argMap.get("with"),
              isCheckConflict // autoDecline always true for Assignment 5
      );
    } else {
      calendarModel.getCurrentCalendar().editMultipleEventsAll(
              argMap.get("eventName"),
              argMap.get("propertyName"),
              argMap.get("with"),
              isCheckConflict // autoDecline always true for Assignment 5
      );
    }


  }


  /*
   * Validates the parsed input arguments.
   *
   * @param argMap The map containing the parsed input arguments.
   *
   * @throws IllegalArgumentException If the input arguments are invalid.
   *
   */
  private void validateParsedArguments(Map<String, String> argMap) {
    final String propertyName = argMap.get("propertyName");
    if (!eventEditableProperties.contains(propertyName)) {
      throw new IllegalArgumentException("Invalid property name: " + propertyName);
    }
    final String startDateTime = argMap.getOrDefault("from", "");
    if (!startDateTime.isEmpty()
            && isInvalidDateOrDateTimeString(startDateTime)) {
      throw new IllegalArgumentException("Invalid from date time: " + startDateTime);
    }
    final String endDateTime = argMap.getOrDefault("to", "");
    if (!endDateTime.isEmpty()
            && isInvalidDateOrDateTimeString(endDateTime)) {
      throw new IllegalArgumentException("Invalid to date time: " + endDateTime);
    }
  }


}