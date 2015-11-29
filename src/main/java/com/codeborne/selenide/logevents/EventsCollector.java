package com.codeborne.selenide.logevents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventsCollector implements LogEventListener {
  private final List<LogEvent> logEvents = new ArrayList<>();

  @Override
  public void onEvent(LogEvent currentLog) {
    logEvents.add(currentLog);
  }
  
  public List<LogEvent> events() {
    return Collections.unmodifiableList(logEvents);
  }
}
