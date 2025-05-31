package controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Supplier;

import controller.command.Command;
import controller.command.CopyCommand;
import controller.command.CreateCommand;
import controller.command.EditCommand;
import controller.command.ExportCommand;
import controller.command.PrintCommand;
import controller.command.ShowCommand;
import controller.command.UseCommand;
import model.CalendarModel;
import view.CalendarView;

/**
 * The BasicCalendarController class is an implementation of the CalendarController interface. This
 * class is used to control the flow of the application. It takes in user input and processes it
 * accordingly.
 */
public class BasicCalendarController implements CalendarController {

  /**
   * The view object that the controller will interact with.
   */
  private final CalendarView view;

  /**
   * The model object that the controller will interact with.
   */
  private final CalendarModel model;
  /**
   * A map that maps the command name to the command object.
   */
  private final Map<String, Supplier<Command>> commandMap;
  /**
   * The scanner object that the controller will use to take in user input.
   */
  private Scanner scanner;
  /**
   * The mode of the application. Can be either "interactive" or "headless".
   */
  private String mode;

  /**
   * The name of the file that the application is reading from in headless mode.
   */
  private String fileName;

  /**
   * Constructor for the BasicCalendarController class. Initializes the controller with the given
   * model, view, and input stream. Also initializes the command map. Sets the mode to "interactive"
   * and the file name to null.
   *
   * @param model the model object that the controller will interact with.
   * @param view  the view object that the controller will interact with.
   * @param input the input stream that the controller will use to take in user input.
   */
  public BasicCalendarController(CalendarModel model, CalendarView view, InputStream input) {
    this.model = model;
    this.view = view;
    this.scanner = new Scanner(input);
    this.commandMap = initializeCommands();
    this.mode = "interactive";
    this.fileName = null;
  }


  /**
   * Method to start the application. Sets up the application in correct mode and starts the
   * application.
   *
   * @param startAppArgs the arguments to start the application with.
   */
  @Override
  public void startApp(final String[] startAppArgs) {
    setMode(startAppArgs);
    handleMode();
  }

  /**
   * Method to set the mode of the application. Sets the mode of the application to either
   * "interactive" or "headless" based on the given arguments.
   *
   * @param startAppArgs the arguments to start the application with.
   */
  private void setMode(final String[] startAppArgs) {

    switch (startAppArgs[1].toLowerCase()) {
      case "interactive":
        this.mode = "interactive";
        break;
      case "headless":
        this.mode = "headless";
        this.fileName = startAppArgs[2];
        break;
      default:
        throw new IllegalArgumentException("Invalid mode: " + startAppArgs[1]);
    }
  }

  /**
   * Method to process the user input. Processes the user input by splitting the input into an array
   * of strings and then executing the command based on the first element of the array. If the
   * command is not found, an error message is displayed.
   *
   * @param input the user input to process.
   *
   * @throws IllegalArgumentException if the command is not found or if the command execution fails.
   *                                  Only thrown in headless mode to stop the application. Else the
   *                                  user is prompted to enter a new command.
   */
  private void processInput(final String input) {
    final String[] inputArray = input.split(" ");
    final Supplier<Command> command = commandMap.getOrDefault(inputArray[0], null);
    if (command != null) {
      try {
        command.get().execute(inputArray);
        view.displayMessage("Command executed successfully");
      } catch (IllegalArgumentException e) {
        view.displayError(e.getMessage());
        if (this.mode.equals("headless")) {
          throw new IllegalArgumentException(
                  "Error: " + e.getMessage() + System.lineSeparator() + " for command: " + input);
        }
      }
    } else {
      view.displayError("Unknown command: " + input);
      if (this.mode.equals("headless")) {
        throw new IllegalArgumentException("Unknown command: " + input);
      }
    }
  }

  /**
   * Method to initialize the command map. Initializes the command map with the commands that the
   * controller can execute. Uses Supplier functional interface to create the command objects
   * lazily.
   *
   * @return the map of commands that the controller can execute.
   */
  private Map<String, Supplier<Command>> initializeCommands() {
    Map<String, Supplier<Command>> commands = new HashMap<>();
    commands.put("create", () -> new CreateCommand(model));
    commands.put("edit", () -> new EditCommand(model));
    commands.put("print", () -> new PrintCommand(model, view));
    commands.put("export", () -> new ExportCommand(model, view));
    commands.put("show", () -> new ShowCommand(model, view));
    commands.put("use", () -> new UseCommand(model));
    commands.put("copy", () -> new CopyCommand(model));
    return commands;
  }

  /**
   * Handles the mode of the application by displaying the appropriate message based on the mode. If
   * the mode is interactive, the user is prompted to enter commands. If the mode is headless, the
   * user is prompted to wait for the output. Changes the scanner to read from the file in headless
   * mode.
   */
  private void handleMode() {
    if (this.mode.equals("interactive")) {
      view.displayMessage("You are in interactive mode, please enter your commands");
    } else if (this.mode.equals("headless")) {
      view.displayMessage("You are in headless mode, please wait for the output");
      changeScanner();
    }
    processInputLoop();
  }

  /**
   * Changes the scanner to read from the file in headless mode. If the file is not found, an error
   * message is displayed.
   */
  private void changeScanner() {
    try {
      this.scanner = new Scanner(Files.newInputStream(Paths.get(this.fileName)));
    } catch (IOException e) {
      throw new IllegalArgumentException(
              "Error (No such file or directory): " + e.getMessage());
    }
  }

  /**
   * Processes the user input in a loop until the user enters "exit". If the mode is headless, the
   * file must end with "exit" to stop the application.
   */
  private void processInputLoop() {
    String userInput;
    while (true) {
      userInput = scanner.nextLine().trim();
      if (userInput.isEmpty()) {
        continue;
      }

      if (userInput.equalsIgnoreCase("exit")) {
        view.displayMessage("Goodbye!");
        break;
      }

      processInput(userInput);

      if (this.mode.equals("headless") && !scanner.hasNextLine()) {
        throw new IllegalArgumentException("Error: File must end with 'exit'");
      }
    }
  }

}
