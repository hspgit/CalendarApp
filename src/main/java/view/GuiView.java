package view;

import controller.features.Features;

/**
 * Interface for the graphical user interface view of the calendar application. Defines methods for
 * setting up, refreshing, and managing interactions with the view.
 */
public interface GuiView {

  /**
   * Adds features controller to the view to enable interaction with the model.
   *
   * @param features the features controller providing calendar functionality
   */
  void addFeatures(Features features);

  /**
   * Sets up the view components and initializes the user interface.
   */
  void setUp();

  /**
   * Refreshes the view to reflect the current state of the model.
   */
  void refresh();

  /**
   * Displays an error message to the user.
   *
   * @param message the error message to display
   */
  void displayError(String message);

  /**
   * Displays an informational message to the user.
   *
   * @param message the message to display
   */
  void displayMessage(String message);

}