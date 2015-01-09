package com.codeborne.selenide.impl;

import com.codeborne.selenide.logevents.LogEventListener;

import java.util.*;

/**
 * EXPERIMENTAL
 * to be refactored soon
 * <p/>
 * Logs Selenide test steps and notifies all registered LogEventListener about it
 *
 * @since Selenide 2.16
 */
public class SelenideLogger {
  public enum EventStatus {
    IN_PROGRESS, PASSED, FAILED
  }

  protected static ThreadLocal<List<LogEventListener>> listeners = new ThreadLocal<List<LogEventListener>>();

  protected static ThreadLocal<Deque<SelenideLog>> eventLog = new ThreadLocal<Deque<SelenideLog>>() {
    @Override
    protected Deque<SelenideLog> initialValue() {
    return new ArrayDeque<SelenideLog>();
    }
  };

  public static void addListener(LogEventListener listener) {
    List<LogEventListener> list = listeners.get();
    if (list == null) {
      list = new ArrayList<LogEventListener>();
    }

    list.add(listener);
    listeners.set(list);
  }

  public static void beginStep(String source, String methodName, Object... args) {
    beginStep(source, readableMethodName(methodName) + "(" + readableArguments(args) + ")");
  }

  static String readableMethodName(String methodName) {
    return methodName.replaceAll("([A-Z])", " $1").toLowerCase();
  }

  static String readableArguments(Object... args) {
    return args == null ? "" :
        (args[0] instanceof Object[]) ? arrayToString((Object[]) args[0]) :
            arrayToString(args);
  }

  private static String arrayToString(Object[] args) {
    return args.length == 1 ? args[0].toString() : Arrays.toString(args);
  }

  public static void beginStep(String source, String subject) {
    Deque<SelenideLog> eventLogs = eventLog.get();
    eventLogs.add(new SelenideLog(source, subject));
  }

  public static void commitStep(EventStatus status) {
    Deque<SelenideLog> eventLogs = eventLog.get();
    if (eventLogs.isEmpty()) {
      throw new IllegalStateException("Cannot commit step that is not started: " + status);
    }

    SelenideLog currentLog = eventLogs.peek();
    currentLog.setStatus(status);

    List<LogEventListener> listeners = getEventLoggerListeners();
    for (LogEventListener listener : listeners) {
      listener.onEvent(currentLog);
    }

    eventLogs.remove();
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
