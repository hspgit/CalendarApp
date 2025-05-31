package controller.command;

import controller.fileexporter.CsvFileExporter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import model.CalendarModel;
import view.CalendarView;

import static utils.ParsingUtils.isValidCSVFileName;

/**
 * ExportCommand is a class that exports the calendar to a csv file. It extends the AbstractCommand
 * class and overrides the execute and parseInputArguments methods.
 */
public class ExportCommand extends AbstractCommand {

  /**
   * Constructs an ExportCommand object by calling the super constructor of the AbstractCommand
   * class.
   *
   * @param calendarModel the calendar model
   * @param calendarView  the calendar view
   */
  public ExportCommand(CalendarModel calendarModel, CalendarView calendarView) {
    super(calendarModel, calendarView);
  }

  /**
   * Exports the calendar to a csv file.
   *
   * @param input the input arguments
   */
  @Override
  public void execute(String[] input) {
    final Map<String, String> inputArguments = parseInputArguments(input);
    final String fileName = inputArguments.get("fileName");
    try {
      Map<String, String>[] events = calendarModel.getCurrentCalendar().getAllEvents();
      Path filePath = new CsvFileExporter().export(events, fileName);
      if (calendarView != null) {
        calendarView.displayMessage("File exported to: " + filePath);
      }
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Parses the input arguments.
   *
   * @param inputArguments the input arguments
   *
   * @return the parsed input arguments
   *
   * @throws IllegalArgumentException if the number of arguments is invalid, the export type is
   *                                  invalid, or the file name is invalid
   */
  @Override
  protected Map<String, String> parseInputArguments(String[] inputArguments) {
    if (inputArguments.length != 3) {
      throw new IllegalArgumentException("Invalid number of arguments");
    }
    if (!inputArguments[1].equals("cal")) {
      throw new IllegalArgumentException("Invalid export type: " + inputArguments[1]);
    }
    final String testFileName = inputArguments[2];
    if (isValidCSVFileName(testFileName)) {
      Map<String, String> parsedArguments = new HashMap<>();
      parsedArguments.put("fileName", testFileName);
      return parsedArguments;
    }
    throw new IllegalArgumentException("Invalid file name");
  }

}