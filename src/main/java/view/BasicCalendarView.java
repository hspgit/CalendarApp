package view;

import java.io.IOException;

/**
 * A basic implementation of the CalendarView interface that writes to an Appendable object.
 */
public class BasicCalendarView implements CalendarView {

  /**
   * ANSI escape codes for colored text.
   */
  private static final String ANSI_RED_ERROR = "\u001B[31m";
  private static final String ANSI_GREEN_MESSAGE = "\u001B[32m";
  private static final String ANSI_BLUE_DATA = "\u001B[34m";
  private static final String ANSI_RESET_USER_INPUT = "\u001B[0m";
  /**
   * The Appendable object to write to.
   */
  private final Appendable out;

  /**
   * Constructs a new BasicCalendarView that writes to the given Appendable object.
   *
   * @param out the Appendable object to write to
   */
  public BasicCalendarView(Appendable out) {
    this.out = out;
  }

  /**
   * Displays the given message to the user. Outputs the message in green text. Resets the text
   * color to the default color after the message is displayed.
   *
   * @param message the message to display
   */
  @Override
  public void displayMessage(String message) {
    try {
      out.append(ANSI_GREEN_MESSAGE)
              .append(message)
              .append(ANSI_RESET_USER_INPUT)
              .append(System.lineSeparator());
    } catch (IOException e) {
      handleIOException(e);
    }
  }

  /**
   * Displays the given error message to the user. Outputs the message in red text. Resets the text
   * color to the default color after the message is displayed.
   *
   * @param message the error message to display
   */
  @Override
  public void displayError(String message) {
    try {
      out.append(ANSI_RED_ERROR)
              .append("ERROR: ")
              .append(message)
              .append(ANSI_RESET_USER_INPUT)
              .append(System.lineSeparator());
    } catch (IOException e) {
      handleIOException(e);
    }
  }

  /**
   * Displays the given entries to the user. Outputs the entries in blue text. Resets the text color
   * to the default color after the entries are displayed.
   *
   * @param calendar the entries to display
   */
  @Override
  public void displayEntries(String[] calendar) {
    try {
      for (String entry : calendar) {
        out.append(ANSI_BLUE_DATA)
                .append(entry)
                .append(System.lineSeparator());
      }
      out.append(ANSI_RESET_USER_INPUT);
    } catch (IOException e) {
      handleIOException(e);
    }
  }

  /**
   * Handles an IOException that occurred while writing to the output.
   *
   * @param e the IOException that occurred
   */
  private void handleIOException(IOException e) {
    throw new IllegalStateException("Failed to write to output", e);
  }
}