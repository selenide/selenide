package com.codeborne.selenide.ex.custom;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ex.ErrorFormatter;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriverException;

import static com.codeborne.selenide.impl.Plugins.inject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorFormatterWithPageUrlTest {
  private final ErrorFormatterWithPageUrl formatter = new ErrorFormatterWithPageUrl();
  private final Driver driver = mock();

  @Test
  void injectsTheRightImplementation() {
    ErrorFormatter errorFormatter = inject(ErrorFormatter.class);
    assertThat(errorFormatter).isInstanceOf(ErrorFormatterWithPageUrl.class);
  }

  @Test
  void pageUrl() {
    when(driver.url()).thenReturn("https://test.com");
    assertThat(formatter.pageUrl(driver)).isEqualTo("Page url: https://test.com");
  }

  @Test
  void pageUrl_exception() {
    when(driver.url()).thenThrow(new WebDriverException("Browser has been closed?.."));
    assertThat(formatter.pageUrl(driver)).isEqualTo("");
  }
}
