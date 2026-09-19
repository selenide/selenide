package com.codeborne.selenide;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chromium.HasCdp;

import java.util.Map;

import static com.codeborne.selenide.Selenide.clock;
import static com.codeborne.selenide.Selenide.using;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Isolated("uses the thread-local static webdriver")
final class SelenideClockTest {
  @AfterEach
  void tearDown() {
    closeWebDriver();
  }

  @Test
  void clock_delegatesToThreadLocalDriver() {
    ChromiumDriver webDriver = mock();
    using(webDriver, () -> {
      clock().setTimezone("America/New_York");
      verify(webDriver).executeCdpCommand(eq("Emulation.setTimezoneOverride"),
        eq(Map.of("timezoneId", "America/New_York")));
    });
  }

  private interface ChromiumDriver extends WebDriver, HasCdp {
  }
}
