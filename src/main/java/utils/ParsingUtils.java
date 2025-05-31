package utils;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for parsing operations.
 */
public class ParsingUtils {

  /**
   * Get the value of the key word from the input arguments. Handles the case where the value is
   * enclosed in quotes. Quoted values must start with a word starting quote and end with a word
   * ending with a quote. Example: ... key "value with spaces" ...
   *
   * @param inputArguments input arguments
   * @param index          index of the key word in the input arguments
   * @param keyWords       set of keywords
   *
   * @return value of the key word
   */
  public static String getKeyWordValue(String[] inputArguments, int index, Set<String> keyWords) {
    final StringBuilder value = new StringBuilder();
    boolean inQuotes = false;
    for (int i = index + 1; i < inputArguments.length; i++) {
      String arg = inputArguments[i];
      if (arg.startsWith("\"")) {
        inQuotes = true;
        value.append(arg.substring(1)).append(" ");
      } else if (arg.endsWith("\"")) {
        value.append(arg, 0, arg.length() - 1);
        break;
      } else if (inQuotes) {
        value.append(arg).append(" ");
      } else if (keyWords.contains(arg) || arg.startsWith("--")) {
        break;
      } else {
        value.append(arg).append(" ");
      }
    }
    return value.toString().trim();
  }

  /**
   * Update the argument map with the values of the keywords from the input arguments. If the
   * keyword is not present in the input arguments, the value is set to an empty string.
   *
   * @param inputArguments input arguments
   * @param keyWords       set of keywords
   * @param argMap         argument map
   */
  public static void updateArgMap(
          String[] inputArguments,
          Set<String> keyWords,
          Map<String, String> argMap) {
    for (String keyWord : keyWords) {
      int index = Arrays.asList(inputArguments).indexOf(keyWord);
      if (index != -1) {
        argMap.put(keyWord, getKeyWordValue(inputArguments, index, keyWords));
      } else {
        argMap.put(keyWord, "");
      }
    }
  }

  /**
   * Check if the given file name is a valid CSV file name. The file name should be of the format
   * [a-zA-Z0-9]+.csv. No special characters are allowed in the file name.
   *
   * @param fileName file name
   *
   * @return true if the given file name is a valid CSV file name, false otherwise
   */
  public static boolean isValidCSVFileName(String fileName) {
    final String csvRegex = "[a-zA-Z0-9_\\-. ]+\\.csv$";

    return fileName.matches(csvRegex);
  }

  /**
   * Escape the given value for CSV format. If the value is null, an empty string is returned.
   * Encloses the value in double quotes and escapes any inner double quotes.
   *
   * @param value value to escape
   *
   * @return escaped value
   */
  public static String escapeCsv(String value) {
    if (value == null) {
      return "";
    }
    // Enclose in double quotes and escape any inner double quotes
    return "\"" + value.replace("\"", "\"\"") + "\"";
  }
}
