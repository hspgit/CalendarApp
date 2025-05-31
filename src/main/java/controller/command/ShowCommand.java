package controller.command;

import java.util.HashMap;
import java.util.Map;

import model.CalendarModel;
import view.CalendarView;

import static utils.DateTimeUtils.isInvalidDateTimeString;

/**
 * ShowCommand is a class that represents the command to show the status of a date. It extends the
 * AbstractCommand class and overrides the execute and parseInputArguments methods.
 */
public class ShowCommand extends AbstractCommand {

  /**
   * Constructs a ShowCommand object with the provided calendarModel and calendarView.
   *
   * @param calendarModel the model to be used by the ShowCommand object
   * @param calendarView  the view to be used by the ShowCommand object
   */
  public ShowCommand(CalendarModel calendarModel, CalendarView calendarView) {
    super(calendarModel, calendarView);
  }

  /**
   * Executes the show command with the provided input arguments. It throws an
   * IllegalArgumentException if the number of arguments is wrong or if the arguments are wrong. It
   * then parses the input arguments and shows the status of the date. If the calendarView is not
   * null, it displays the status.
   *
   * @param input the input arguments to execute the show command.
   *
   * @throws IllegalArgumentException if the number of arguments is wrong or if the arguments are
   *                                  wrong.
   */
  @Override
  public void execute(String[] input) {
    if (input.length != 4) {
      throw new IllegalArgumentException("Wrong number of arguments");
    }
    if (!input[1].equals("status") || !input[2].equals("on")) {
      throw new IllegalArgumentException("Wrong arguments");
    }
    final Map<String, String> inputArguments = parseInputArguments(input);

    final String date = inputArguments.get("date");
    final String status = calendarModel.getCurrentCalendar().getStatusOnDateTime(date);
    displayStatus(status);
  }

  private void displayStatus(String status) {
    if (calendarView != null) {
      calendarView.displayMessage(status);
    }
  }

  /**
   * Parses the input arguments and returns a map of the arguments.
   *
   * @param inputArguments the input arguments to be parsed.
   *
   * @return a map of the parsed arguments.
   */
  @Override
  protected Map<String, String> parseInputArguments(String[] inputArguments) {
    final String testDateTimeString = inputArguments[3];
    if (isInvalidDateTimeString(testDateTimeString)) {
      throw new IllegalArgumentException("Invalid date time string: " + testDateTimeString);
    }
    final Map<String, String> parsedArguments = new HashMap<>();
    parsedArguments.put("date", testDateTimeString);
    return parsedArguments;
  }
}