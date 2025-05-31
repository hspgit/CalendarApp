import java.util.Set;

import static javax.swing.SwingUtilities.invokeLater;

import controller.BasicCalendarController;
import controller.CalendarController;
import controller.GuiCalendarController;
import model.BasicCalendarModel;
import model.CalendarModel;
import view.BasicCalendarView;
import view.CalendarView;
import view.GuiCalendarView;
import view.GuiView;

/**
 * The main class to start the calendar application.
 */
public class CalendarApp {

  private static final Set<String> validModes = Set.of("interactive", "headless");

  /**
   * The main method to start the calendar application. It validates the arguments and starts the
   * application by creating a model, view, and controller.
   *
   * @param args the arguments to start the application.
   */
  public static void main(String[] args) {
    final CalendarModel model = new BasicCalendarModel();
    final CalendarController controller;

    if (args.length == 0) { // run in gui mode
      final GuiView view = new GuiCalendarView("Calendar App");
      controller = new GuiCalendarController(model, view);
      // Run the GUI asynchronously without blocking
      invokeLater(() -> controller.startApp(args));
    } else {
      validateStartArgs(args);
      final CalendarView view = new BasicCalendarView(System.out);
      controller = new BasicCalendarController(model, view, System.in);
      controller.startApp(args);
    }
  }


  /**
   * Validates the arguments to start the application. It checks if the arguments are of the correct
   * length and if the first argument is "--mode" and the second argument is either "interactive" or
   * "headless". If the mode is "headless", it checks if the third argument for file name is
   * present.
   *
   * @param startAppArgs the arguments to start the application.
   */
  private static void validateStartArgs(String[] startAppArgs) {
    if (startAppArgs.length != 2 && startAppArgs.length != 3) {
      throw new IllegalArgumentException("Invalid length of arguments: expected 2 or 3 arguments "
              + "only, given " + startAppArgs.length + " arguments");
    }

    if (!"--mode".equals(startAppArgs[0])) {
      throw new IllegalArgumentException("Invalid first argument: " + startAppArgs[0]);
    }

    if (validModes.stream().noneMatch(startAppArgs[1]::equalsIgnoreCase)) {
      throw new IllegalArgumentException("Invalid mode: " + startAppArgs[1]);
    }

    if ("headless".equalsIgnoreCase(startAppArgs[1]) && startAppArgs.length != 3) {
      throw new IllegalArgumentException("Missing file name for headless mode");
    }

  }

}