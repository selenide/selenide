package com.codeborne.selenide;

import com.codeborne.selenide.impl.WebDriverThreadLocalContainer;
import com.codeborne.selenide.rules.MockWebdriverContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import java.net.URL;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.FIREFOX;
import static com.codeborne.selenide.WebDriverRunner.HTMLUNIT;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class WebDriverRunnerTest {
  static WebDriver driver;

  @Rule
  public MockWebdriverContainer mockWebdriverContainer = new MockWebdriverContainer();

  URL url = currentThread().getContextClassLoader().getResource("start_page.html");

  @Before
  public void resetWebDriverContainer() {
    driver = mock(RemoteWebDriver.class, RETURNS_DEEP_STUBS);
    doReturn(mock(Navigation.class)).when(driver).navigate();

    WebDriverRunner.webdriverContainer = spy(new WebDriverThreadLocalContainer());
    doReturn(null).when((JavascriptExecutor) driver).executeScript(anyString(), any());
  }

  @After
  public void resetSettings() {
    WebDriverRunner.closeWebDriver();
    driver = null;
    Configuration.browser = System.getProperty("browser", FIREFOX);
  }

  @Test
  public void allowsToSpecifyCustomWebDriverConfiguration() {
    WebDriverRunner.closeWebDriver();
    Configuration.browser = "com.codeborne.selenide.WebDriverRunnerTest$CustomWebDriverProvider";

    assertSame(driver, WebDriverRunner.getWebDriver());
  }

  @Test
  public void allowsToSpecifyCustomWebDriverProgrammatically() {
    RemoteWebDriver myDriver = mock(RemoteWebDriver.class);
    doReturn(mock(Navigation.class)).when(myDriver).navigate();
    WebDriverRunner.setWebDriver(myDriver);

    open(url);
    assertSame(myDriver, WebDriverRunner.getWebDriver());
  }

  @Test
  public void userCanAddWebDriverListeners() {
    WebDriverEventListener listener1 = mock(WebDriverEventListener.class);
    WebDriverRunner.addListener(listener1);
    Configuration.browser = HTMLUNIT;
    open(url);
    verify(listener1).beforeNavigateTo(eq(url.toString()), any(WebDriver.class));
  }

  @Test
  public void chrome_supportsAlerts() {
    Configuration.browser = "chrome";
    assertTrue(WebDriverRunner.supportsModalDialogs());
  }

  @Test
  public void headless_chrome_supportsAlerts() {
    Configuration.browser = "chrome";
    Configuration.headless = true;
    assertTrue(WebDriverRunner.supportsModalDialogs());
  }

  @Test
  public void firefox_supportsAlerts() {
    Configuration.browser = "firefox";
    assertTrue(WebDriverRunner.supportsModalDialogs());
  }

  @Test
  public void headless_firefox_supportsAlerts() {
    Configuration.browser = "firefox";
    Configuration.headless = true;
    assertTrue(WebDriverRunner.supportsModalDialogs());
  }

  @Test
  public void safari_doesNotSupportAlerts() {
    Configuration.browser = "safari";
    assertFalse(WebDriverRunner.supportsModalDialogs());
  }

  @Test
  public void phantomjs_doesNotSupportAlerts() {
    Configuration.browser = "phantomjs";
    assertFalse(WebDriverRunner.supportsModalDialogs());
  }

  public static class CustomWebDriverProvider implements WebDriverProvider {
    @Override
    public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
      return driver;
    }
  }
}
