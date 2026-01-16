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
      .isEqualToIgnoringNewLines("Timeout: 0ms");
    assertThat(errorFormatter.timeout(1))
      .isEqualToIgnoringNewLines("Timeout: 1ms");
    assertThat(errorFormatter.timeout(999))
      .isEqualToIgnoringNewLines("Timeout: 999ms");
    assertThat(errorFormatter.timeout(1000))
      .isEqualToIgnoringNewLines("Timeout: 1s");
    assertThat(errorFormatter.timeout(1001))
      .isEqualToIgnoringNewLines("Timeout: 1.001s");
    assertThat(errorFormatter.timeout(1500))
      .isEqualToIgnoringNewLines("Timeout: 1.5s");
    assertThat(errorFormatter.timeout(4000))
      .isEqualToIgnoringNewLines("Timeout: 4s");
  }
}
