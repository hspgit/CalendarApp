import org.junit.Test;

import java.io.IOException;

import view.BasicCalendarView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test class for BasicCalendarView.
 */
public class BasicCalendarViewTest {

  private static final String ANSI_RED_ERROR = "\u001B[31m";
  private static final String ANSI_GREEN_MESSAGE = "\u001B[32m";
  private static final String ANSI_BLUE_DATA = "\u001B[34m";
  private static final String ANSI_RESET_USER_INPUT = "\u001B[0m";

  @Test
  public void testColoredMessage() {
    StringBuilder output = new StringBuilder();
    BasicCalendarView view = new BasicCalendarView(output);

    view.displayMessage("Test message");

    String expected = ANSI_GREEN_MESSAGE
            + "Test message" + ANSI_RESET_USER_INPUT + System.lineSeparator();
    assertEquals(expected, output.toString());
  }

  @Test
  public void testColoredError() {
    StringBuilder output = new StringBuilder();
    BasicCalendarView view = new BasicCalendarView(output);

    view.displayError("Error message");

    String expected = ANSI_RED_ERROR
            + "ERROR: Error message" + ANSI_RESET_USER_INPUT + System.lineSeparator();
    assertEquals(expected, output.toString());
  }

  @Test
  public void testColoredCalendar() {
    StringBuilder output = new StringBuilder();
    BasicCalendarView view = new BasicCalendarView(output);
    String[] calendar = {"Day 1", "Day 2"};

    view.displayEntries(calendar);

    String expected = ANSI_BLUE_DATA + "Day 1" + System.lineSeparator()
            + ANSI_BLUE_DATA + "Day 2" + System.lineSeparator() + ANSI_RESET_USER_INPUT;
    assertEquals(expected, output.toString());
  }

  @Test
  public void testDisplayMessageIOException() {
    Appendable failingAppendable = new FailingAppendable();
    BasicCalendarView view = new BasicCalendarView(failingAppendable);

    try {
      view.displayMessage("Test message");
      fail("Expected IllegalStateException was not thrown");
    } catch (IllegalStateException e) {
      // Test passes - correct exception was thrown
    }
  }

  @Test
  public void testDisplayErrorIOException() {
    Appendable failingAppendable = new FailingAppendable();
    BasicCalendarView view = new BasicCalendarView(failingAppendable);

    try {
      view.displayError("Error message");
      fail("Expected IllegalStateException was not thrown");
    } catch (IllegalStateException e) {
      // Test passes - correct exception was thrown
    }
  }

  @Test
  public void testDisplayEntriesIOException() {
    Appendable failingAppendable = new FailingAppendable();
    BasicCalendarView view = new BasicCalendarView(failingAppendable);

    try {
      view.displayEntries(new String[]{"Event 1"});
      fail("Expected IllegalStateException was not thrown");
    } catch (IllegalStateException e) {
      // Test passes - correct exception was thrown
    }
  }

  private static class FailingAppendable implements Appendable {
    @Override
    public Appendable append(CharSequence csq) throws IOException {
      throw new IOException("Simulated failure");
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException {
      throw new IOException("Simulated failure");
    }

    @Override
    public Appendable append(char c) throws IOException {
      throw new IOException("Simulated failure");
    }
  }
}

