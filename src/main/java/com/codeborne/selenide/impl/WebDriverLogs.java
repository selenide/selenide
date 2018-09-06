package com.codeborne.selenide.impl;

import com.codeborne.selenide.Context;
import org.openqa.selenium.logging.LogEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static java.util.Collections.emptyList;

public class WebDriverLogs {
  private final Context context;

  public WebDriverLogs(Context context) {
    this.context = context;
  }

  public List<String> logs(String logType) {
    return logs(logType, Level.ALL);
  }

  public List<String> logs(String logType, Level logLevel) {
    return listToString(getLogEntries(logType, logLevel));
  }

  private List<LogEntry> getLogEntries(String logType, Level logLevel) {
    try {
      return context.getWebDriver().manage().logs().get(logType).filter(logLevel);
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
