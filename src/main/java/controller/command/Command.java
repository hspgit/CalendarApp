package controller.command;

/**
 * Interface for the Command pattern. The Command pattern is used to encapsulate a request as an
 * object, thereby allowing for parameterization of clients with different requests.
 */
public interface Command {

  /**
   * Executes the command.
   *
   * @param input the input to the command
   */
  void execute(String[] input);
}
