package com.codeborne.selenide.logevents;

import com.codeborne.selenide.UnitTest;
import org.junit.jupiter.api.Test;

class EventsCollectorTest extends UnitTest {
  @Test
  void testOnEvent() {
    EventsCollector eventsCollector = new EventsCollector();
    SelenideLog selenideLog = new SelenideLog("Link", "Not Found");
    eventsCollector.onEvent(selenideLog);

    assertThat(eventsCollector.events())
      .contains(selenideLog);
  }
}
