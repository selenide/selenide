package com.codeborne.selenide;

import com.codeborne.selenide.impl.WebDriverThreadLocalContainer;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.WebDriverRunner.webdriverContainer;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class SelenideTest {
  @Before
  public void setUp() {
    webdriverContainer = mock(WebDriverThreadLocalContainer.class);
  }

  @Test
  public void getJavascriptErrors_returnsEmptyListIfWebdriverIsNotStarted() {
    doThrow(new RuntimeException("should not start webdriver if not started yet")).when(webdriverContainer).getWebDriver();
    assertEquals(0, Selenide.getJavascriptErrors().size());
  }
}
