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
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

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

  public static class CustomWebDriverProvider implements WebDriverProvider {
    @Override
    public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
      return driver;
    }
  }
}
