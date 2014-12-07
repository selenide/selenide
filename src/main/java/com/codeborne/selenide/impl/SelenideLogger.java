package com.codeborne.selenide.impl;

import com.codeborne.selenide.logevents.LogEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Logs Selenide test steps and notifies all registered LogEventListener about it
 */
public class SelenideLogger {
  public enum EventStatus {
    IN_PROGRESS, PASSED, FAILED
  }

  protected static ThreadLocal<List<LogEventListener>> listeners =
      new ThreadLocal<List<LogEventListener>>();

  protected static ThreadLocal<SelenideLog> eventLog = new ThreadLocal<SelenideLog>();

  public static void addListener(LogEventListener listener) {
    List<LogEventListener> list = listeners.get();
    if (list == null) {
      list = new ArrayList<LogEventListener>();
    }

    list.add(listener);
    listeners.set(list);
  }

  public static void beginStep(Object el, String subject) {
    if (el instanceof AbstractSelenideElement) {
      eventLog.set(new SelenideLog(((AbstractSelenideElement) el).getSearchCriteria(), subject));
    }
    if (el instanceof Navigator) {
      eventLog.set(new SelenideLog("", subject));
    }
  }

  public static void commitStep(EventStatus status) {
    SelenideLog currentLog = eventLog.get();
    if (currentLog == null) {
      throw new IllegalStateException("Cannot commit step that is not started: " + status);
    }
    
    currentLog.setStatus(status);

    List<LogEventListener> listeners = getEventLoggerListeners();
    for (LogEventListener listener : listeners) {
      listener.onEvent(currentLog);
    }

    eventLog.set(null);
  }

  private static List<LogEventListener> getEventLoggerListeners() {
    if (listeners.get() == null) {
      listeners.set(new ArrayList<LogEventListener>());
    }
    return listeners.get();
  }

  public static void clearListeners() {
    listeners.remove();
  }
}
