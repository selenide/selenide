package com.codeborne.selenide.table;

import java.util.List;
import java.util.regex.Pattern;

final class ColumnResolver {
  private static final Pattern WHITESPACE = Pattern.compile("[\\s\\u00A0]+");

  private ColumnResolver() {
  }

  static String normalize(String text) {
    return WHITESPACE.matcher(text).replaceAll(" ").trim();
  }

  static List<String> normalize(List<String> texts) {
    return texts.stream().map(ColumnResolver::normalize).toList();
  }

  static int indexOf(List<String> displayedHeaders, String header, String table) {
    List<String> normalized = normalize(displayedHeaders);
    String expected = normalize(header);
    int first = normalized.indexOf(expected);
    if (first < 0) {
      throw new TableColumnException(header, "not found", table, normalized);
    }
    if (normalized.lastIndexOf(expected) != first) {
      throw new TableColumnException(header, "ambiguous", table, normalized);
    }
    return first;
  }
}
