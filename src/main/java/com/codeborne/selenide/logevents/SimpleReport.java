package com.codeborne.selenide.logevents;

import com.google.common.base.Joiner;

import java.util.Collections;
import java.util.OptionalInt;
import java.util.logging.Logger;

/**
 * A simple text report of Selenide actions performed during test run.
 * 
 * Class is thread-safe: the same instance of SimpleReport can be reused by different threads simultaneously.
 */
public class SimpleReport {
  private static final Logger log = Logger.getLogger(SimpleReport.class.getName());

  public void start() {
    SelenideLogger.addListener("simpleReport", new EventsCollector());
  }

  public void finish(String title) {
    EventsCollector logEventListener = SelenideLogger.removeListener("simpleReport");

    if (logEventListener == null) {
      log.warning("Can not publish report because Selenide logger has not started.");
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

    String delimiter = '+' + Joiner.on('+').join(line(count), line(70), line(10), line(10)) + "+\n";

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
    return Joiner.on("").join(Collections.nCopies(count, "-"));
  }
}
