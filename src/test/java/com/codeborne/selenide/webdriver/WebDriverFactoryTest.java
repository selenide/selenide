package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

import static org.mockito.Mockito.*;

public class WebDriverFactoryTest {

  WebDriverFactory factory = spy(new WebDriverFactory());
  WebDriver webdriver = mock(WebDriver.class, RETURNS_DEEP_STUBS);

  @Before
  public void setUp() {
    Configuration.browser = null;
    Configuration.browserSize = null;
    Configuration.browserPosition = null;
    Configuration.startMaximized = false;
  }

  @Test
  public void doesNotChangeWindowSizeByDefault() {
    factory.adjustBrowserSize(webdriver);
    verifyNoMoreInteractions(webdriver);
  }

  @Test
  public void canConfigureBrowserWindowSize() {
    Configuration.browserSize = "1600x800";

    factory.adjustBrowserSize(webdriver);

    verify(webdriver.manage().window()).setSize(new Dimension(1600, 800));
  }

  @Test
  public void canMaximizeBrowserWindow() {
    Configuration.startMaximized = true;

    factory.adjustBrowserSize(webdriver);

    verify(webdriver.manage().window()).maximize();
  }

  @Test
  public void canMaximizeBrowserWindow_chrome() {
    Configuration.startMaximized = true;
    Configuration.browser = "chrome";

    factory.adjustBrowserSize(webdriver);

    verify(webdriver.manage().window()).maximize();
  }

  @Test
  public void canMaximizeBrowserWindow_chromeException() {
    Configuration.startMaximized = true;
    Configuration.browser = "chrome";

    WebDriver.Options options = Mockito.mock(WebDriver.Options.class);
    WebDriver.Window window = Mockito.mock(WebDriver.Window.class);
    doReturn(window).when(options).window();
    doReturn(options).when(webdriver).manage();
    doThrow(new RuntimeException("Chrome webdriver cannot maximize the window!")).when(window).maximize();
    doReturn(new Dimension(1600, 1200)).when(factory).getScreenSize();

    factory.adjustBrowserSize(webdriver);
    verify(webdriver.manage().window()).setSize(new Dimension(1600, 1200));
    verify(webdriver.manage().window()).setPosition(new Point(0, 0));
  }

  @Test
  public void canConfigureBrowserWindowPosition() {
    Configuration.browserPosition = "20x40";

    factory.adjustBrowserPosition(webdriver);

    verify(webdriver.manage().window()).setPosition(new Point(20, 40));
  }
}
