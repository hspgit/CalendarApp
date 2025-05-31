import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import controller.BasicCalendarController;
import controller.CalendarController;
import model.BasicCalendarModel;
import model.CalendarModel;
import view.BasicCalendarView;
import view.CalendarView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for BasicCalendarController.
 */
public class BasicCalendarControllerTest {

  private static final String ANSI_RED_ERROR = "\u001B[31m";
  private static final String ANSI_GREEN_MESSAGE = "\u001B[32m";
  private static final String ANSI_BLUE_DATA = "\u001B[34m";
  private static final String ANSI_RESET_USER_INPUT = "\u001B[0m";
  private final CalendarView view;
  private final CalendarModel model;
  private final StringBuilder out;

  /**
   * Constructor for the test class.
   */
  public BasicCalendarControllerTest() {
    this.out = new StringBuilder();
    this.model = new BasicCalendarModel();
    this.view = new BasicCalendarView(out);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidMode() {
    InputStream in = new ByteArrayInputStream(("").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    try {
      controller.startApp(new String[]{"--mode", "invalid"});
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid mode: invalid", e.getMessage());
      throw e;
    }
  }

  @Test
  public void testExitInteractive() {
    InputStream in = new ByteArrayInputStream(("exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    assertEquals(ANSI_GREEN_MESSAGE
                    + "You are in interactive mode, please enter your commands"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
                    + System.lineSeparator(), out.toString());
  }

  @Test
  public void testExitInteractiveCaseInsensitive() {
    InputStream in = new ByteArrayInputStream(("exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "InterActive"});
    assertEquals(ANSI_GREEN_MESSAGE
                    + "You are in interactive mode, please enter your commands"
                    + ANSI_RESET_USER_INPUT + System.lineSeparator()
                    + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
                    + System.lineSeparator(), out.toString());
  }

  @Test
  public void testExitHeadless() {
    InputStream in = new ByteArrayInputStream(("").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "headless", "./scripts/commands.txt"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in headless mode, please wait for the output"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testExitHeadlessCaseInsensitive() {
    InputStream in = new ByteArrayInputStream(("").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "HEADless", "./scripts/commands.txt"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in headless mode, please wait for the output"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testHeadlessFileNotFound() {
    InputStream in = new ByteArrayInputStream(("").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    try {
      controller.startApp(new String[]{"--mode", "headless", "./scripts/commands1.txt"});
    } catch (IllegalArgumentException e) {
      assertEquals("Error (No such file or directory): ./scripts/commands1.txt",
              e.getMessage());
      throw e;
    }
  }

  @Test
  public void testEmptyCommand() {
    InputStream in = new ByteArrayInputStream(("create event evName from 2020-10-10"
            + System.lineSeparator()
            + System.lineSeparator()
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testInvalidCreateTask() {
    InputStream in = new ByteArrayInputStream(("create task " + System.lineSeparator() + "exit")
            .getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid input length: create task"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCreateTaskHeadless() {
    InputStream in = new ByteArrayInputStream(("").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    try {
      controller.startApp(new String[]{"--mode", "headless", "./scripts/err_command2.txt"});
    } catch (IllegalArgumentException e) {
      final String output = out.toString();
      assertEquals(ANSI_GREEN_MESSAGE
              + "You are in headless mode, please wait for the output"
              + ANSI_RESET_USER_INPUT + System.lineSeparator()
              + ANSI_RED_ERROR + "ERROR: Invalid input length: create event evName from"
              + ANSI_RESET_USER_INPUT + System.lineSeparator(), output);
      assertEquals("Error: Invalid input length: create event evName from"
              + System.lineSeparator() + " for command: create event evName from", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testHeadLessNoExitLine() {
    InputStream in = new ByteArrayInputStream(("").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    try {
      controller.startApp(
              new String[]{"--mode", "headless", "./scripts/endless_command.txt"});
    } catch (IllegalArgumentException e) {
      final String output = out.toString();
      assertEquals(ANSI_GREEN_MESSAGE
              + "You are in headless mode, please wait for the output"
              + ANSI_RESET_USER_INPUT + System.lineSeparator()
              + ANSI_GREEN_MESSAGE + "Command executed successfully"
              + ANSI_RESET_USER_INPUT + System.lineSeparator(), output);
      assertEquals("Error: File must end with 'exit'", e.getMessage());
      throw e;
    }
  }

  @Test
  public void testCreateInvalidLength() {
    InputStream in = new ByteArrayInputStream(("create event \"task1\" from "
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid input length: create event \"task1\" from"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testCreateInvalidOnDateTime() {
    InputStream in = new ByteArrayInputStream(("create event \"task1\" on 2021-13-01T12:00 "
            + "description \"this should fail\" "
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR
            + "ERROR: Invalid on date time: 2021-13-01T12:00"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testCreateInvalidOnWithFrom() {
    InputStream in = new ByteArrayInputStream(("create event \"task1\" from 2021-03-01T12:00 "
            + "on 2021-03-01T12:00 description \"this should fail\" "
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR
            + "ERROR: Cannot specify both on and from/to date time"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testCreateInvalidOnWithTo() {
    InputStream in = new ByteArrayInputStream(("create event \"task1\" to 2021-03-01T12:00 "
            + "on 2021-03-01T12:00 description \"this should fail\" "
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR
            + "ERROR: Cannot specify both on and from/to date time"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testCreateInvalidEntryType() {
    InputStream in = new ByteArrayInputStream(("create InvalidEvent \"task1\" "
            + "from 2021-03-01T12:00 " + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid creation type: InvalidEvent"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testCreateEmptyFromDateTime() {
    InputStream in = new ByteArrayInputStream(("create event \"task1\" "
            + "to 2021-03-01T12:00 description \"this should fail\" "
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR
            + "ERROR: Missing start date time, Specify either from/to or on date time"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testCreateInvalidWeekdays() {
    InputStream in = new ByteArrayInputStream(("create event \"task1\" from 2021-03-01T12:00 "
            + "to 2021-03-05T12:00 description \"this should fail\" "
            + "repeats MXU for 5 times"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid week days: MXU"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testCreateSingleEvent() {
    InputStream in =
            new ByteArrayInputStream(("create event \"eventN with space\" from 2021-03-01T12:00 "
                    + "to 2021-03-01T13:00 location \"my home\" description \"for testing\" "
                    + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testCreateSingleEventAutoDecline() {
    InputStream in =
            new ByteArrayInputStream(("create event --autoDecline \"eventN with space\" from "
                    + "2021-03-01T12:00 "
                    + "to 2021-03-01T13:00 location \"my home\" description \"for testing\" "
                    + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testCreateSingleEventAutoDeclinePublic() {
    InputStream in =
            new ByteArrayInputStream(("create event --autoDecline \"eventN with space\" from "
                    + "2021-03-01T12:00 "
                    + "to 2021-03-01T13:00 location \"my home\" description \"for testing\" "
                    + "--public"
                    + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testCreateRecEventTimes() {
    InputStream in =
            new ByteArrayInputStream(("create event --autoDecline \"eventN with space\" "
                    + "from 2021-03-01T12:00 "
                    + "to 2021-03-01T13:00 location \"my home\" description \"for testing\" "
                    + "repeats MTWRFSU for 5 times"
                    + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testCreateRecEventUntil() {
    InputStream in =
            new ByteArrayInputStream(("create event --autoDecline \"eventN with space\" "
                    + "from 2021-03-01T12:00 "
                    + "to 2021-03-01T13:00 location \"my home\" description \"for testing\" "
                    + "repeats MTWRFSU until 2021-03-31T12:00"
                    + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testCreateRecEventUntilInvalidFrom() {
    InputStream in =
            new ByteArrayInputStream(("create event --autoDecline \"eventN with space\" "
                    + "from 2021-13-01T12:00 "
                    + "to 2021-03-01T13:00 location \"my home\" description \"for testing\" "
                    + "repeats MTWRFSU until 2021-03-31T12:00"
                    + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid from date time: 2021-13-01T12:00"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testCreateRecEventUntilInvalidTo() {
    InputStream in =
            new ByteArrayInputStream(("create event --autoDecline \"eventN with space\" "
                    + "from 2021-03-01T12:00 "
                    + "to 2021-03-41T13:00 location \"my home\" description \"for testing\" "
                    + "repeats MTWRFSU until 2021-03-31T12:00"
                    + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid to date time: 2021-03-41T13:00"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testCreateRecEventUntilInvalidUntil() {
    InputStream in =
            new ByteArrayInputStream(("create event --autoDecline \"eventN with space\" "
                    + "from 2021-03-01T12:00 "
                    + "to 2021-03-01T13:00 location \"my home\" description \"for testing\" "
                    + "repeats MTWRFSU until 2021-13-32T12:00"
                    + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid until date time: 2021-13-32T12:00"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testCreateRecEventInvalidTimes() {
    InputStream in =
            new ByteArrayInputStream(("create event --autoDecline \"eventN with space\" "
                    + "from 2021-03-01T12:00 "
                    + "to 2021-03-01T13:00 location \"my home\" description \"for testing\" "
                    + "repeats MTWRFSU for abc times"
                    + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid for value: abc times"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testEditEventRecFrom() {
    InputStream in =
            new ByteArrayInputStream(("edit events location \"some evName\" from 2021-03-01T12:00 "
                    + "with \"new loc\""
                    + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Could not find any recurring event: some evName"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testEditEventRecAll() {
    InputStream in =
            new ByteArrayInputStream(("edit events location someName newLoc"
                    + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Could not find any recurring event: someName"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testHeadlessInvalidCommand() {
    InputStream in = new ByteArrayInputStream(("").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    try {
      controller.startApp(new String[]{"--mode", "headless", "./scripts/error_command.txt"});
    } catch (IllegalArgumentException e) {
      final String output = out.toString();
      assertEquals(
              ANSI_GREEN_MESSAGE + "You are in headless mode, please wait for the output"
                      + ANSI_RESET_USER_INPUT + System.lineSeparator()
                      + ANSI_RED_ERROR + "ERROR: Unknown command: invalidCommand this should fail"
                      + ANSI_RESET_USER_INPUT + System.lineSeparator(), output);
      assertEquals("Unknown command: invalidCommand this should fail", e.getMessage());
      throw e;
    }
  }

  @Test
  public void testEditInvalidLength() {
    InputStream in = new ByteArrayInputStream(("edit event " + System.lineSeparator() + "exit")
            .getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid input length: edit event"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testEditInvalidEvenType() {
    InputStream in = new ByteArrayInputStream(("edit InvalidEvent location evName from 2020-10-10"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid edit type: InvalidEvent"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testEditInvalidStartTime() {
    InputStream in = new ByteArrayInputStream(("edit event location evName from 2020-45-10 "
                + "with newLoc " + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid from date time: 2020-45-10"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testEditInvalidEndTime() {
    InputStream in = new ByteArrayInputStream(("edit event location evName from 2020-05-10 "
                + "to 2020-15-10 with newLoc " + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid to date time: 2020-15-10"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testEditInvalidProperty() {
    InputStream in = new ByteArrayInputStream(("edit event invalidProperty evName from 2020-05-10 "
            + "to 2020-05-14 with newLoc " + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid property name: invalidProperty"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testExportCalendar() {
    InputStream in = new ByteArrayInputStream(("create event evName from 2019-01-01T00:00 "
            + "to 2019-01-01T00:00 "
            + "description \"This is a multi word\" " + System.lineSeparator()
            + "create event evName1 location \"My own room\" from 2019-01-01T00:00 to "
            + "2019-01-01T00:00 repeats MWF for 4 times description chalo" + System.lineSeparator()
            + "export cal fileName.csv" + System.lineSeparator()
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    String projectPath = System.getProperty("user.dir");
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator() + ANSI_GREEN_MESSAGE + "File exported to: "
            + projectPath + "/fileName.csv" + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator() + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testExportCalendarCheckFile() throws IOException {
    final String fileName = "fileName.csv";
    InputStream in = new ByteArrayInputStream(("create event Event1 from 2019-01-01T00:00 "
            + "to 2019-01-01T00:00 "
            + "description \"This is a multi word\" " + System.lineSeparator()
            + "create event Event2 location \"My own room\" from 2019-01-01T00:00 to "
            + "2019-01-01T00:00 repeats MWF for 4 times description chalo" + System.lineSeparator()
            + "export cal " + fileName + " " + System.lineSeparator()
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});

    // Read and verify the file contents
    File file = new File(fileName);
    assertTrue("Exported file should exist", file.exists());

    List<String> lines = Files.readAllLines(file.toPath());
    assertEquals("File should contain 6 lines (header + 5 events)", 6, lines.size());

    // Check header line
    assertTrue("Header line incorrect",
            lines.get(0).contains("Subject")
                    && lines.get(0).contains("Start Date")
                    && lines.get(0).contains("End Date"));

    // Check event entries
    assertTrue("Event1 not found in export",
            lines.stream().anyMatch(line -> line.contains("Event1")));
    assertTrue("Event2 not found in export",
            lines.stream().anyMatch(line -> line.contains("Event2")));

    // Clean up test file
    file.delete();

  }

  @Test
  public void testExportCalendarInvalidLength() {
    InputStream in = new ByteArrayInputStream(("export"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid number of arguments"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testExportCalendarInvalidExportType() {
    InputStream in = new ByteArrayInputStream(("export notCal fileName.csv"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid export type: notCal"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testExportCalendarInvalidFileName() {
    InputStream in = new ByteArrayInputStream(("export cal file+@!Name.csv"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid file name" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testPrintCommandFromTo() {
    InputStream in = new ByteArrayInputStream(("create event evName from 2021-03-01T16:00 to "
            + "2021-03-01T17:00 location \"Northeastern University\" " + System.lineSeparator()
            + "create event \"home test\" from 2021-03-02T16:00 to "
            + "2021-03-02T17:00 location \"Boylston Street\" " + System.lineSeparator()
            + "print events from 2021-03-01T00:00 to 2021-03-02T23:59"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            // create event 1
            + ANSI_GREEN_MESSAGE + "Command executed successfully"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            // create event 2
            + ANSI_GREEN_MESSAGE + "List of events:" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_BLUE_DATA + "Name:evName StartDateTime:2021-03-01T16:00 "
            + "EndDateTime:2021-03-01T17:00 Location:Northeastern University"
            + System.lineSeparator()
            + ANSI_BLUE_DATA + "Name:home test StartDateTime:2021-03-02T16:00 "
            + "EndDateTime:2021-03-02T17:00 Location:Boylston Street" + System.lineSeparator()
            + ANSI_RESET_USER_INPUT
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testPrintCommandFromToRecurring() {
    InputStream in = new ByteArrayInputStream(("create event evName from 2021-03-01T16:00 to "
            + "2021-03-01T17:00 location \"Northeastern University\" repeats MTWRFSU "
            + "until 2021-03-06T14:30 " + System.lineSeparator()
            + "create event \"home test\" from 2021-03-02T14:00 to "
            + "2021-03-02T15:00 location \"Boylston Street\" " + System.lineSeparator()
            + "print events from 2021-03-01T00:00 to 2021-03-06T14:00"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "List of events:" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_BLUE_DATA + "Name:evName StartDateTime:2021-03-01T16:00 "
            + "EndDateTime:2021-03-01T17:00 Location:Northeastern University"
            + System.lineSeparator()
            + ANSI_BLUE_DATA + "Name:home test StartDateTime:2021-03-02T14:00 "
            + "EndDateTime:2021-03-02T15:00 Location:Boylston Street" + System.lineSeparator()
            + ANSI_BLUE_DATA + "Name:evName StartDateTime:2021-03-02T16:00 "
            + "EndDateTime:2021-03-02T17:00 Location:Northeastern University"
            + System.lineSeparator()
            + ANSI_BLUE_DATA + "Name:evName StartDateTime:2021-03-03T16:00 "
            + "EndDateTime:2021-03-03T17:00 Location:Northeastern University"
            + System.lineSeparator()
            + ANSI_BLUE_DATA + "Name:evName StartDateTime:2021-03-04T16:00 "
            + "EndDateTime:2021-03-04T17:00 Location:Northeastern University"
            + System.lineSeparator()
            + ANSI_BLUE_DATA + "Name:evName StartDateTime:2021-03-05T16:00 "
            + "EndDateTime:2021-03-05T17:00 Location:Northeastern University"
            + System.lineSeparator()
            + ANSI_RESET_USER_INPUT
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testPrintCommandOnInvalid() {
    InputStream in = new ByteArrayInputStream(("print events on 2021-13-01"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid date string for 'on': 2021-13-01"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testPrintCommandOnWithFrom() {
    InputStream in = new ByteArrayInputStream(("print events on 2021-10-01 from 2021-03-01"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Cannot use 'on' with 'from' or 'to'"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);

  }

  @Test
  public void testPrintCommandOnWithTo() {
    InputStream in = new ByteArrayInputStream(("print events on 2021-10-01 to 2021-03-01"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Cannot use 'on' with 'from' or 'to'"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testPrintCommandOn() {
    InputStream in = new ByteArrayInputStream(("create event evName from 2021-03-01T16:00 to "
            + "2021-03-01T17:00 location \"Northeastern University\" " + System.lineSeparator()
            + "create event \"home test\" from 2021-03-02T16:00 to "
            + "2021-03-02T17:00 location \"Boylston Street\" " + System.lineSeparator()
            + "print events on 2021-03-01"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            // create event 1
            + ANSI_GREEN_MESSAGE + "Command executed successfully"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            // create event 2
            + ANSI_GREEN_MESSAGE + "List of events:" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_BLUE_DATA + "Name:evName StartDateTime:2021-03-01T16:00 "
            + "EndDateTime:2021-03-01T17:00 Location:Northeastern University"
            + System.lineSeparator()
            + ANSI_RESET_USER_INPUT
            + ANSI_GREEN_MESSAGE + "Command executed successfully"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testPrintCommandFromToInvalidFrom() {
    InputStream in = new ByteArrayInputStream(("print events from 2021-13-01 to 2021-03-05"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid date time string for 'from': 2021-13-01"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testPrintCommandFromToInvalidTo() {
    InputStream in = new ByteArrayInputStream(("print events from 2021-03-01T12:12 to 2021-13-05"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid date time string for 'to': 2021-13-05"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testPrintCommandNoKeywords() {
    InputStream in = new ByteArrayInputStream(("print events abc abc abc abc"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Must provide either 'on' or both 'from' and 'to'"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testPrintNotEvents() {
    InputStream in = new ByteArrayInputStream(("print NotEvents on 2021-03-01"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid command format. Use 'print events on <date>'"
            + " or 'print events from <date> to <date>'" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testPrintInvalidLength() {
    InputStream in = new ByteArrayInputStream(("print events lol"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid command format. Use 'print events on <date>'"
            + " or 'print events from <date> to <date>'"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }


  @Test
  public void testShowStatus() {
    InputStream in = new ByteArrayInputStream(("show status on 2021-03-01T12:00"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Available" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testShowStatusInvalidDate() {
    InputStream in = new ByteArrayInputStream(("show status on 2021-03-01"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid date time string: 2021-03-01"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testShowStatusInvalidLength() {
    InputStream in = new ByteArrayInputStream(("show status on"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Wrong number of arguments"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testShowStatusKeyWord1() {
    InputStream in = new ByteArrayInputStream(("show invalid on 2021-03-01T12:00"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Wrong arguments"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testShowStatusKeyWord2() {
    InputStream in = new ByteArrayInputStream(("show status from 2021-03-01T12:00"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Wrong arguments"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testEditAllRecurrence() {
    InputStream in = new ByteArrayInputStream(("create event \"this test\" "
            + "from 2025-03-10T16:00 to 2025-03-10T17:00 "
            + "location \"Northeastern University\" repeats MTWRFSU "
            + "for 3 times " + System.lineSeparator()
            + "edit events location \"this test\" \"new loc\" " + System.lineSeparator()
            + "print events from 2025-03-01T00:00 to 2025-03-16T14:00 " + System.lineSeparator()
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});

    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "List of events:" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_BLUE_DATA + "Name:this test StartDateTime:2025-03-10T16:00 "
            + "EndDateTime:2025-03-10T17:00 Location:new loc" + System.lineSeparator()
            + ANSI_BLUE_DATA + "Name:this test StartDateTime:2025-03-11T16:00 "
            + "EndDateTime:2025-03-11T17:00 Location:new loc" + System.lineSeparator()
            + ANSI_BLUE_DATA + "Name:this test StartDateTime:2025-03-12T16:00 "
            + "EndDateTime:2025-03-12T17:00 Location:new loc" + System.lineSeparator()
            + ANSI_RESET_USER_INPUT
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testEditAllRecurrenceNoQuotes() {
    InputStream in = new ByteArrayInputStream(("create event \"this test\" "
            + "from 2025-03-10T16:00 to 2025-03-10T17:00 "
            + "location \"Northeastern University\" repeats MTWRFSU "
            + "for 3 times " + System.lineSeparator()
            + "edit events location \"this test\" newLoc " + System.lineSeparator()
            + "print events from 2025-03-01T00:00 to 2025-03-16T14:00 " + System.lineSeparator()
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});

    final String output = out.toString();
    final Map<String, String>[] events = model.getCurrentCalendar()
            .getEventsRange("2025-03-01T00:00", "2025-03-16T00:00");
    assertEquals(3, events.length);
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "List of events:" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_BLUE_DATA + "Name:this test StartDateTime:2025-03-10T16:00 "
            + "EndDateTime:2025-03-10T17:00 Location:newLoc" + System.lineSeparator()
            + ANSI_BLUE_DATA + "Name:this test StartDateTime:2025-03-11T16:00 "
            + "EndDateTime:2025-03-11T17:00 Location:newLoc" + System.lineSeparator()
            + ANSI_BLUE_DATA + "Name:this test StartDateTime:2025-03-12T16:00 "
            + "EndDateTime:2025-03-12T17:00 Location:newLoc" + System.lineSeparator()
            + ANSI_RESET_USER_INPUT
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testEditAllRecurrenceNoQuotesInvalidProperty() {
    InputStream in = new ByteArrayInputStream(("create event \"this test\" "
            + "from 2025-03-10T16:00 to 2025-03-10T17:00 "
            + "location \"Northeastern University\" repeats MTWRFSU "
            + "for 3 times " + System.lineSeparator()
            + "edit events invalidProp \"this test\" newLoc " + System.lineSeparator()
            + "print events from 2025-03-01T00:00 to 2025-03-16T14:00 " + System.lineSeparator()
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});

    final String output = out.toString();
    final Map<String, String>[] events = model.getCurrentCalendar()
            .getEventsRange("2025-03-01T00:00", "2025-03-16T00:00");
    assertEquals(3, events.length);
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_RED_ERROR + "ERROR: Invalid property name: invalidProp" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "List of events:" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_BLUE_DATA + "Name:this test StartDateTime:2025-03-10T16:00 "
            + "EndDateTime:2025-03-10T17:00 Location:Northeastern University"
            + System.lineSeparator()
            + ANSI_BLUE_DATA + "Name:this test StartDateTime:2025-03-11T16:00 "
            + "EndDateTime:2025-03-11T17:00 Location:Northeastern University"
            + System.lineSeparator()
            + ANSI_BLUE_DATA + "Name:this test StartDateTime:2025-03-12T16:00 "
            + "EndDateTime:2025-03-12T17:00 Location:Northeastern University"
            + System.lineSeparator()
            + ANSI_RESET_USER_INPUT
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testEditEventSingle() {
    InputStream in =
            new ByteArrayInputStream(("create event \"some evName\" from 2021-03-01T12:00 "
                    + "to 2021-03-01T13:00 location \"some loc\"" + System.lineSeparator()
                    + "edit event location \"some evName\" from 2021-03-01T12:00 "
                    + "to 2021-03-01T13:00 with \"new loc\"" + System.lineSeparator()
                    + "print events on 2021-03-01"
                    + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final Map<String, String>[] events = model.getCurrentCalendar().getEventsOnDate("2021-03-01");
    assertEquals(1, events.length);
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "List of events:" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_BLUE_DATA + "Name:some evName StartDateTime:2021-03-01T12:00 "
            + "EndDateTime:2021-03-01T13:00 Location:new loc" + System.lineSeparator()
            + ANSI_RESET_USER_INPUT
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testCreateCalendar() {
    InputStream in = new ByteArrayInputStream(("create calendar --name \"My NewCalendar\" "
            + "--timezone America/New_York"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testEditToAllDay() {
    InputStream in = new ByteArrayInputStream(("create event \"this test\" "
            + "from 2025-03-10T16:00 to 2025-03-10T17:00 " + System.lineSeparator()
            + "edit event allDay \"this test\""
            + " from 2025-03-10T16:00 to 2025-03-10T17:00 with true"
            + System.lineSeparator()
            + "print events from 2025-03-01T00:00 to 2025-03-16T14:00 " + System.lineSeparator()
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});

    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "List of events:" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_BLUE_DATA + "Name:this test StartDateTime:2025-03-10T00:00 "
            + "EndDateTime:2025-03-10T23:59 Location:" + System.lineSeparator()
            + ANSI_RESET_USER_INPUT
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }

  @Test
  public void testEditFromDateMatching() {
    InputStream in = new ByteArrayInputStream(("create event SingleEvent from 2021-03-01T12:00 "
            + "to 2021-03-01T13:00 location \"some loc\"" + System.lineSeparator()
            + "create event SingleEvent from 2021-03-02T12:00 to 2021-03-02T13:00 location \"old"
            + " loc\"" + System.lineSeparator()
            + "edit events location SingleEvent from 2021-03-01T10:00 with \"new loc\""
            + System.lineSeparator()
            + "print events from 2021-03-01T00:00 to 2021-03-03T00:00"
            + System.lineSeparator() + "exit").getBytes());
    CalendarController controller = new BasicCalendarController(model, view, in);
    controller.startApp(new String[]{"--mode", "interactive"});
    final String output = out.toString();
    assertEquals(ANSI_GREEN_MESSAGE
            + "You are in interactive mode, please enter your commands"
            + ANSI_RESET_USER_INPUT + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "List of events:" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_BLUE_DATA + "Name:SingleEvent StartDateTime:2021-03-01T12:00 "
            + "EndDateTime:2021-03-01T13:00 Location:new loc" + System.lineSeparator()
            + ANSI_BLUE_DATA
            + "Name:SingleEvent StartDateTime:2021-03-02T12:00 "
            + "EndDateTime:2021-03-02T13:00 Location:new loc" + System.lineSeparator()
            + ANSI_RESET_USER_INPUT
            + ANSI_GREEN_MESSAGE + "Command executed successfully" + ANSI_RESET_USER_INPUT
            + System.lineSeparator()
            + ANSI_GREEN_MESSAGE + "Goodbye!" + ANSI_RESET_USER_INPUT
            + System.lineSeparator(), output);
  }


}