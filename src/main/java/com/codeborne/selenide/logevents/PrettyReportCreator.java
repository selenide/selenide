package com.codeborne.selenide.logevents;

import com.codeborne.selenide.impl.SelenideLogger;
import com.google.common.base.Joiner;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * EXPERIMENTAL
 * 
 * Use with cautions! This API will likely be changed soon.
 *
 * @since Selenide 2.16
 */
public class PrettyReportCreator extends TestWatcher {
  private static final Logger log = Logger.getLogger(PrettyReportCreator.class.getName());

  private final List<LogEvent> logEvents = new ArrayList<LogEvent>();
  private final LogEventListener logEventListener = new LogEventListener() {
    @Override
    public void onEvent(LogEvent currentLog) {
      logEvents.add(currentLog);
    }
  };

  @Override
  protected void starting(Description description) {
    SelenideLogger.addListener(logEventListener);
  }

  @Override
  protected void finished(Description description) {
    StringBuilder sb = new StringBuilder();
    sb.append("Report for ").append(description.getDisplayName()).append('\n');

    String delimiter = '+' + Joiner.on('+').join(line(20), line(70), line(10), line(10)) + "+\n";

    sb.append(delimiter);
    sb.append(String.format("|%-20s|%-70s|%-10s|%-10s|%n", "Element", "Subject", "Status", "ms."));
    sb.append(delimiter);

    for (LogEvent e : logEvents) {
      sb.append(String.format("|%-20s|%-70s|%-10s|%-10s|%n", e.getElement(), e.getSubject(), e.getStatus(), e.getDuration()));
    }
    sb.append(delimiter);
    log.info(sb.toString());
  }

  private String line(int count) {
    return Joiner.on("").join(Collections.nCopies(count, "-"));
  }
}
