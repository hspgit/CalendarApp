package controller.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import model.CalendarModel;
import view.CalendarView;

import static utils.DateTimeUtils.isInvalidDateString;
import static utils.DateTimeUtils.isInvalidDateTimeString;
import static utils.ParsingUtils.updateArgMap;

/**
 * PrintCommand is a class that prints a list of events from the calendar model. The list of events
 * can be filtered by date or date range.
 */
public class PrintCommand extends AbstractCommand {

  /**
   * Set of keywords that are used to parse the input arguments.
   */
  private final Set<String> printKeyWords = Set.of(
          "from",
          "to",
          "on"
  );

  /**
   * Constructs a PrintCommand object with the specified calendar model and view.
   *
   * @param calendarModel the calendar model
   * @param calendarView  the calendar view
   */
  public PrintCommand(CalendarModel calendarModel, CalendarView calendarView) {
    super(calendarModel, calendarView);
  }

  /**
   * Executes the print command with the specified input arguments. The input arguments must be in
   * the format 'print events on dateString' or 'print events from dateTimeString to dateTimeString'
   * Use view to display the list of events.
   *
   * @param input the input arguments
   *
   * @throws IllegalArgumentException if the input arguments are invalid
   */
  @Override
  public void execute(String[] input) {
    if (input.length < 4 || !input[1].equals("events")) {
      throw new IllegalArgumentException(
              "Invalid command format. Use 'print events on <date>' "
                      + "or 'print events from <date> to <date>'");
    }

    final Map<String, String> inputArguments = parseInputArguments(input);
    validateParsedArgs(inputArguments);
    String[] eventList;
    String fromDate = inputArguments.get("from");
    String toDate = inputArguments.get("to");
    String onDate = inputArguments.get("on");

    if (onDate.isEmpty()) {
      eventList = convertArrayOfMapsToArrayOfStrings(
          calendarModel.getCurrentCalendar().getEventsRange(fromDate, toDate));
    } else {
      eventList = convertArrayOfMapsToArrayOfStrings(
          calendarModel.getCurrentCalendar().getEventsOnDate(onDate));
    }

    displayResults(eventList);
  }

  private String[] convertArrayOfMapsToArrayOfStrings(Map<String, String>[] arrayOfMaps) {
    String[] arrayOfStrings = new String[arrayOfMaps.length];
    for (int i = 0; i < arrayOfMaps.length; i++) {
      arrayOfStrings[i] = convertMapToString(arrayOfMaps[i]);
    }
    return arrayOfStrings;
  }

  private String convertMapToString(Map<String, String> eventDetails) {
    return "Name:" + eventDetails.getOrDefault("Name", "") + " "
        + "StartDateTime:" + eventDetails.getOrDefault("StartDateTime", "") + " "
        + "EndDateTime:" + eventDetails.getOrDefault("EndDateTime", "") + " "
        + "Location:" + eventDetails.getOrDefault("Location", "");
  }

  private void displayResults(String[] eventList) {
    if (calendarView != null) {
      calendarView.displayMessage("List of events:");
      calendarView.displayEntries(eventList);
    }
  }

  /**
   * Parses the input arguments and returns a map of the arguments.
   *
   * @param inputArguments the input arguments
   *
   * @return a map of the input arguments
   */
  @Override
  protected Map<String, String> parseInputArguments(String[] inputArguments) {
    Map<String, String> inputArgumentsMap = new HashMap<>();
    updateArgMap(inputArguments, printKeyWords, inputArgumentsMap);
    return inputArgumentsMap;
  }

  /**
   * Validates the parsed input arguments. The 'on' argument must be a valid date string, and 'from'
   * and 'to' arguments must be valid date time strings. The 'on' argument cannot be used with
   * 'from' or 'to'.
   *
   * @param argMap the parsed input arguments
   *
   * @throws IllegalArgumentException if the input arguments are invalid
   */
  private void validateParsedArgs(Map<String, String> argMap) {
    if (!argMap.get("on").isEmpty()) {
      if (isInvalidDateString(argMap.get("on"))) {
        throw new IllegalArgumentException(
                "Invalid date string for 'on': " + argMap.get("on"));
      }
      if (!argMap.get("from").isEmpty() || !argMap.get("to").isEmpty()) {
        throw new IllegalArgumentException("Cannot use 'on' with 'from' or 'to'");
      }
    } else if (!argMap.get("from").isEmpty() && !argMap.get("to").isEmpty()) {
      if (isInvalidDateTimeString(argMap.get("from"))) {
        throw new IllegalArgumentException(
                "Invalid date time string for 'from': " + argMap.get("from"));
      }
      if (isInvalidDateTimeString(argMap.get("to"))) {
        throw new IllegalArgumentException(
                "Invalid date time string for 'to': " + argMap.get("to"));
      }
    } else {
      throw new IllegalArgumentException("Must provide either 'on' or both 'from' and 'to'");
    }
  }
}