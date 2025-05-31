import controller.features.Features;
import view.GuiView;

/**
 * MockGui is a mock implementation of the GuiView interface.
 * It appends method calls to a StringBuilder for testing purposes.
 */
public class MockGui implements GuiView {

  private final StringBuilder sb;

  /**
   * Constructor for MockGui.
   *
   * @param sb StringBuilder to append method calls to
   */
  public MockGui(StringBuilder sb) {
    this.sb = sb;
  }

  /**
   * Mock implementation of the addCalendar method.
   */
  @Override
  public void addFeatures(Features features) {
    sb.append("addFeatures called");
  }

  /**
   * Mock implementation of the setUp method.
   */
  @Override
  public void setUp() {
    sb.append("setUp called");

  }

  /**
   * Mock implementation of the refresh method.
   */
  @Override
  public void refresh() {
    sb.append("refresh called");

  }

  /**
   * Mock implementation of the displayError method.
   */
  @Override
  public void displayError(String message) {
    sb.append("displayError called with message: ").append(message);

  }

  /**
   * Mock implementation of the displayMessage method.
   */
  @Override
  public void displayMessage(String message) {
    sb.append("displayMessage called with message: ").append(message);
  }
}