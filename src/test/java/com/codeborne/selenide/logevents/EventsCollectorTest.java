package com.codeborne.selenide.logevents;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class EventsCollectorTest {
  @Test
  void collectsEvents() {
    EventsCollector eventsCollector = new EventsCollector();
    SelenideLog log1 = new SelenideLog("Link", "Not Found");
    SelenideLog log2 = new SelenideLog("Link", "Not Found");

    eventsCollector.afterEvent(log1);
    eventsCollector.afterEvent(log2);

    assertThat(eventsCollector.events()).containsExactly(log1, log2);
  }
}
