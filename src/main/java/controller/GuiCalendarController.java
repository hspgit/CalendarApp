package controller;

import controller.features.Features;
import controller.features.GuiFeatures;
import model.CalendarModel;
import view.GuiView;

/**
 * Controller implementation for GUI-based calendar applications. Acts as the mediator between the
 * calendar model and the GUI view, setting up the necessary components and initializing the
 * application.
 */
public class GuiCalendarController implements CalendarController {

  private final CalendarModel model;
  private final GuiView view;

  /**
   * Constructor for the GuiCalendarController class. Initializes the controller with the given view
   * and model.
   *
   * @param model the calendar model that contains the application data
   * @param view  the GUI view that displays the calendar interface
   */
  public GuiCalendarController(CalendarModel model, GuiView view) {
    this.model = model;
    this.view = view;
  }

  /**
   * Sets up the controller by initializing the features and view. Creates the features object and
   * connects it to the view, then initiates the view setup process.
   */
  private void setup() {
    Features features = new GuiFeatures(model, view);
    view.addFeatures(features);
    view.setUp();
  }

  /**
   * Starts the application with the specified arguments. This method initializes the application by
   * calling the setup method.
   *
   * @param startAppArgs command-line arguments passed to the application
   */
  @Override
  public void startApp(String[] startAppArgs) {
    setup();
  }
}