package com.codeborne.selenide.logevents;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;
import org.slf4j.helpers.NOPLoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.OptionalInt;

import static java.lang.System.lineSeparator;

/**
 * A simple text report of Selenide actions performed during test run.
 *
 * Class is thread-safe: the same instance of SimpleReport can be reused by different threads simultaneously.
 */
@ParametersAreNonnullByDefault
public class SimpleReport {
  private static final Logger log = LoggerFactory.getLogger(SimpleReport.class);
  private static final int MIN_FIRST_COLUMN_WIDTH = 20;
  private static final int MAX_SECOND_COLUMN_WIDTH = 70;

  public void start() {
    checkThatSlf4jIsConfigured();
    SelenideLogger.addListener("simpleReport", new EventsCollector());
  }

  public void finish(String title) {
    EventsCollector logEventListener = SelenideLogger.removeListener("simpleReport");

    if (logEventListener == null) {
      log.warn("Can not publish report because Selenide logger has not started.");
      return;
    }

    String report = generateReport(title, logEventListener.events());
    log.info(report);
  }

  @Nonnull
  @CheckReturnValue
  String generateReport(String title, List<LogEvent> events) {
    int firstColumnWidth = Math.max(maxLocatorLength(events).orElse(0), MIN_FIRST_COLUMN_WIDTH);
    int secondColumnWidth = Math.min(maxSubjectLength(events).orElse(7), MAX_SECOND_COLUMN_WIDTH);
    int estimatedReportLength = 20 + title.length() + (firstColumnWidth + secondColumnWidth + 35) * (4 + events.size());

    ReportBuilder report = new ReportBuilder(firstColumnWidth, secondColumnWidth, estimatedReportLength);
    report.appendTitle(title);
    report.appendHeader();

    for (LogEvent e : events) {
      report.appendEvent(e);
    }
    report.appendDelimiterLine();
    return report.build();
  }

  private static class ReportBuilder {
    private final int firstColumnWidth;
    private final int secondColumnWidth;
    private final String delimiterLine;
    private final StringBuilder sb;

    private ReportBuilder(int firstColumnWidth, int secondColumnWidth, int estimatedReportLength) {
      this.firstColumnWidth = firstColumnWidth;
      this.secondColumnWidth = secondColumnWidth;
      delimiterLine = '+' + String.join("+",
        line(firstColumnWidth + 2),
        line(secondColumnWidth + 2),
        line(10 + 2),
        line(10 + 2)
      ) + '+' + lineSeparator();
      sb = new StringBuilder(estimatedReportLength);
    }

    private void appendTitle(String title) {
      sb.append("Report for ").append(title).append(lineSeparator());
      sb.append(delimiterLine);
    }

    @CheckReturnValue
    @Nonnull
    private String line(int count) {
      StringBuilder sb = new StringBuilder(count);
      for (int i = 0; i < count; i++) {
        sb.append('-');
      }
      return sb.toString();
    }

    public void appendHeader() {
      appendLine(sb, firstColumnWidth, secondColumnWidth, "Element", "Subject", "Status", "ms.");
      appendDelimiterLine();
    }

    private void appendLine(StringBuilder sb, int firstColumnWidth, int secondColumnWidth,
                            String first, String second, String third, String fourth) {
      sb.append("| ");
      append(sb, first, firstColumnWidth);
      sb.append(" | ");
      append(sb, second, secondColumnWidth);
      sb.append(" | ");
      append(sb, third, 10);
      sb.append(" | ");
      append(sb, fourth, 10);
      sb.append(" |").append(lineSeparator());
    }

    private void appendEvent(LogEvent e) {
      appendLine(sb, firstColumnWidth, secondColumnWidth, e.getElement(), e.getSubject(),
        e.getStatus().name(), String.valueOf(e.getDuration()));
    }

    public void appendDelimiterLine() {
      sb.append(delimiterLine);
    }

    private void append(StringBuilder sb, String text, int minLength) {
      sb.append(text);
      for (int i = text.length(); i < minLength; i++) {
        sb.append(' ');
      }
    }

    public String build() {
      return sb.toString();
    }
  }

  @Nonnull
  @CheckReturnValue
  private OptionalInt maxLocatorLength(List<LogEvent> events) {
    return events
            .stream()
            .map(LogEvent::getElement)
            .map(String::length)
            .mapToInt(Integer::intValue)
            .max();
  }

  @Nonnull
  @CheckReturnValue
  private OptionalInt maxSubjectLength(List<LogEvent> events) {
    return events
            .stream()
            .map(LogEvent::getSubject)
            .map(String::length)
            .mapToInt(Integer::intValue)
            .max();
  }

  public void clean() {
    SelenideLogger.removeListener("simpleReport");
  }

  private static void checkThatSlf4jIsConfigured() {
    ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
    if (loggerFactory instanceof NOPLoggerFactory || loggerFactory.getLogger("com.codeborne.selenide") instanceof NOPLogger) {
      throw new IllegalStateException("SLF4J is not configured. You will not see any Selenide logs. " + lineSeparator() +
        "  Please add slf4j-simple.jar, slf4j-log4j12.jar or logback-classic.jar to your classpath. " + lineSeparator() +
        "  See https://github.com/selenide/selenide/wiki/slf4j");
    }
  }
}
