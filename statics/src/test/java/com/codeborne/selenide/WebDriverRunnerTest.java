package com.codeborne.selenide;

import com.codeborne.selenide.impl.WebDriverThreadLocalContainer;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import java.net.URL;

import static com.codeborne.selenide.Browsers.FIREFOX;
import static com.codeborne.selenide.Browsers.HTMLUNIT;
import static com.codeborne.selenide.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.webdriverContainer;
import static java.lang.Thread.currentThread;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class WebDriverRunnerTest implements WithAssertions {
  private static WebDriver driver;

  private URL url = currentThread().getContextClassLoader().getResource("start_page.html");

  @BeforeEach
  void resetWebDriverContainer() {
    driver = mock(RemoteWebDriver.class, RETURNS_DEEP_STUBS);
    doReturn(mock(Navigation.class)).when(driver).navigate();

    WebDriverRunner.webdriverContainer = spy(new WebDriverThreadLocalContainer());
    doReturn(null).when((JavascriptExecutor) driver).executeScript(anyString(), any());
    Configuration.proxyEnabled = false;
    Configuration.fileDownload = HTTPGET;
  }

  @AfterEach
  void resetSettings() {
    WebDriverRunner.closeWebDriver();
    driver = null;
    Configuration.browser = System.getProperty("browser", FIREFOX);
    webdriverContainer = new WebDriverThreadLocalContainer();
  }

  @Test
  void allowsToSpecifyCustomWebDriverConfiguration() {
    WebDriverRunner.closeWebDriver();
    Configuration.browser = "com.codeborne.selenide.WebDriverRunnerTest$CustomWebDriverProvider";

    assertThat(WebDriverRunner.getAndCheckWebDriver())
      .isEqualTo(driver);
  }

  @Test
  void allowsToSpecifyCustomWebDriverProgrammatically() {
    RemoteWebDriver myDriver = mock(RemoteWebDriver.class);
    doReturn(mock(Navigation.class)).when(myDriver).navigate();
    WebDriverRunner.setWebDriver(myDriver);

    open(url);
    assertThat(WebDriverRunner.getWebDriver())
      .isEqualTo(myDriver);
  }

  @Test
  void userCanAddWebDriverListeners() {
    WebDriverEventListener listener = mock(WebDriverEventListener.class);
    WebDriverRunner.addListener(listener);
    Configuration.browser = HTMLUNIT;
    open(url);
    verify(listener).beforeNavigateTo(eq(url.toString()), any(WebDriver.class));
  }

  @Test
  void chrome_supportsAlerts() {
    Configuration.browser = "chrome";
    assertThat(WebDriverRunner.supportsModalDialogs())
      .isTrue();
  }

  @Test
  void headless_chrome_supportsAlerts() {
    Configuration.browser = "chrome";
    Configuration.headless = true;
    assertThat(WebDriverRunner.supportsModalDialogs())
      .isTrue();
  }

  @Test
  void firefox_supportsAlerts() {
    Configuration.browser = "firefox";
    assertThat(WebDriverRunner.supportsModalDialogs())
      .isTrue();
  }

  @Test
  void headless_firefox_supportsAlerts() {
    Configuration.browser = "firefox";
    Configuration.headless = true;
    assertThat(WebDriverRunner.supportsModalDialogs())
      .isTrue();
  }

  @Test
  void safari_doesNotSupportAlerts() {
    Configuration.browser = "safari";
    assertThat(WebDriverRunner.supportsModalDialogs())
      .isFalse();
  }

  @Test
  void phantomjs_doesNotSupportAlerts() {
    Configuration.browser = "phantomjs";
    assertThat(WebDriverRunner.supportsModalDialogs())
      .isFalse();
  }

  private static class CustomWebDriverProvider implements WebDriverProvider {
    @Override
    public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
      return driver;
    }
  }
}
