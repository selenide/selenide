package com.codeborne.selenide.logevents;

import com.github.bsideup.jabel.Desugar;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;
import org.slf4j.helpers.NOPLoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

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
  private static final int MIN_SECOND_COLUMN_WIDTH = 7;
  private static final String TWO_SPACES = "  ";
  private static final String INDENT = System.getProperty("selenide.report.indent", TWO_SPACES);

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
    var eventsWithNestingLevel = computeNestingLevel(events);

    int firstColumnWidth = Math.max(maxLocatorLength(eventsWithNestingLevel), MIN_FIRST_COLUMN_WIDTH);
    int secondColumnWidth = Math.min(maxSubjectLength(eventsWithNestingLevel), MAX_SECOND_COLUMN_WIDTH);
    int estimatedReportLength = 20 + title.length() + (firstColumnWidth + secondColumnWidth + 35) * (4 + eventsWithNestingLevel.size());

    ReportBuilder report = new ReportBuilder(firstColumnWidth, secondColumnWidth, estimatedReportLength);
    report.appendTitle(title);
    report.appendHeader();

    for (var wrapper : eventsWithNestingLevel) {
      report.appendEvent(wrapper);
    }
    report.appendDelimiterLine();
    return report.build();
  }

  private List<LogEventWithNestingLevel> computeNestingLevel(List<LogEvent> events) {
    var stack = new ArrayDeque<LogEvent>();
    var result = new ArrayList<LogEventWithNestingLevel>(events.size());
    for (LogEvent event : events) {
      while (!stack.isEmpty() && stack.peekFirst().getEndTime() <= event.getStartTime())
        stack.removeFirst();

      result.add(new LogEventWithNestingLevel(stack.size(), event));

      stack.addFirst(event);
    }

    return result;
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
      return new String(new char[count]).replace("\0", "-");
    }

    @CheckReturnValue
    @Nonnull
    private String indent(int count) {
      return new String(new char[count]).replace("\0", INDENT);
    }

    public void appendHeader() {
      appendLine(sb, firstColumnWidth, secondColumnWidth, "Element", "Subject", "Status", "ms.");
      appendDelimiterLine();
    }

    private void appendLine(StringBuilder sb, int firstColumnWidth, int secondColumnWidth,
                            String element, String subject, String status, String duration) {
      sb.append("| ");
      append(sb, element, firstColumnWidth);
      sb.append(" | ");
      append(sb, subject, secondColumnWidth);
      sb.append(" | ");
      append(sb, status, 10);
      sb.append(" | ");
      append(sb, duration, 10);
      sb.append(" |").append(lineSeparator());
    }

    private void appendEvent(LogEventWithNestingLevel wrapper) {
      var elementWithIndent = indent(wrapper.nestingLevel) + wrapper.event.getElement();
      appendLine(sb, firstColumnWidth, secondColumnWidth, elementWithIndent, wrapper.event.getSubject(),
        wrapper.event.getStatus().name(), String.valueOf(wrapper.event.getDuration()));
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

  @CheckReturnValue
  private int maxLocatorLength(List<LogEventWithNestingLevel> events) {
    var maxLength = 0;
    for (var wrapper : events) {
      int length = wrapper.event.getElement().length() + wrapper.nestingLevel * INDENT.length();

      if (length > maxLength)
        maxLength = length;
    }

    return maxLength;
  }

  @CheckReturnValue
  private int maxSubjectLength(List<LogEventWithNestingLevel> events) {
    var maxLength = MIN_SECOND_COLUMN_WIDTH;

    for (var wrapper : events) {
      final int length = wrapper.event.getSubject().length();
      if (length > maxLength)
        maxLength = length;
    }

    return maxLength;
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

  @Desugar
  private record LogEventWithNestingLevel(int nestingLevel, LogEvent event) {
  }
}
