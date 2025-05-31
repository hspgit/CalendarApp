package controller.fileexporter;

import static utils.ParsingUtils.isValidCSVFileName;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Map;
import model.SingleCalendar;

/**
 * Implementation of the FileExporter interface for CSV files. Exports calendar data in CSV format
 * compatible with calendar applications.
 */
public class CsvFileExporter implements FileExporter {

  /**
   * Exports a calendar's data to a CSV file at the specified path. Creates the output file and
   * parent directories if they don't exist. The CSV file includes a header row and one row per
   * calendar event.
   *
   * @param events all the events in the calendar to be exported
   * @param filePath the destination path where the CSV file should be saved
   *
   * @return the Path object representing the created CSV file
   *
   * @throws IOException              if there's an error writing to the file
   * @throws IllegalArgumentException if the specified file name is not a valid CSV file name
   */
  @Override
  public Path export(Map<String, String>[] events, String filePath) throws IOException {
    String separator = File.separator;
    String fileName = filePath.split(separator)[filePath.split(separator).length - 1];
    if (!isValidCSVFileName(fileName)) {
      throw new IllegalArgumentException("Invalid CSV file name:" + filePath);
    }
    Path path = Paths.get(filePath);
    if (!path.isAbsolute()) {
      path = path.toAbsolutePath();
    }
    try {
      if (!Files.exists(path)) {
        Files.createDirectories(path.getParent());
        Files.createFile(path);
      }
      String header = "Subject,Start Date,Start Time,End Date,End Time,"
              + "All Day Event,Description,Location,Private";
      String[] allLines = new String[events.length + 1];
      allLines[0] = header;
      String[] formattedEvents = convertAllMapDetails(events);
      System.arraycopy(formattedEvents, 0, allLines, 1, events.length);
      Files.write(path, String.join(System.lineSeparator(), allLines).getBytes());

    } catch (IOException e) {
      throw new IOException("Error exporting file: " + e.getMessage());
    }
    return path;
  }

  private String[] convertAllMapDetails(Map<String, String>[] events) {
    String[] formattedEvents = new String[events.length];
    for (int i = 0; i < events.length; i++) {
      formattedEvents[i] = convertMapToFormattedString(events[i]);
    }
    return formattedEvents;
  }

  private String convertMapToFormattedString(Map<String, String> eventDetails) {


    return String.join(",",
        escapeCsv(eventDetails.getOrDefault("Name", "")), // Subject
        eventDetails.getOrDefault("StartDate",""), // Start Date
        eventDetails.getOrDefault("StartTime",""), // Start Time
        eventDetails.getOrDefault("EndDate",""), // End Date
        eventDetails.getOrDefault("EndTime",""),  // End Time
        eventDetails.getOrDefault("IsAllDay", ""), // All Day Event
        escapeCsv(eventDetails.getOrDefault("Description", "")), // Description
        escapeCsv(eventDetails.getOrDefault("Location", "")), // Location
        eventDetails.getOrDefault("IsPrivate", "") // Private
    );
  }

  // Utility method to escape CSV values
  private String escapeCsv(String value) {
    if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
      value = "\"" + value.replace("\"", "\"\"") + "\"";
    }
    return value;
  }
}