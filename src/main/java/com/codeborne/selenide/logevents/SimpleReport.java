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

    StringBuilder sb = new StringBuilder();
    sb.append("Report for ").append(title).append('\n');

    String delimiter = '+' + String.join("+", line(firstColumnWidth + 2), line(secondColumnWidth + 2), line(10 + 2), line(10 + 2)) + "+\n";

    sb.append(delimiter);
    sb.append(String.format("| %-" + firstColumnWidth + "s | %-" + secondColumnWidth + "s | %-10s | %-10s |%n", "Element", "Subject", "Status", "ms."));
    sb.append(delimiter);

    for (LogEvent e : events) {
      sb.append(String.format("| %-" + firstColumnWidth + "s | %-" + secondColumnWidth + "s | %-10s | %-10s |%n", e.getElement(), e.getSubject(),
              e.getStatus(), e.getDuration()));
    }
    sb.append(delimiter);
    return sb.toString();
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

  @CheckReturnValue
  @Nonnull
  private String line(int count) {
    return String.join("", Collections.nCopies(count, "-"));
  }

  private static void checkThatSlf4jIsConfigured() {
    ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
    if (loggerFactory instanceof NOPLoggerFactory || loggerFactory.getLogger("com.codeborne.selenide") instanceof NOPLogger) {
      throw new IllegalStateException("SLF4J is not configured. You will not see any Selenide logs. \n" +
        "  Please add slf4j-simple.jar, slf4j-log4j12.jar or logback-classic.jar to your classpath. \n" +
        "  See https://github.com/selenide/selenide/wiki/slf4j");
    }
  }
}
