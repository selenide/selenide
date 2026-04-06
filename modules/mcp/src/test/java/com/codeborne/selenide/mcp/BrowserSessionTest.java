package com.codeborne.selenide.mcp;

import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BrowserSessionTest {
  @Test
  void driverIsNotStartedUntilFirstAccess() {
    BrowserSession session = new BrowserSession(new SelenideConfig());
    assertThat(session.isStarted()).isFalse();
  }

  @Test
  void closingUnstartedSessionDoesNothing() {
    BrowserSession session = new BrowserSession(new SelenideConfig());
    session.close();
    assertThat(session.isStarted()).isFalse();
  }
}
