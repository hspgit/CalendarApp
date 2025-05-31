import org.junit.Test;

import controller.CalendarController;
import controller.GuiCalendarController;
import model.BasicCalendarModel;
import model.CalendarModel;
import view.GuiView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * This class tests the GuiCalendarController class.
 */
public class GuiCalendarControllerTest {

  StringBuilder viewOutput = new StringBuilder();

  @Test
  public void testConstructor() {
    CalendarModel calendarModel = new BasicCalendarModel();
    GuiView mockGui = new MockGui(viewOutput);
    CalendarController controller = new GuiCalendarController(calendarModel, mockGui);
    assertNotNull(controller);
  }

  @Test
  public void testSetup() {
    CalendarModel calendarModel = new BasicCalendarModel();
    GuiView mockGui = new MockGui(viewOutput);
    CalendarController controller = new GuiCalendarController(calendarModel, mockGui);
    controller.startApp(new String[0]);
    assertEquals("addFeatures calledsetUp called", viewOutput.toString());
  }
}
