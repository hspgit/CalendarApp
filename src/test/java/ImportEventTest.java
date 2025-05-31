import controller.features.Features;

import java.util.Map;

import model.BasicCalendarModel;
import model.CalendarModel;

import org.junit.Test;

import controller.features.GuiFeatures;

import static org.junit.Assert.assertEquals;

/**
 * Test class for ImportEvent functionality.
 */
public class ImportEventTest {

  private final CalendarModel calendarModel;
  private final Features features;
  private final StringBuilder sb;

  /**
   * Constructor for ImportEventTest. Initializes the calendar model and GUI features.
   */
  public ImportEventTest() {
    calendarModel = new BasicCalendarModel();
    sb = new StringBuilder();
    features = new GuiFeatures(calendarModel, new MockGui(sb));
  }

  @Test
  public void testSimpleImportEvent() {
    String filePath = "./scripts/test_importAllValid.csv";
    features.importCSV(filePath);
    Map<String, String>[] events = calendarModel.getCurrentCalendar()
            .getEventsRange("2025-04-01T08:00", "2025-04-10T08:00");
    assertEquals(6, events.length);
    assertEquals("ML", events[0].get("Name"));
    assertEquals("2025-04-01T00:00", events[0].get("StartDateTime"));
    assertEquals("2025-04-01T23:59", events[0].get("EndDateTime"));

    assertEquals("PDP", events[1].get("Name"));
    assertEquals("2025-04-02T13:00", events[1].get("StartDateTime"));
    assertEquals("2025-04-02T14:00", events[1].get("EndDateTime"));

    assertEquals("ML", events[2].get("Name"));
    assertEquals("2025-04-04T09:00", events[2].get("StartDateTime"));
    assertEquals("2025-04-04T10:00", events[2].get("EndDateTime"));

    assertEquals("PDP", events[3].get("Name"));
    assertEquals("2025-04-04T13:00", events[3].get("StartDateTime"));
    assertEquals("2025-04-04T14:00", events[3].get("EndDateTime"));

    assertEquals("ML", events[4].get("Name"));
    assertEquals("2025-04-08T09:00", events[4].get("StartDateTime"));
    assertEquals("2025-04-08T10:00", events[4].get("EndDateTime"));

    assertEquals("PDP", events[5].get("Name"));
    assertEquals("2025-04-08T13:00", events[5].get("StartDateTime"));
    assertEquals("2025-04-08T14:00", events[5].get("EndDateTime"));

    assertEquals("displayMessage called with message: Successfully added "
            + "6 out of 6 events", sb.toString());
  }

