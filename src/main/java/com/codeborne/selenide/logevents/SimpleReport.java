package com.codeborne.selenide.logevents;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * A simple text report of Selenide actions performed during test run.
 * <p>
 * Class is thread-safe: the same instance of SimpleReport can be reused by different threads simultaneously.
 */
public class SimpleReport {
  private static final Logger log = Logger.getLogger(SimpleReport.class.getName());
  private static final String newLine = System.getProperty("line.separator");

  public void start() {
    SelenideLogger.addListener("simpleReport", new EventsCollector());
  }

  public void finish(String title) {
    EventsCollector logEventListener = SelenideLogger.removeListener("simpleReport");
    if (logEventListener != null) {
      TextTable textTable = new TextTable(title, logEventListener.events());
      textTable.addHeader("Element", LogEvent:: getElement, 30, 55);
      textTable.addHeader("Subject", LogEvent:: getSubject, 80, 170);
      textTable.addHeader("Status", event -> event.getStatus().toString(), 10, 25);
      textTable.addHeader("ms.", event -> String.valueOf(event.getDuration()), 10, 25);
      log.info(textTable.toString());
    }
  }

  private class Row {
    private String name;
    private int minLength;
    private int maxLength;
    private Function<LogEvent, String> source;

    Row(String name, Function<LogEvent, String> source, int minLength, int maxLength) {
      this.name = name;
      this.minLength = minLength;
      this.maxLength = maxLength;
      this.source = source;
    }
  }

  private class TextTable {
    private String title;
    private List<LogEvent> events;
    private List<Row> rows = new ArrayList<>();
    private StringBuilder sb;


    TextTable(String title, List<LogEvent> events) {
      this.title = title;
      this.events = events;
    }

    void addHeader(String name, Function<LogEvent, String> source, int minLength, int maxLength) {
      rows.add(new Row(name, source, minLength, maxLength));
    }

    private String getTable() {
      sb = new StringBuilder();
      sb.append("Report for ").append(title).append(newLine);
      StringJoiner headerDelimiterJoin = new StringJoiner("+", "+", "+" + newLine);
      StringJoiner headerColumnJoin = new StringJoiner("|", "|", "|" + newLine);
      StringJoiner tableColumnJoin = new StringJoiner("|", "|", "|" + newLine);
      for (Row row : rows) {
        int max = getColumnLength(row.source, events, row.minLength);
        headerDelimiterJoin.add(String.join("", Collections.nCopies(max, "-")));
        String pattern = "%-" + max + "s";
        tableColumnJoin.add(pattern);
        headerColumnJoin.add(String.format(pattern, row.name));
      }

      sb.append(headerDelimiterJoin.toString());
      sb.append(headerColumnJoin.toString());
      sb.append(headerDelimiterJoin.toString());

      for (LogEvent event : events) {
        int size = rows.size();
        Object[] args = new String[size];
        for (int i = 0; i < size; i++) {
          Row row = rows.get(i);
          String desc = row.source.apply(event);
          if (desc.length() >= row.maxLength) {
            desc = desc.substring(0, row.maxLength);
          }
          args[i] = desc;
        }
        String format = String.format(tableColumnJoin.toString(), args);
        sb.append(format);
      }

      sb.append(headerDelimiterJoin.toString());
      return sb.toString();
    }

    @Override
    public String toString() {
      return sb == null ? getTable() : sb.toString();
    }

    private int getColumnLength(Function<LogEvent, String> mapper, List<LogEvent> events, Integer minLength) {
      OptionalInt max = events
              .stream()
              .map(mapper)
              .map(String:: length)
              .mapToInt(Integer:: intValue)
              .max();
      return max.orElse(0) < minLength ? minLength : (max.getAsInt() + 1);
    }

  }

  public void clean() {
    SelenideLogger.removeListener("simpleReport");
  }

}
