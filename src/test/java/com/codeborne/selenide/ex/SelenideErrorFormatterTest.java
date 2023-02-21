package com.codeborne.selenide.ex;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.UseLocaleExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class SelenideErrorFormatterTest {
  @RegisterExtension
  private static final UseLocaleExtension useLocale = new UseLocaleExtension("en");

  private final SelenideErrorFormatter errorFormatter = new SelenideErrorFormatter();
  private final ChromeDriver webDriver = mock();
  private final SelenideConfig config = new SelenideConfig().reportsFolder("build/reports/tests");

  @BeforeEach
  void setUp() {
    config.screenshots(true);
    config.savePageSource(false);
    when(webDriver.getPageSource()).thenReturn("<html></html>");
  }

  @Test
  void formatsTimeoutToReadable() {
    assertThat(errorFormatter.timeout(0))
      .isEqualToIgnoringNewLines("Timeout: 0 ms.");
    assertThat(errorFormatter.timeout(1))
      .isEqualToIgnoringNewLines("Timeout: 1 ms.");
    assertThat(errorFormatter.timeout(999))
      .isEqualToIgnoringNewLines("Timeout: 999 ms.");
    assertThat(errorFormatter.timeout(1000))
      .isEqualToIgnoringNewLines("Timeout: 1 s.");
    assertThat(errorFormatter.timeout(1001))
      .isEqualToIgnoringNewLines("Timeout: 1.001 s.");
    assertThat(errorFormatter.timeout(1500))
      .isEqualToIgnoringNewLines("Timeout: 1.500 s.");
    assertThat(errorFormatter.timeout(4000))
      .isEqualToIgnoringNewLines("Timeout: 4 s.");
  }
}
