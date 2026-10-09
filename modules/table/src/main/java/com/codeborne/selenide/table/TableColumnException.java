package com.codeborne.selenide.table;

import java.util.List;

/**
 * Thrown when a column header is not displayed in a table, or is displayed more than once.
 *
 * @since 7.19.0
 */
public class TableColumnException extends RuntimeException {
  /**
   * @param header expected column header
   * @param reason why the column could not be resolved, e.g. "not found" or "ambiguous"
   * @param table description of the table root element
   * @param displayedHeaders headers actually displayed in the table
   * @since 7.19.0
   */
  public TableColumnException(String header, String reason, String table, List<String> displayedHeaders) {
    super("Column \"" + header + "\" " + reason + " in " + table + "; displayed headers: " + displayedHeaders);
  }
}
