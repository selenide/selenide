package com.codeborne.selenide.impl;

import com.codeborne.selenide.logevents.LogEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Logs Selenide test steps and notifies all registered LogEventListener about it
 * 
 */
public class SelenideLogger {
  public enum EventStatus {
    PASSED, FAILED
  }
  
  protected static ThreadLocal<List<LogEventListener>> listeners = 
      new ThreadLocal<List<LogEventListener>>();
  
  protected static ThreadLocal<SelenideLog> eventLog = new ThreadLocal<SelenideLog>();
  
  public static void addListener(LogEventListener l) {
    List<LogEventListener> list = listeners.get();
    if(list == null) {
      list = new ArrayList<LogEventListener>();
    }
    
    list.add(l);
    listeners.set(list);
  }
  
  public static void beginStep(Object el, String subject) {
    SelenideLog currentLog = new SelenideLog();
    String elStr = "";
    if(el instanceof AbstractSelenideElement) {
      elStr = ((AbstractSelenideElement) el).getSearchCriteria();
    }
    if(el instanceof Navigator) {
      elStr = "Browser";
    }
    currentLog.setElement(elStr);
    currentLog.setSubject(subject);
    
    eventLog.set(currentLog);
  }
  
  
  public static void commitStep(EventStatus status) {
    SelenideLog currentLog = eventLog.get();
    if(currentLog != null) {
      currentLog.setStatus(status);
      
      List<LogEventListener> listeners = getEventLoggerListeners();
      for (LogEventListener l : listeners) {
        l.onEvent(currentLog);
      }
      
      eventLog.set(null);
    }
  }


  private static List<LogEventListener> getEventLoggerListeners() {
    if(listeners.get() == null) {
      List<LogEventListener> list = new ArrayList<LogEventListener>();
      listeners.set(list);
    }
    return listeners.get();
  }

  public static void clearListeners() {
    listeners.remove();
  }

}
