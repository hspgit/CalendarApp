package controller.command;

import java.util.Map;

import model.CalendarModel;
import view.CalendarView;

/**
 * Abstract class for the Command pattern. Initializes the calendarModel and calendarView fields.
 */
public abstract class AbstractCommand implements Command {
  /**
   * The calendarModel field. Will be used to call the methods in the model.
   */
  protected final CalendarModel calendarModel;

  /**
   * The calendarView field. Will be used to call the methods in the view.
   */
  protected final CalendarView calendarView;

  /**
   * This flag is used to determine if the command should check for conflicts. For assignment 5,
   * this flag is set to true overriding the command typed by the user. In the future, if we want to
   * allow conflicting events, this flag can be set to false.
   */
  protected final boolean isCheckConflict = true;

  /**
   * Constructor for the AbstractCommand class. Initializes the calendarModel and calendarView
   * fields. The calendarView field is set to null.
   *
   * @param calendarModel the calendarModel to be used
   */
  protected AbstractCommand(CalendarModel calendarModel) {
    this.calendarModel = calendarModel;
    this.calendarView = null;
  }

  /**
   * Constructor for the AbstractCommand class. Initializes the calendarModel and calendarView
   * fields.
   *
   * @param calendarModel the calendarModel to be used
   * @param calendarView  the calendarView to be used
   */
  protected AbstractCommand(CalendarModel calendarModel, CalendarView calendarView) {
    this.calendarModel = calendarModel;
    this.calendarView = calendarView;
  }

  /**
   * Parses the input arguments into a map of key-value pairs.
   *
   * @param inputArguments the input arguments to be parsed
   *
   * @return a map of key-value pairs
   */
  protected abstract Map<String, String> parseInputArguments(String[] inputArguments);
}