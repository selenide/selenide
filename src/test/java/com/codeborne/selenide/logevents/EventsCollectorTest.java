package com.codeborne.selenide.logevents;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class EventsCollectorTest {

  @Test
  public void testOnEvent() {
    EventsCollector eventsCollector = new EventsCollector();
    SelenideLog selenideLog = new SelenideLog("Link", "Not Found");
    eventsCollector.onEvent(selenideLog);

    List<LogEvent> events = eventsCollector.events();
    assertEquals(selenideLog, events.get(0));
  }
}
