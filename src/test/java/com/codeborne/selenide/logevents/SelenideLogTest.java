package com.codeborne.selenide.logevents;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.logevents.LogEvent.EventStatus.IN_PROGRESS;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static org.assertj.core.api.Assertions.assertThat;

final class SelenideLogTest {
  @Test
  void initialStatusIsInProgress() {
    assertThat(new SelenideLog("By.name: domain", "exists()").getStatus())
      .isEqualTo(IN_PROGRESS);
  }

  @Test
  void measuresDurationOfEveryStep() throws InterruptedException {
    SelenideLog step = new SelenideLog("By.name: domain", "exists()");

    Thread.sleep(15);
    step.setStatus(PASS);

    assertThat(step.getStatus()).isEqualTo(PASS);
    assertThat(step.getDuration()).isBetween(15L, 100L);
  }

  @Test
  void stringRepresentationUsedInReports() {
    SelenideLog log = new SelenideLog("By.name: domain", "exists()");
    assertThat(log).hasToString("$(\"By.name: domain\") exists()");
  }
}
