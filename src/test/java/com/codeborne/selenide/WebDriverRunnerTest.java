package com.codeborne.selenide;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.FIREFOX;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

public class WebDriverRunnerTest {

  static WebDriver driver = mock(WebDriver.class, RETURNS_DEEP_STUBS);

  @Test
  public void allowsToSpecifyCustomWebDriverConfiguration() {
    WebDriverRunner.closeWebDriver();
    Configuration.browser = "com.codeborne.selenide.WebDriverRunnerTest$CustomWebDriverProvider";

    try {
      assertSame(driver, WebDriverRunner.getWebDriver());
    } finally {
      WebDriverRunner.closeWebDriver();
      Configuration.browser = System.getProperty("browser", FIREFOX);
    }
  }

  @Test
  public void allowsToSpecifyCustomWebDriverProgrammatically() {
    HtmlUnitDriver myDriver = new HtmlUnitDriver();
    myDriver.setJavascriptEnabled(true);
    WebDriverRunner.setWebDriver(myDriver);
    open(currentThread().getContextClassLoader().getResource("page_with_selects_without_jquery.html"));
    try {
      assertSame(myDriver, WebDriverRunner.getWebDriver());
    } finally {
      WebDriverRunner.closeWebDriver();
    }
  }

  public static class CustomWebDriverProvider implements WebDriverProvider {
    @Override
    public WebDriver createDriver() {
      return driver;
    }
  }
}
