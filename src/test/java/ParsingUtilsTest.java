import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import utils.ParsingUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for the ParsingUtils class.
 */
public class ParsingUtilsTest {

  @Test
  public void testParsingUtils() {
    ParsingUtilsTest ut = new ParsingUtilsTest();
    assertNotNull(ut);
  }


  // CSV file name validation tests
  @Test
  public void testIsValidCSVFileName() {
    // Valid cases
    assertTrue(ParsingUtils.isValidCSVFileName("events.csv"));
    assertTrue(ParsingUtils.isValidCSVFileName("calendar123.csv"));
    assertTrue(ParsingUtils.isValidCSVFileName("events_2023.csv"));
    assertTrue(ParsingUtils.isValidCSVFileName("my-calendar.csv"));
    assertTrue(ParsingUtils.isValidCSVFileName("events 2023.csv"));
    assertTrue(ParsingUtils.isValidCSVFileName("calendar.name.csv"));

    // Invalid cases
    assertFalse(ParsingUtils.isValidCSVFileName("events.txt"));    // Wrong extension
    assertFalse(ParsingUtils.isValidCSVFileName("events"));        // No extension
    assertFalse(ParsingUtils.isValidCSVFileName(".csv"));          // No filename
    assertFalse(ParsingUtils.isValidCSVFileName("event$.csv"));    // Invalid character
    assertFalse(ParsingUtils.isValidCSVFileName("event%.csv"));    // Invalid character
    assertFalse(ParsingUtils.isValidCSVFileName("events/2023.csv")); // Path separator
  }

  // Keyword value extraction tests
  @Test
  public void testGetKeyWordValue() {
    String[] args = {"create", "event", "\"Meeting", "with", "Team\"", "from",
        "2023-01-15T14:30"};
    Set<String> keywords = Set.of("from", "to", "description");

    // Standard extraction
    assertEquals("Meeting with Team",
            ParsingUtils.getKeyWordValue(args, 1, keywords));
    assertEquals("2023-01-15T14:30",
            ParsingUtils.getKeyWordValue(args, 5, keywords));

    // Test with empty value
    String[] emptyArgs = {"create", "event", "name", "from", "2023-01-15T14:30"};
    assertEquals("",
            ParsingUtils.getKeyWordValue(emptyArgs, 2, keywords));

    // Test with multi-word unquoted value
    String[] unquotedArgs = {"create", "event", "name", "Team", "Meeting",
        "from", "2023-01-15T14:30"};
    assertEquals("Team Meeting",
            ParsingUtils.getKeyWordValue(unquotedArgs, 2, keywords));
  }

  @Test
  public void testGetKeyWordValueWithQuotedText() {
    // Test with multipart quoted string
    String[] quotedArgs = {"create", "event", "description", "\"This", "is", "a", "description\"",
        "from", "2023-01-15T14:30"};
    Set<String> keywords = Set.of("name", "from", "to", "description");
    assertEquals("This is a description",
            ParsingUtils.getKeyWordValue(quotedArgs, 2, keywords));

  }


  @Test
  public void testUpdateArgMapWithMissingValues() {
    String[] args = {"create", "event", "name", "from", "to", "description"};
    Set<String> keywords = Set.of("name", "from", "to", "description");
    Map<String, String> argMap = new HashMap<>();

    ParsingUtils.updateArgMap(args, keywords, argMap);

    assertEquals("", argMap.get("name"));
    assertEquals("", argMap.get("from"));
    assertEquals("", argMap.get("to"));
    assertEquals("", argMap.get("description"));
  }

  @Test
  public void testEscapeCsv() {
    // Test with null value
    assertEquals("", ParsingUtils.escapeCsv(null));

    // Test with empty string
    assertEquals("\"\"", ParsingUtils.escapeCsv(""));

    // Test with simple string
    assertEquals("\"Hello World\"", ParsingUtils.escapeCsv("Hello World"));

    // Test with string containing double quotes
    assertEquals("\"He said \"\"Hello\"\"\"",
            ParsingUtils.escapeCsv("He said \"Hello\""));

    // Test with multiple quotes
    assertEquals("\"\"\"Quoted\"\" text with \"\"more\"\" quotes\"",
            ParsingUtils.escapeCsv("\"Quoted\" text with \"more\" quotes"));

    // Test with commas and other CSV-sensitive characters
    assertEquals("\"data,with,commas\"",
            ParsingUtils.escapeCsv("data,with,commas"));
  }


}
