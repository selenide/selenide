package com.codeborne.selenide.logevents;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.logevents.LogEvent.EventStatus.IN_PROGRESS;

final class SelenideLogTest implements WithAssertions {
  @Test
  void testGetSubject() {
    SelenideLog log = new SelenideLog("Element", "Subject");
    assertThat(log.getSubject())
      .isEqualTo("Subject");
  }

  @Test
  void testGetStatus() {
    SelenideLog log = new SelenideLog("Element", "Subject");
    assertThat(log.getStatus())
      .isEqualTo(IN_PROGRESS);
  }

  @Test
  void testGetElement() {
    SelenideLog log = new SelenideLog("Element", "Subject");
    assertThat(log.getElement())
      .isEqualTo("Element");
  }

  @Test
  void testError() {
    SelenideLog log = new SelenideLog("Element", "Subject");
    Throwable error = new Throwable("Error message");
    log.setError(error);
    assertThat(log.getError())
      .isEqualTo(error);
  }

  @Test
  void testToString() {
    SelenideLog log = new SelenideLog("Element", "Subject");
    assertThat(log)
      .hasToString(String.format("$(\"%s\") %s", log.getElement(), log.getSubject()));
  }
}
