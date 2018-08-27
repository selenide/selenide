package com.codeborne.selenide.impl;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static java.util.Collections.emptyList;

public class WebDriverLogs {
  public List<String> getWebDriverLogs(WebDriver webDriver, String logType, Level logLevel) {
    return listToString(getLogEntries(webDriver, logType, logLevel));
  }

  private List<LogEntry> getLogEntries(WebDriver webDriver, String logType, Level logLevel) {
    try {
      return webDriver.manage().logs().get(logType).filter(logLevel);
    }
    catch (UnsupportedOperationException ignore) {
      return emptyList();
    }
  }

  private <T> List<String> listToString(List<T> objects) {
    if (objects == null || objects.isEmpty()) {
      return emptyList();
    }
    List<String> result = new ArrayList<>(objects.size());
    for (T object : objects) {
      result.add(object.toString());
    }
    return result;
  }
}
