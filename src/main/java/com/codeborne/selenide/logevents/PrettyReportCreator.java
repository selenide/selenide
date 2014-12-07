package com.codeborne.selenide.logevents;

import com.codeborne.selenide.impl.SelenideLogger;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.ArrayList;
import java.util.List;

/**
 * EXPERIMENTAL
 * 
 * Use with cautions! This API will likely be changed soon.
 */
public class PrettyReportCreator extends TestWatcher {
  private final List<LogEvent> logEvents = new ArrayList<LogEvent>();
  private final LogEventListener logEventListener = new LogEventListener() {
    @Override
    public void onEvent(LogEvent currentLog) {
      logEvents.add(currentLog);
//      System.out.println( "{" + currentLog.getElement() + "} " +
//          currentLog.getSubject() + ": " + currentLog.getStatus()
//      );
    }
  };

  @Override
  protected void starting(Description description) {
    SelenideLogger.addListener(logEventListener);
  }

  @Override
  protected void finished(Description description) {
    System.out.println();
    System.out.println("Report for " + description.getDisplayName());

    String hLine = "+--------------------+----------------------------------------------------------------------+----------+";

    System.out.println(hLine);

    System.out.format("|%-20s|%-70s|%-10s|\n", "Element", "Subject", "Status");
    System.out.println(hLine);
    for (LogEvent e : logEvents) {
      System.out.format("|%-20s|%-70s|%-10s|\n", e.getElement(),  e.getSubject(), e.getStatus());
    }
    System.out.println(hLine);
    System.out.println();
  }
}