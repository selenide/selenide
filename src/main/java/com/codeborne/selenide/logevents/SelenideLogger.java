package com.codeborne.selenide.logevents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.logevents.LogEvent.EventStatus.FAIL;

/**
 * Logs Selenide test steps and notifies all registered LogEventListener about it
 */
public class SelenideLogger {
  protected static ThreadLocal<List<LogEventListener>> listeners = new ThreadLocal<>();

  public static void addListener(LogEventListener listener) {
    List<LogEventListener> list = listeners.get();
    if (list == null) {
      list = new ArrayList<>();
    }

    list.add(listener);
    listeners.set(list);
  }

  public static SelenideLog beginStep(String source, String methodName, Object... args) {
    return beginStep(source, readableMethodName(methodName) + "(" + readableArguments(args) + ")");
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
    return args.length == 1 ? String.valueOf(args[0]) : Arrays.toString(args);
  }

  public static SelenideLog beginStep(String source, String subject) {
    return new SelenideLog(source, subject);
  }

  public static void commitStep(SelenideLog log, Throwable error) {
    log.setError(error);
    commitStep(log, FAIL);
  }
  
  public static void commitStep(SelenideLog log, LogEvent.EventStatus status) {
    log.setStatus(status);

    List<LogEventListener> listeners = getEventLoggerListeners();
    for (LogEventListener listener : listeners) {
      listener.onEvent(log);
    }
  }

  private static List<LogEventListener> getEventLoggerListeners() {
    if (listeners.get() == null) {
      listeners.set(new ArrayList<LogEventListener>());
    }
    return listeners.get();
  }
  
  public static void removeListener(LogEventListener listener) {
    try {
      listeners.get().remove(listener);
      if (listeners.get().isEmpty()) {
        listeners.remove();
      }
    } catch(NullPointerException npe) {
      // Prevent NPE throwing
    }
  }
}
