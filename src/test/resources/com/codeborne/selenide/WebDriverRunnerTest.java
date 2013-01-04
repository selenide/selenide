package com.codeborne.selenide;

import org.junit.Test;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

public class WebDriverRunnerTest {

  static WebDriver driver = mock(WebDriver.class);

  @Test
  public void allowsToSpecifyCustomWebDriverConfiguration() throws Exception {
    System.setProperty("browser", "com.codeborne.selenide.WebDriverRunnerTest$CustomWebDriverProvider");
    assertSame(driver, WebDriverRunner.getWebDriver());
  }

  public static class CustomWebDriverProvider implements WebDriverProvider {

    @Override
    public WebDriver createDriver() {
      return driver;
    }
  }
}
