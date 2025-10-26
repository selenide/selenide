package com.codeborne.selenide;

import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static com.codeborne.selenide.impl.BiDiUti.isBiDiEnabled;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class WebDriverLogs {
  private final Driver driver;

  WebDriverLogs(Driver driver) {
    this.driver = driver;
  }

  public List<String> logs(String logType) {
    return logs(logType, Level.ALL);
  }

  public List<String> logs(String logType, Level logLevel) {
    return listToString(getLogEntries(logType, logLevel));
  }

  private List<LogEntry> getLogEntries(String logType, Level logLevel) {
    if (LogType.BROWSER.equals(logType) && isBiDiEnabled(driver.getWebDriver())) {
      List<LogEntry> collectedLogs = new ArrayList<>(driver.getBrowserLogs());
      // Mimic the behaviour of `org.openqa.selenium.logging.Logs#get`: clear the collected logs on every call.
      // Maybe it's strange, but at least we have the same behaviour for browsers with and without BiDi.
      driver.getBrowserLogs().clear();
      return filter(collectedLogs, logLevel);
    }

    try {
      return filter(driver.getWebDriver().manage().logs().get(logType), logLevel);
    }
    catch (UnsupportedOperationException ignore) {
      return emptyList();
    }
  }

  private List<LogEntry> filter(LogEntries entries, Level level) {
    return filter(entries.getAll(), level);
  }

  private List<LogEntry> filter(List<LogEntry> logs, Level level) {
    return logs.stream()
      .filter(entry -> entry.getLevel().intValue() >= level.intValue())
      .toList();
  }

  private <T> List<String> listToString(List<T> objects) {
    return objects.stream().map(Object::toString).collect(toList());
  }
}
