package com.codeborne.selenide;

import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;

import java.util.List;
import java.util.logging.Level;

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
    try {
      return filter(driver.getWebDriver().manage().logs().get(logType), logLevel);
    }
    catch (UnsupportedOperationException ignore) {
      return emptyList();
    }
  }

  private List<LogEntry> filter(LogEntries entries, Level level) {
    return entries.getAll().stream()
      .filter(entry -> entry.getLevel().intValue() >= level.intValue())
      .toList();
  }

  private <T> List<String> listToString(List<T> objects) {
    return objects.stream().map(Object::toString).collect(toList());
  }
}
