package controller.fileparser;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Interface for parsing files into structured data. Implementations should handle different file
 * formats and convert them into an iterator of map entries where each map represents a record.
 */
public interface FileParser {
  /**
   * Parses a file at the given path and returns an iterator of records.
   *
   * @param filePath the path to the file to be parsed
   *
   * @return an iterator of maps, where each map represents a record with string keys and values
   *
   * @throws IOException if there's an error reading or parsing the file
   */
  Iterator<Map<String, String>> parseFile(String filePath) throws IOException;
}