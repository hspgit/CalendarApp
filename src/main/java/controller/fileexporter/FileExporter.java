package controller.fileexporter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import model.SingleCalendar;

/**
 * Interface for exporting calendar data to files. Implementations should handle exporting calendar
 * contents to different file formats.
 */
public interface FileExporter {

  /**
   * Exports a calendar's data to a file at the specified path.
   *
   * @param events all the events in the calendar to be exported
   * @param filePath the destination path where the file should be saved
   *
   * @return the Path object representing the created file
   *
   * @throws IOException if there's an error writing to the file
   */
  Path export(Map<String, String>[] events, String filePath) throws IOException;
}