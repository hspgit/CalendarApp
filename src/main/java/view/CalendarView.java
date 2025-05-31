package view;

/**
 * Interface for the CalendarView class. This interface is used to define the methods that the
 * CalendarView class must implement.
 */
public interface CalendarView {

  /**
   * Displays a message to the user. Can be used to display instructions or information. Can handle
   * formatting.
   *
   * @param message the message to display
   */
  void displayMessage(String message);

  /**
   * Displays an error message to the user. Can be used to display error messages. Can handle
   * formatting.
   *
   * @param message the error message to display
   */
  void displayError(String message);

  /**
   * Displays the entries in the calendar. Can be used to display the events in the calendar. Can
   * handle formatting.
   *
   * @param events the events to display
   */
  void displayEntries(String[] events);
}
