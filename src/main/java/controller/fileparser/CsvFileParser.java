package controller.fileparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


/**
 * Implementation of the FileParser interface for CSV files. Parses CSV files into a collection of
 * map entries where each map represents a row with column headers as keys and row values as
 * values.
 */
public class CsvFileParser implements FileParser {

  /**
   * Parses a CSV file at the given path and returns an iterator of records. The first line of the
   * CSV file is expected to contain headers.
   *
   * @param filePath the path to the CSV file to be parsed
   *
   * @return an iterator of maps, where each map represents a CSV row with headers as keys
   *
   * @throws IOException if there's an error reading or parsing the file
   */
  @Override
  public Iterator<Map<String, String>> parseFile(String filePath) throws IOException {
    return new CsvFileIterator(filePath);
  }

  /**
   * Iterator implementation that processes CSV files one line at a time. This provides
   * memory-efficient parsing for large CSV files.
   */
  private static class CsvFileIterator implements Iterator<Map<String, String>> {
    private final BufferedReader reader;
    private final List<String> headers;
    private String nextLine;

    /**
     * Creates a new iterator for the specified CSV file. Reads the first line as headers and
     * prepares to iterate over data rows.
     *
     * @param filePath path to the CSV file
     *
     * @throws IOException if there's an error opening or reading from the file
     */
    public CsvFileIterator(String filePath) throws IOException {
      this.reader = new BufferedReader(new FileReader(filePath));
      this.headers = Arrays.asList(reader.readLine().split(","));
      this.nextLine = reader.readLine();
    }

    /**
     * Checks if there are more rows available in the CSV file.
     *
     * @return true if there are more rows to process, false otherwise
     */
    @Override
    public boolean hasNext() {
      return nextLine != null;
    }

    /**
     * Processes the next row in the CSV file and returns it as a map. Maps column headers to the
     * corresponding values in the current row. Skips empty lines in the CSV file.
     *
     * @return a map containing the values from the current row with headers as keys
     *
     * @throws NoSuchElementException if there are no more rows to process
     * @throws UncheckedIOException   if an I/O error occurs while reading the file
     */
    @Override
    public Map<String, String> next() {
      if (nextLine == null) {
        throw new NoSuchElementException();
      }

      // Skip blank lines
      while (nextLine != null && nextLine.trim().isEmpty()) {
        try {
          nextLine = reader.readLine();
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }

      // Check again after skipping blank lines
      if (nextLine == null) {
        throw new NoSuchElementException();
      }

      String[] values = nextLine.split(",");
      Map<String, String> map = new HashMap<>();
      for (int i = 0; i < Math.min(headers.size(), values.length); i++) {
        String value = values[i].replaceAll("^\"|\"$", "");
        map.put(headers.get(i), value);
      }

      try {
        // Skip any trailing blank lines for the next iteration
        do {
          nextLine = reader.readLine();
        }
        while (nextLine != null && nextLine.trim().isEmpty());
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }

      return map;
    }
  }
}