package com.codeborne.selenide.logevents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.OptionalInt;

/**
 * A simple text report of Selenide actions performed during test run.
 *
 * Class is thread-safe: the same instance of SimpleReport can be reused by different threads simultaneously.
 */
public class SimpleReport {
  private static final Logger log = LoggerFactory.getLogger(SimpleReport.class);

  public void start() {
    SelenideLogger.addListener("simpleReport", new EventsCollector());
  }

  public void finish(String title) {
    EventsCollector logEventListener = SelenideLogger.removeListener("simpleReport");

    if (logEventListener == null) {
      log.warn("Can not publish report because Selenide logger has not started.");
      return;
    }

    OptionalInt maxLineLength = logEventListener.events()
            .stream()
            .map(LogEvent::getElement)
            .map(String::length)
            .mapToInt(Integer::intValue)
            .max();

    int count = maxLineLength.orElse(0) >= 20 ? (maxLineLength.getAsInt() + 1) : 20;

    StringBuilder sb = new StringBuilder();
    sb.append("Report for ").append(title).append('\n');

    String delimiter = '+' + String.join("+", line(count), line(70), line(10), line(10)) + "+\n";

    sb.append(delimiter);
    sb.append(String.format("|%-" + count + "s|%-70s|%-10s|%-10s|%n", "Element", "Subject", "Status", "ms."));
    sb.append(delimiter);

    for (LogEvent e : logEventListener.events()) {
      sb.append(String.format("|%-" + count + "s|%-70s|%-10s|%-10s|%n", e.getElement(), e.getSubject(),
              e.getStatus(), e.getDuration()));
    }
    sb.append(delimiter);
    log.info(sb.toString());
  }

  public void clean() {
    SelenideLogger.removeListener("simpleReport");
  }

  private String line(int count) {
    return String.join("", Collections.nCopies(count, "-"));
  }
}
