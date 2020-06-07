package com.codeborne.selenide.logevents;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
public class EventsCollector implements LogEventListener {
  private final List<LogEvent> logEvents = new ArrayList<>();

  @Override
  public void afterEvent(LogEvent currentLog) {
    logEvents.add(currentLog);
  }

  @Override
  public void beforeEvent(LogEvent currentLog) {
    //ignore
  }

  @CheckReturnValue
  @Nonnull
  public List<LogEvent> events() {
    return Collections.unmodifiableList(logEvents);
  }
}
