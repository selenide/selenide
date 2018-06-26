package com.codeborne.selenide.logevents;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EventsCollectorTest {

  @Test
  void testOnEvent() {
    EventsCollector eventsCollector = new EventsCollector();
    SelenideLog selenideLog = new SelenideLog("Link", "Not Found");
    eventsCollector.onEvent(selenideLog);

    List<LogEvent> events = eventsCollector.events();
    Assertions.assertEquals(selenideLog, events.get(0));
  }
}