  @Test
  public void testCorrectImportConflictsEvent() {
    String filePath = "./scripts/test_importAllValid.csv";
    calendarModel.getCurrentCalendar().addSingleEvent(
            "Event1",
            "2025-04-01T09:00",
            "2025-04-01T10:00",
            "Description",
            "Location",
            false,
            false);

    Map<String, String>[] events = calendarModel.getCurrentCalendar()
            .getEventsRange("2025-04-01T08:00", "2025-04-10T08:00");
    assertEquals(1, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-04-01T09:00", events[0].get("StartDateTime"));
    assertEquals("2025-04-01T10:00", events[0].get("EndDateTime"));

    features.importCSV(filePath);

    events = calendarModel.getCurrentCalendar()
            .getEventsRange("2025-04-01T08:00", "2025-04-10T08:00");
    assertEquals(6, events.length);
    assertEquals("Event1", events[0].get("Name"));
    assertEquals("2025-04-01T09:00", events[0].get("StartDateTime"));
    assertEquals("2025-04-01T10:00", events[0].get("EndDateTime"));

    assertEquals("PDP", events[1].get("Name"));
    assertEquals("2025-04-02T13:00", events[1].get("StartDateTime"));
    assertEquals("2025-04-02T14:00", events[1].get("EndDateTime"));

    assertEquals("ML", events[2].get("Name"));
    assertEquals("2025-04-04T09:00", events[2].get("StartDateTime"));
    assertEquals("2025-04-04T10:00", events[2].get("EndDateTime"));

    assertEquals("PDP", events[3].get("Name"));
    assertEquals("2025-04-04T13:00", events[3].get("StartDateTime"));
    assertEquals("2025-04-04T14:00", events[3].get("EndDateTime"));

    assertEquals("ML", events[4].get("Name"));
    assertEquals("2025-04-08T09:00", events[4].get("StartDateTime"));
    assertEquals("2025-04-08T10:00", events[4].get("EndDateTime"));

    assertEquals("PDP", events[5].get("Name"));
    assertEquals("2025-04-08T13:00", events[5].get("StartDateTime"));
    assertEquals("2025-04-08T14:00", events[5].get("EndDateTime"));
    assertEquals("displayMessage called with message: Successfully added "
            + "5 out of 6 events", sb.toString());

  }

  @Test
  public void testInValidCsvFile() {
    String filePath = "./scripts/test_importSomeInValid.csv";
    features.importCSV(filePath);

    Map<String, String>[] events = calendarModel.getCurrentCalendar()
            .getEventsRange("2025-04-01T08:00", "2025-04-10T08:00");

    assertEquals(5, events.length);

    assertEquals("ML", events[0].get("Name"));
    assertEquals("2025-04-01T09:00", events[0].get("StartDateTime"));
    assertEquals("2025-04-01T10:00", events[0].get("EndDateTime"));

    assertEquals("PDP", events[1].get("Name"));
    assertEquals("2025-04-01T13:00", events[1].get("StartDateTime"));
    assertEquals("2025-04-01T14:00", events[1].get("EndDateTime"));

    assertEquals("ML", events[2].get("Name"));
    assertEquals("2025-04-04T09:00", events[2].get("StartDateTime"));
    assertEquals("2025-04-04T10:00", events[2].get("EndDateTime"));

    assertEquals("PDP", events[1].get("Name"));
    assertEquals("2025-04-04T13:00", events[3].get("StartDateTime"));
    assertEquals("2025-04-04T14:00", events[3].get("EndDateTime"));

    assertEquals("ML", events[4].get("Name"));
    assertEquals("2025-04-08T09:00", events[4].get("StartDateTime"));
    assertEquals("2025-04-08T10:00", events[4].get("EndDateTime"));

    assertEquals("displayMessage called with message: Successfully "
            + "added 5 out of 6 events", sb.toString());
  }


  @Test
  public void testSomeInValidCsvWithFile() {
    String filePath = "./scripts/test_importMultipleInvalid.csv";
    features.importCSV(filePath);

    Map<String, String>[] events = calendarModel.getCurrentCalendar()
            .getEventsRange("2025-04-01T08:00", "2025-04-10T08:00");

    assertEquals(4, events.length);

    assertEquals("ML", events[0].get("Name"));
    assertEquals("2025-04-01T09:00", events[0].get("StartDateTime"));
    assertEquals("2025-04-01T10:00", events[0].get("EndDateTime"));

    assertEquals("PDP", events[1].get("Name"));
    assertEquals("2025-04-01T13:00", events[1].get("StartDateTime"));
    assertEquals("2025-04-01T14:00", events[1].get("EndDateTime"));

    assertEquals("ML", events[2].get("Name"));
    assertEquals("2025-04-04T09:00", events[2].get("StartDateTime"));
    assertEquals("2025-04-04T10:00", events[2].get("EndDateTime"));

    assertEquals("PDP", events[3].get("Name"));
    assertEquals("2025-04-04T13:00", events[3].get("StartDateTime"));
    assertEquals("2025-04-04T14:00", events[3].get("EndDateTime"));

    assertEquals("displayMessage called with message: Successfully added "
            + "4 out of 6 events", sb.toString());
  }

  @Test
  public void testSimpleWithSomeSpacesImportEvent() {
    String filePath = "./scripts/test_importAllValidWithEmptyLines.csv";
    features.importCSV(filePath);
    Map<String, String>[] events = calendarModel.getCurrentCalendar()
            .getEventsRange("2025-04-01T08:00", "2025-04-10T08:00");
    assertEquals(6, events.length);
    assertEquals("ML", events[0].get("Name"));
    assertEquals("2025-04-01T09:00", events[0].get("StartDateTime"));
    assertEquals("2025-04-01T10:00", events[0].get("EndDateTime"));

    assertEquals("PDP", events[1].get("Name"));
    assertEquals("2025-04-01T13:00", events[1].get("StartDateTime"));
    assertEquals("2025-04-01T14:00", events[1].get("EndDateTime"));

    assertEquals("ML", events[2].get("Name"));
    assertEquals("2025-04-04T09:00", events[2].get("StartDateTime"));
    assertEquals("2025-04-04T10:00", events[2].get("EndDateTime"));

    assertEquals("PDP", events[3].get("Name"));
    assertEquals("2025-04-04T13:00", events[3].get("StartDateTime"));
    assertEquals("2025-04-04T14:00", events[3].get("EndDateTime"));

    assertEquals("ML", events[4].get("Name"));
    assertEquals("2025-04-08T09:00", events[4].get("StartDateTime"));
    assertEquals("2025-04-08T10:00", events[4].get("EndDateTime"));

    assertEquals("PDP", events[5].get("Name"));
    assertEquals("2025-04-08T13:00", events[5].get("StartDateTime"));
    assertEquals("2025-04-08T14:00", events[5].get("EndDateTime"));

    assertEquals("displayMessage called with message: Successfully "
            + "added 6 out of 6 events", sb.toString());

  }


  @Test
  public void testInvalidPath() {
    String filePath = "./scripts/InvalidPath.csv";
    try {
      features.importCSV(filePath);
    } catch (Exception e) {
      assertEquals("java.io.FileNotFoundException: "
              + "./scripts/InvalidPath.csv (No such file or directory)", e.getMessage());
    }
  }

}
