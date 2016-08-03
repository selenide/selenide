package com.codeborne.selenide;

import com.codeborne.selenide.rules.MockWebdriverContainer;
import org.junit.Rule;
import org.junit.Test;

import static com.codeborne.selenide.WebDriverRunner.webdriverContainer;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;

public class SelenideTest {
  @Rule
  public MockWebdriverContainer mockWebdriverContainer = new MockWebdriverContainer();

  @Test
  public void getJavascriptErrors_returnsEmptyListIfWebdriverIsNotStarted() {
    doThrow(new RuntimeException("should not start webdriver if not started yet")).when(webdriverContainer).getWebDriver();
    assertEquals(0, Selenide.getJavascriptErrors().size());
  }
}
