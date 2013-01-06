package com.codeborne.selenide;

import org.junit.Test;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.WebDriverRunner.FIREFOX;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

public class WebDriverRunnerTest {

  static WebDriver driver = mock(WebDriver.class);

  @Test
  public void allowsToSpecifyCustomWebDriverConfiguration() {
    WebDriverRunner.closeWebDriver();
    WebDriverRunner.browser = "com.codeborne.selenide.WebDriverRunnerTest$CustomWebDriverProvider";

    try {
      assertSame(driver, WebDriverRunner.getWebDriver());
    } finally {
      WebDriverRunner.closeWebDriver();
      WebDriverRunner.browser = System.getProperty("browser", FIREFOX);
    }
  }

  public static class CustomWebDriverProvider implements WebDriverProvider {
    @Override
    public WebDriver createDriver() {
      return driver;
    }
  }
}
