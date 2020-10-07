package com.codeborne.selenide.logevents;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

final class EventsCollectorTest implements WithAssertions {
  @Test
  void testOnEvent() {
    EventsCollector eventsCollector = new EventsCollector();
    SelenideLog selenideLog = new SelenideLog("Link", "Not Found");
    eventsCollector.afterEvent(selenideLog);

    assertThat(eventsCollector.events())
      .contains(selenideLog);
  }
}
