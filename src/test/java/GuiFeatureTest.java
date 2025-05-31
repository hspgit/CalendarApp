import controller.features.Features;
import controller.features.GuiFeatures;
import model.BasicCalendarModel;
import model.CalendarModel;
import view.GuiView;

import org.junit.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utils.DateTimeUtils.getAllAvailableTimezones;

/**
 * This class tests the GuiFeature class.
 */
public class GuiFeatureTest {


  @Test
  public void testRefreshView() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);

    features.refreshView();
    assertEquals("refresh called", viewSb.toString());
  }

  @Test
  public void testAddCalendar() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    String calendarName = "Test Calendar";
    String timezone = "UTC";
    boolean success = features.addCalendar(calendarName, timezone);
    assertTrue(success);
    assertEquals("addCalendar: Test Calendar, UTC", modelSb.toString());
    assertEquals("refresh called", viewSb.toString());
  }

  @Test
  public void testSelectCalendar() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    String calendarName = "Test Calendar";
    features.selectCalendar(calendarName);

    assertEquals("useCalendar: Test Calendar", modelSb.toString());
  }

  @Test
  public void testGetACurrentMonth() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    assertEquals(YearMonth.now(), features.getCurrentMonth());
  }

  @Test
  public void testGetAvailableZones() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    String[] actual = features.getAvailableTimezones();
    String[] expected = getAllAvailableTimezones();
    assertEquals(expected.length, actual.length);
  }

  @Test
  public void testGetEventCounts() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    List<List<String>> actual = features.getEventCountsInMonth(YearMonth.now());
    int[] expected = new int[YearMonth.now().lengthOfMonth()];
    assertEquals(expected.length, actual.size());
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected[i] + 1, actual.get(i).size()); // 1 mock event
    }

    assertTrue(modelSb.toString().contains("getCurrentCalendar"));
  }

  @Test
  public void testGetEventsOnDay() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    String dateString = LocalDate.now().toString();
    features.getEventDetailsOnDay(LocalDate.now());
    assertEquals("getCurrentCalendar: getEventsOnDate: "
            + dateString, modelSb.toString());
  }

  @Test
  public void testGetExactEvent() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    String eventName = "Test Event";
    String startDateTime = "2023-10-01T10:00:00";
    features.getExactEvent(eventName, startDateTime);
    assertEquals("getCurrentCalendar: getExactEvent: Test Event, 2023-10-01T10:00:00",
            modelSb.toString());
  }

  @Test
  public void testAddEventSingleEventInvalidName() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    boolean success = features.addEvent(eventDetails);
    assertEquals("displayError called with message: Event name cannot be empty",
            viewSb.toString());
    assertFalse(success);
  }

  @Test
  public void testAddEventSingleEventInvalidEmptyStart() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    boolean success = features.addEvent(eventDetails);
    assertEquals("displayError called with message: Start date and time cannot be empty",
            viewSb.toString());
    assertFalse(success);
  }

  @Test
  public void testAddEventSingleEventInvalidStartFormat() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("StartDateTime", "Test Start");
    boolean success = features.addEvent(eventDetails);
    assertEquals("displayError called with message: Invalid start date and time format",
            viewSb.toString());
    assertFalse(success);
  }

  @Test
  public void testAddEventSingleEventInvalidEmptyEnd() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("StartDateTime", "2025-10-01T10:00");
    boolean success = features.addEvent(eventDetails);
    assertEquals("displayError called with message: End date and time cannot be empty",
            viewSb.toString());
    assertFalse(success);
  }

  @Test
  public void testAddEventSingleEventInvalidEndFormat() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("StartDateTime", "2025-10-01T10:00");
    eventDetails.put("EndDateTime", "Test");
    boolean success = features.addEvent(eventDetails);
    assertEquals("displayError called with message: Invalid end date and time format",
            viewSb.toString());
    assertFalse(success);
  }

  @Test
  public void testAddEventSingleValid() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("StartDateTime", "2025-10-01T10:00");
    eventDetails.put("EndDateTime", "2025-10-01T11:00");
    eventDetails.put("Location", "Test Location");
    eventDetails.put("Description", "Test Description");
    eventDetails.put("IsPrivate", "Test Type");
    boolean success = features.addEvent(eventDetails);
    assertEquals("getCurrentCalendar: addSingleEvent: Test Event, 2025-10-01T10:00, "
                    + "2025-10-01T11:00, Test Description, Test Location, true, false",
            modelSb.toString());
    assertEquals("refresh called",
            viewSb.toString());
    assertTrue(success);
  }

  @Test
  public void testAddEventRecValidUntil() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("StartDateTime", "2025-10-01T10:00");
    eventDetails.put("EndDateTime", "2025-10-01T11:00");
    eventDetails.put("Location", "Test Location");
    eventDetails.put("Description", "Test Description");
    eventDetails.put("IsPrivate", "Test Type");
    eventDetails.put("IsRecurring", "true");
    eventDetails.put("RecurringDays", "Mon, Tue");
    eventDetails.put("RecurringEndType", "UntilDate");
    eventDetails.put("RecurringUntilDateTime", "2025-10-11T11:00");

    boolean success = features.addEvent(eventDetails);
    assertEquals("getCurrentCalendar: addRecurringEventUntil: Test Event, "
                    + "2025-10-01T10:00, 2025-10-01T11:00, Test Description, Test Location, MT, "
                    + "2025-10-11T11:00, true, false",
            modelSb.toString());
    assertEquals("refresh called",
            viewSb.toString());
    assertTrue(success);
  }

  @Test
  public void testAddEventRecInvalidEmptyUntil() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("StartDateTime", "2025-10-01T10:00");
    eventDetails.put("EndDateTime", "2025-10-01T11:00");
    eventDetails.put("Location", "Test Location");
    eventDetails.put("Description", "Test Description");
    eventDetails.put("IsPrivate", "Test Type");
    eventDetails.put("IsRecurring", "true");
    eventDetails.put("RecurringDays", "Mon, Tue");
    eventDetails.put("RecurringEndType", "UntilDate");
    // eventDetails.put("RecurringUntilDateTime", "2025-10-11T11:00");

    boolean success = features.addEvent(eventDetails);
    assertEquals("displayError called with message: Until date cannot be empty",
            viewSb.toString());
    assertFalse(success);
  }

  @Test
  public void testAddEventRecInvalidUntilFormat() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("StartDateTime", "2025-10-01T10:00");
    eventDetails.put("EndDateTime", "2025-10-01T11:00");
    eventDetails.put("Location", "Test Location");
    eventDetails.put("Description", "Test Description");
    eventDetails.put("IsPrivate", "Test Type");
    eventDetails.put("IsRecurring", "true");
    eventDetails.put("RecurringDays", "Mon, Tue");
    eventDetails.put("RecurringEndType", "UntilDate");
    eventDetails.put("RecurringUntilDateTime", "Invalid");

    boolean success = features.addEvent(eventDetails);
    assertEquals("displayError called with message: Invalid until date format",
            viewSb.toString());
    assertFalse(success);
  }

  @Test
  public void testAddEventRecValidFreq() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("StartDateTime", "2025-10-01T10:00");
    eventDetails.put("EndDateTime", "2025-10-01T11:00");
    eventDetails.put("Location", "Test Location");
    eventDetails.put("Description", "Test Description");
    eventDetails.put("IsPrivate", "Test Type");
    eventDetails.put("IsRecurring", "true");
    eventDetails.put("RecurringDays", "Mon, Tue");
    // eventDetails.put("RecurringEndType", "UntilDate");
    eventDetails.put("RecurringOccurrences", "5");

    boolean success = features.addEvent(eventDetails);
    assertEquals("getCurrentCalendar: addRecurringEventFrequency: Test Event, "
                    + "2025-10-01T10:00, 2025-10-01T11:00, Test Description, Test Location, MT, "
                    + "5, true, false",
            modelSb.toString());
    assertEquals("refresh called",
            viewSb.toString());
    assertTrue(success);
  }

  @Test
  public void testAddEventRecInvaliEmptyFreq() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("StartDateTime", "2025-10-01T10:00");
    eventDetails.put("EndDateTime", "2025-10-01T11:00");
    eventDetails.put("Location", "Test Location");
    eventDetails.put("Description", "Test Description");
    eventDetails.put("IsPrivate", "Test Type");
    eventDetails.put("IsRecurring", "true");
    eventDetails.put("RecurringDays", "Mon, Tue");
    eventDetails.put("RecurringOccurrences", "");

    boolean success = features.addEvent(eventDetails);
    assertEquals("displayError called with message: Invalid number of occurrences",
            viewSb.toString());
    assertFalse(success);
  }

  @Test
  public void testAddEventRecInvaliFreqNotNumber() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("StartDateTime", "2025-10-01T10:00");
    eventDetails.put("EndDateTime", "2025-10-01T11:00");
    eventDetails.put("Location", "Test Location");
    eventDetails.put("Description", "Test Description");
    eventDetails.put("IsPrivate", "Test Type");
    eventDetails.put("IsRecurring", "true");
    eventDetails.put("RecurringDays", "Mon, Tue");
    eventDetails.put("RecurringOccurrences", "Not number");
    boolean success = features.addEvent(eventDetails);
    assertEquals("displayError called with message: Invalid number of occurrences",
            viewSb.toString());
    assertFalse(success);
  }

  @Test
  public void testEditSingleName() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("StartDateTime", "2025-10-01T10:00");
    eventDetails.put("EndDateTime", "2025-10-01T11:00");
    eventDetails.put("selectedProperty", "Name");
    eventDetails.put("newValue", "New");

    boolean success = features.editSingleOccurrence(eventDetails);
    assertEquals("getCurrentCalendar: editSingleEvent: Test Event, 2025-10-01T10:00, "
            + "2025-10-01T11:00, name, New, true", modelSb.toString());
    assertEquals("refresh called", viewSb.toString());
    assertTrue(success);
  }

  @Test
  public void testEditSingleNameInvalid() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("StartDateTime", "2025-10-01T10:00");
    eventDetails.put("EndDateTime", "2025-10-01T11:00");
    eventDetails.put("selectedProperty", "Name");
    eventDetails.put("newValue", "  ");

    boolean success = features.editSingleOccurrence(eventDetails);
    assertEquals("displayError called with message: Event name cannot be empty",
            viewSb.toString());
    assertFalse(success);
  }

  @Test
  public void testEditSingleStartValid() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("StartDateTime", "2025-10-01T10:00");
    eventDetails.put("EndDateTime", "2025-10-01T11:00");
    eventDetails.put("selectedProperty", "Start Date Time");
    eventDetails.put("newValue", "10/10/2025 10:10 AM");

    boolean success = features.editSingleOccurrence(eventDetails);
    assertEquals("getCurrentCalendar: editSingleEvent: Test Event, 2025-10-01T10:00, "
            + "2025-10-01T11:00, startDateTime, 2025-10-10T10:10, true", modelSb.toString());
    assertEquals("refresh called", viewSb.toString());
    assertTrue(success);

  }

  @Test
  public void testEditSingleStartInValid() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("StartDateTime", "2025-10-01T10:00");
    eventDetails.put("EndDateTime", "2025-10-01T11:00");
    eventDetails.put("selectedProperty", "Start Date Time");
    eventDetails.put("newValue", "invalid");

    boolean success = features.editSingleOccurrence(eventDetails);
    assertEquals("displayError called with message: Invalid date format. "
            + "Expected format: MM/dd/yyyy hh:mm AM/PM", viewSb.toString());
    assertFalse(success);
  }

  @Test
  public void testEditSingleEndValid() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("StartDateTime", "2025-10-01T10:00");
    eventDetails.put("EndDateTime", "2025-10-01T11:00");
    eventDetails.put("selectedProperty", "End Date Time");
    eventDetails.put("newValue", "10/10/2025 10:10 AM");

    boolean success = features.editSingleOccurrence(eventDetails);
    assertEquals("getCurrentCalendar: editSingleEvent: Test Event, 2025-10-01T10:00, "
            + "2025-10-01T11:00, endDateTime, 2025-10-10T10:10, true", modelSb.toString());
    assertEquals("refresh called", viewSb.toString());
    assertTrue(success);
  }

  @Test
  public void testEditSingleEndInValid() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("StartDateTime", "2025-10-01T10:00");
    eventDetails.put("EndDateTime", "2025-10-01T11:00");
    eventDetails.put("selectedProperty", "End Date Time");
    eventDetails.put("newValue", "invalid");

    boolean success = features.editSingleOccurrence(eventDetails);
    assertEquals("displayError called with message: Invalid date format. "
            + "Expected format: MM/dd/yyyy hh:mm AM/PM", viewSb.toString());
    assertFalse(success);
  }

  @Test
  public void testEditSingleDescription() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("StartDateTime", "2025-10-01T10:00");
    eventDetails.put("EndDateTime", "2025-10-01T11:00");
    eventDetails.put("selectedProperty", "Description");
    eventDetails.put("newValue", "New");

    boolean success = features.editSingleOccurrence(eventDetails);
    assertEquals("getCurrentCalendar: editSingleEvent: Test Event, 2025-10-01T10:00, "
            + "2025-10-01T11:00, description, New, true", modelSb.toString());
    assertEquals("refresh called", viewSb.toString());
    assertTrue(success);
  }

  @Test
  public void testEditSingleLocation() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("StartDateTime", "2025-10-01T10:00");
    eventDetails.put("EndDateTime", "2025-10-01T11:00");
    eventDetails.put("selectedProperty", "Location");
    eventDetails.put("newValue", "New");

    boolean success = features.editSingleOccurrence(eventDetails);
    assertEquals("getCurrentCalendar: editSingleEvent: Test Event, 2025-10-01T10:00, "
            + "2025-10-01T11:00, location, New, true", modelSb.toString());
    assertEquals("refresh called", viewSb.toString());
    assertTrue(success);
  }

  @Test
  public void testEditSinglePrivate() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("StartDateTime", "2025-10-01T10:00");
    eventDetails.put("EndDateTime", "2025-10-01T11:00");
    eventDetails.put("selectedProperty", "Private");
    eventDetails.put("newValue", "True");

    boolean success = features.editSingleOccurrence(eventDetails);
    assertEquals("getCurrentCalendar: editSingleEvent: Test Event, 2025-10-01T10:00, "
            + "2025-10-01T11:00, private, true, true", modelSb.toString());
    assertEquals("refresh called", viewSb.toString());
    assertTrue(success);
  }

  @Test
  public void testEditSinglePrivateInvalid() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("StartDateTime", "2025-10-01T10:00");
    eventDetails.put("EndDateTime", "2025-10-01T11:00");
    eventDetails.put("selectedProperty", "Private");
    eventDetails.put("newValue", "Invalid");

    boolean success = features.editSingleOccurrence(eventDetails);
    assertEquals("displayError called with message: Private value must be true or false",
            viewSb.toString());
    assertFalse(success);
  }

  @Test
  public void testEditSingleAllDay() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("StartDateTime", "2025-10-01T10:00");
    eventDetails.put("EndDateTime", "2025-10-01T11:00");
    eventDetails.put("selectedProperty", "All Day");

    boolean success = features.editSingleOccurrence(eventDetails);
    assertEquals("getCurrentCalendar: editSingleEvent: Test Event, 2025-10-01T10:00, "
            + "2025-10-01T11:00, allDay, true, true", modelSb.toString());
    assertEquals("refresh called", viewSb.toString());
    assertTrue(success);
  }

  @Test
  public void testEditMultipleFreqAll() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("selectedProperty", "Frequency");
    eventDetails.put("newValue", "5");
    eventDetails.put("followingEventsValue", "");

    boolean success = features.editMultipleOccurrences(eventDetails);
    assertEquals("getCurrentCalendar: editMultipleEventsAll: Test Event, frequency, "
            + "5, true", modelSb.toString());
    assertEquals("refresh called", viewSb.toString());
    assertTrue(success);
  }

  @Test
  public void testEditMultipleUntil() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("selectedProperty", "Until Date Time");
    eventDetails.put("newValue", "10/01/2025 11:00 AM");
    eventDetails.put("followingEventsValue", "");

    boolean success = features.editMultipleOccurrences(eventDetails);
    assertEquals("getCurrentCalendar: editMultipleEventsAll: Test Event, "
            + "untilDateTime, 2025-10-01T11:00, true", modelSb.toString());
    assertEquals("refresh called", viewSb.toString());
    assertTrue(success);
  }

  @Test
  public void testEditMultipleAllWeekday() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> eventDetails = new HashMap<>();
    eventDetails.put("Name", "Test Event");
    eventDetails.put("selectedProperty", "Weekdays ('MTWRFSU')");
    eventDetails.put("newValue", "MT");
    eventDetails.put("followingEventsValue", "");

    boolean success = features.editMultipleOccurrences(eventDetails);
    assertEquals("getCurrentCalendar: editMultipleEventsAll: Test Event, "
            + "weekDays, MT, true", modelSb.toString());
    assertEquals("refresh called", viewSb.toString());
    assertTrue(success);
  }

  @Test
  public void testGetAllCalendars() {
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new BasicCalendarModel();
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    Map<String, String> calendars = features.getAllCalendars();
    assertEquals(1, calendars.size()); // default calendar
  }

  @Test
  public void testGetCurrentCalendar() {
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new BasicCalendarModel();
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    String calendarName = features.getCurrentCalendarName();
    assertEquals("default", calendarName); // default calendar
  }

  @Test
  public void testAddSameCalendar() {
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new BasicCalendarModel();
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    String calendarName = "default";
    String timezone = "UTC";
    boolean success = features.addCalendar(calendarName, timezone);
    assertFalse(success);
    assertEquals("displayError called with message: Calendar already exists: default",
            viewSb.toString());
  }

  @Test
  public void testGetEventDetails() {
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new BasicCalendarModel();
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    model.getCurrentCalendar().addSingleEventAllDay(
            "Test Event",
            "2023-10-01",
            "Test Location",
            "Test Description",
            true,
            false);
    List<Map<String, String>> result = features
            .getEventDetailsOnDay(LocalDate.of(2023, 10, 1));

    assertEquals(1, result.size());
  }

  @Test
  public void testGetExactEventReal() {
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new BasicCalendarModel();
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    model.getCurrentCalendar().addSingleEventAllDay(
            "Test Event",
            "2023-10-01",
            "Test Description",
            "Test Location",
            true,
            false);
    Map<String, String> result = features.getExactEvent(
            "Test Event",
            "2023-10-01T00:00:00"
    );
    assertEquals("Test Event", result.get("Name"));
    assertEquals("10/01/2023 12:00 AM", result.get("Start Date Time"));
    assertEquals("10/01/2023 11:59 PM", result.get("End Date Time"));
    assertEquals("Test Location", result.get("Location"));
    assertEquals("Test Description", result.get("Description"));
    assertEquals("false", result.get("Private"));
    assertEquals("true", result.get("All Day"));
    assertEquals("False", result.get("Is Recurring"));
  }

  @Test
  public void testAddSingleFailed() {
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new BasicCalendarModel();
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    model.getCurrentCalendar().addSingleEventAllDay(
            "Test Event",
            "2023-10-01",
            "Test Location",
            "Test Description",
            true,
            false);
    boolean success = features.addEvent(new HashMap<>(
            Map.of("Name", "Test Event",
                    "StartDateTime", "2023-10-01T10:00",
                    "EndDateTime", "2023-10-01T11:59",
                    "Location", "Test Location",
                    "Description", "Test Description",
                    "IsPrivate", "false",
                    "IsRecurring", "false"
            )));
    assertFalse(success);
    assertEquals("displayError called with message: Conflict detected, Event not Created",
            viewSb.toString());

  }

  @Test
  public void testAddRecFailed() {
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new BasicCalendarModel();
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    model.getCurrentCalendar().addSingleEventAllDay(
            "Test Event",
            "2025-04-09",
            "Test Location",
            "Test Description",
            true,
            false);
    boolean success = features.addEvent(new HashMap<>(
            Map.of("Name", "Test Event",
                    "StartDateTime", "2025-04-09T10:00",
                    "EndDateTime", "2025-04-09T11:59",
                    "Location", "Test Location",
                    "Description", "Test Description",
                    "IsPrivate", "false",
                    "IsRecurring", "True",
                    "RecurringDays", "Wed",
                    "RecurringEndType", "NumberOfTimes",
                    "RecurringOccurrences", "5"
            )));
    assertFalse(success);
    assertEquals("displayError called with message: Conflict detected, Event not Created",
            viewSb.toString());

  }

  @Test
  public void testEditSingleFailed() {
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new BasicCalendarModel();
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    model.getCurrentCalendar().addSingleEvent(
            "Test Event",
            "2025-04-09T10:00",
            "2025-04-09T11:00",
            "Test Description",
            "Test Location",
            true,
            false);
    Map<String, String> editDetails = new HashMap<>();
    editDetails.put("Name", "Test Event");
    editDetails.put("StartDateTime", "2025-04-09T10:00");
    editDetails.put("EndDateTime", "2025-04-09T11:00");
    editDetails.put("selectedProperty", "Start Date Time");
    editDetails.put("newValue", "04/10/2025 10:00 AM");
    boolean success = features.editSingleOccurrence(editDetails);
    assertFalse(success);
    assertEquals("displayError called with message: Start date time cannot "
            + "be after end date time", viewSb.toString());
  }

  @Test
  public void testEditMultipleFollowingSuccess() {
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new BasicCalendarModel();
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    model.getCurrentCalendar().addSingleEvent(
            "Test Event Single",
            "2025-04-11T12:00",
            "2025-04-11T13:00",
            "Test Description",
            "Test Location",
            true,
            false);
    model.getCurrentCalendar().addRecurringEventUntil(
            "Test Event",
            "2025-04-09T10:00",
            "2025-04-09T11:00",
            "Test Description",
            "Test Location",
            "MTWRFSU",
            "2025-04-19T11:00",
            false,
            false);
    Map<String, String> editDetails = new HashMap<>();
    editDetails.put("Name", "Test Event");
    editDetails.put("selectedProperty", "End Date Time");
    editDetails.put("newValue", "04/10/2025 11:30 AM");
    editDetails.put("followingEventsValue", "2025-04-10T10:00");
    boolean success = features.editMultipleOccurrences(editDetails);
    assertTrue(success);
    assertEquals("refresh called", viewSb.toString());
  }

  @Test
  public void testEditMultipleFollowingFailed() {
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new BasicCalendarModel();
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    model.getCurrentCalendar().addSingleEvent(
            "Test Event Single",
            "2025-04-11T12:00",
            "2025-04-11T13:00",
            "Test Description",
            "Test Location",
            true,
            false);
    model.getCurrentCalendar().addRecurringEventUntil(
            "Test Event",
            "2025-04-09T10:00",
            "2025-04-09T11:00",
            "Test Description",
            "Test Location",
            "MTWRFSU",
            "2025-04-19T11:00",
            false,
            false);
    Map<String, String> editDetails = new HashMap<>();
    editDetails.put("Name", "Test Event");
    editDetails.put("selectedProperty", "End Date Time");
    editDetails.put("newValue", "04/10/2025 01:00 PM");
    editDetails.put("followingEventsValue", "2025-04-10T10:00");
    boolean success = features.editMultipleOccurrences(editDetails);
    assertFalse(success);
    assertEquals("displayError called with message: Event conflicts with "
            + "existing event", viewSb.toString());
  }

  @Test
  public void testEditMultipleAllSuccess() {
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new BasicCalendarModel();
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    model.getCurrentCalendar().addSingleEvent(
            "Test Event Single",
            "2025-04-11T12:00",
            "2025-04-11T13:00",
            "Test Description",
            "Test Location",
            true,
            false);
    model.getCurrentCalendar().addRecurringEventUntil(
            "Test Event",
            "2025-04-09T10:00",
            "2025-04-09T11:00",
            "Test Description",
            "Test Location",
            "MTWRFSU",
            "2025-04-19T11:00",
            false,
            false);
    Map<String, String> editDetails = new HashMap<>();
    editDetails.put("Name", "Test Event");
    editDetails.put("selectedProperty", "End Date Time");
    editDetails.put("newValue", "04/10/2025 11:30 AM");
    editDetails.put("followingEventsValue", "");
    boolean success = features.editMultipleOccurrences(editDetails);
    assertTrue(success);
    assertEquals("refresh called", viewSb.toString());
  }

  @Test
  public void testEditMultipleAllFailed() {
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new BasicCalendarModel();
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    model.getCurrentCalendar().addSingleEvent(
            "Test Event Single",
            "2025-04-11T12:00",
            "2025-04-11T13:00",
            "Test Description",
            "Test Location",
            true,
            false);
    model.getCurrentCalendar().addRecurringEventUntil(
            "Test Event",
            "2025-04-09T10:00",
            "2025-04-09T11:00",
            "Test Description",
            "Test Location",
            "MTWRFSU",
            "2025-04-19T11:00",
            false,
            false);
    Map<String, String> editDetails = new HashMap<>();
    editDetails.put("Name", "Test Event");
    editDetails.put("selectedProperty", "End Date Time");
    editDetails.put("newValue", "04/10/2025 01:00 PM");
    editDetails.put("followingEventsValue", "");
    boolean success = features.editMultipleOccurrences(editDetails);
    assertFalse(success);
    assertEquals("displayError called with message: Event conflicts with "
            + "existing event", viewSb.toString());
  }

  @Test
  public void testExportCsv() {
    StringBuilder modelSb = new StringBuilder();
    StringBuilder viewSb = new StringBuilder();
    CalendarModel model = new MockModelTest.MockModel(modelSb);
    GuiView guiView = new MockGui(viewSb);
    Features features = new GuiFeatures(model, guiView);
    String fileName = "test.csv";
    features.exportCSV(fileName);

    // Dynamically construct the expected message
    String expectedPath = new java.io.File(fileName).getAbsolutePath();
    assertEquals("getCurrentCalendar: getAllEvents: ", modelSb.toString());
    assertEquals("displayMessage called with message: Successfully exported to file:"
            + expectedPath, viewSb.toString());

    java.io.File file = new java.io.File(fileName);
    if (file.exists()) {
      boolean deleted = file.delete();
      assertTrue("File should be deleted successfully", deleted);
    }

  }

}
