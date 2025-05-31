package controller.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import model.CalendarModel;

import static utils.ParsingUtils.updateArgMap;

/**
 * UseCommand class is responsible for executing the use command.
 */
public class UseCommand extends AbstractCommand {

  private final Set<String> useTypes = Set.of("calendar");

  /**
   * Set of keywords for using a calendar.
   */
  private final Set<String> useKeyWords = Set.of("--name");

  /**
   * Constructor for the UseCommand class. Initializes the calendarModel field.
   *
   * @param calendarModel the calendarModel to be used
   */
  public UseCommand(CalendarModel calendarModel) {
    super(calendarModel);
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
    updateArgMap(inputArguments, useKeyWords, argMap);
    return argMap;
  }

  /**
   * Executes the use command.
   *
   * @param input the input to the command
   */
  @Override
  public void execute(String[] input) {
    if (input.length < 4) {
      throw new IllegalArgumentException("Invalid input length: " + String.join(" ", input));
    }
    final String useType = input[1];
    if (!useTypes.contains(useType)) {
      throw new IllegalArgumentException("Invalid use type: " + useType);
    }
    if ("calendar".equals(useType)) {
      useCalendar(input);
    }
  }

  private void useCalendar(String[] input) {
    final Map<String, String> argMap = parseInputArguments(input);
    calendarModel.useCalendar(argMap.get("--name"));
  }
}
