package com.codeborne.selenide.ex;

import com.codeborne.selenide.SelenideConfig;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ErrorMessagesTest implements WithAssertions {
  private final ChromeDriver webDriver = mock(ChromeDriver.class);
  private final SelenideConfig config = new SelenideConfig().reportsFolder("build/reports/tests");

  @BeforeEach
  void setUp() {
    config.screenshots(true);
    config.savePageSource(false);
    when(webDriver.getPageSource()).thenReturn("<html></html>");
  }

  @Test
  void formatsTimeoutToReadable() {
    Locale.setDefault(Locale.UK);
    assertThat(ErrorMessages.timeout(0))
      .isEqualToIgnoringNewLines("Timeout: 0 ms.");
    assertThat(ErrorMessages.timeout(1))
      .isEqualToIgnoringNewLines("Timeout: 1 ms.");
    assertThat(ErrorMessages.timeout(999))
      .isEqualToIgnoringNewLines("Timeout: 999 ms.");
    assertThat(ErrorMessages.timeout(1000))
      .isEqualToIgnoringNewLines("Timeout: 1 s.");
    assertThat(ErrorMessages.timeout(1001))
      .isEqualToIgnoringNewLines("Timeout: 1.001 s.");
    assertThat(ErrorMessages.timeout(1500))
      .isEqualToIgnoringNewLines("Timeout: 1.500 s.");
    assertThat(ErrorMessages.timeout(4000))
      .isEqualToIgnoringNewLines("Timeout: 4 s.");
  }
}
