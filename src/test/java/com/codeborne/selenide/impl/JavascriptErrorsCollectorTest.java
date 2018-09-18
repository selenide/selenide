package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JavascriptErrorsCollectorTest implements WithAssertions {
  private JavascriptErrorsCollector collector = new JavascriptErrorsCollector();
  private Config config = mock(Config.class);

  @Test
  void getJavascriptErrors_returnsEmptyListIfWebdriverIsNotStarted() {
    Driver driver = mock(Driver.class);
    when(driver.hasWebDriverStarted()).thenReturn(false);
    when(driver.config()).thenReturn(config);

    assertThat(collector.getJavascriptErrors(driver)).hasSize(0);

    verify(driver, never()).executeJavaScript(anyString(), any());
  }
}
