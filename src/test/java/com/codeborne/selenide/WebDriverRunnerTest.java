package com.codeborne.selenide;

import java.net.URL;

import com.codeborne.selenide.extension.MockWebDriverExtension;
import com.codeborne.selenide.impl.WebDriverThreadLocalContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.FIREFOX;
import static com.codeborne.selenide.WebDriverRunner.HTMLUNIT;
import static java.lang.Thread.currentThread;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockWebDriverExtension.class)
class WebDriverRunnerTest {
  private static WebDriver driver;

  private URL url = currentThread().getContextClassLoader().getResource("start_page.html");

  @BeforeEach
  void resetWebDriverContainer() {
    driver = mock(RemoteWebDriver.class, RETURNS_DEEP_STUBS);
    doReturn(mock(Navigation.class)).when(driver).navigate();

    WebDriverRunner.webdriverContainer = spy(new WebDriverThreadLocalContainer());
    doReturn(null).when((JavascriptExecutor) driver).executeScript(anyString(), any());
  }

  @AfterEach
  void resetSettings() {
    WebDriverRunner.closeWebDriver();
    driver = null;
    Configuration.browser = System.getProperty("browser", FIREFOX);
  }

  @Test
  void allowsToSpecifyCustomWebDriverConfiguration() {
    WebDriverRunner.closeWebDriver();
    Configuration.browser = "com.codeborne.selenide.WebDriverRunnerTest$CustomWebDriverProvider";

    Assertions.assertSame(driver, WebDriverRunner.getWebDriver());
  }

  @Test
  void allowsToSpecifyCustomWebDriverProgrammatically() {
    RemoteWebDriver myDriver = mock(RemoteWebDriver.class);
    doReturn(mock(Navigation.class)).when(myDriver).navigate();
    WebDriverRunner.setWebDriver(myDriver);

    open(url);
    Assertions.assertSame(myDriver, WebDriverRunner.getWebDriver());
  }

  @Test
  void userCanAddWebDriverListeners() {
    WebDriverEventListener listener1 = mock(WebDriverEventListener.class);
    WebDriverRunner.addListener(listener1);
    Configuration.browser = HTMLUNIT;
    open(url);
    verify(listener1).beforeNavigateTo(eq(url.toString()), any(WebDriver.class));
  }

  @Test
  void chrome_supportsAlerts() {
    Configuration.browser = "chrome";
    Assertions.assertTrue(WebDriverRunner.supportsModalDialogs());
  }

  @Test
  void headless_chrome_supportsAlerts() {
    Configuration.browser = "chrome";
    Configuration.headless = true;
    Assertions.assertTrue(WebDriverRunner.supportsModalDialogs());
  }

  @Test
  void firefox_supportsAlerts() {
    Configuration.browser = "firefox";
    Assertions.assertTrue(WebDriverRunner.supportsModalDialogs());
  }

  @Test
  void headless_firefox_supportsAlerts() {
    Configuration.browser = "firefox";
    Configuration.headless = true;
    Assertions.assertTrue(WebDriverRunner.supportsModalDialogs());
  }

  @Test
  void safari_doesNotSupportAlerts() {
    Configuration.browser = "safari";
    Assertions.assertFalse(WebDriverRunner.supportsModalDialogs());
  }

  @Test
  void phantomjs_doesNotSupportAlerts() {
    Configuration.browser = "phantomjs";
    Assertions.assertFalse(WebDriverRunner.supportsModalDialogs());
  }

  private static class CustomWebDriverProvider implements WebDriverProvider {
    @Override
    public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
      return driver;
    }
  }
}
