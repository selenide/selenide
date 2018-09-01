package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class BrowserResizerTest {
  private BrowserResizer factory = spy(new BrowserResizer());
  private WebDriver webdriver = mock(WebDriver.class, RETURNS_DEEP_STUBS);
  private Browser browser = new Browser("firefox", true);

  @BeforeEach
  void setUp() {
    Configuration.browser = "";
    Configuration.browserSize = null;
    Configuration.browserPosition = null;
    Configuration.startMaximized = false;
  }

  @Test
  void doesNotChangeWindowSizeByDefault() {
    factory.adjustBrowserSize(browser, webdriver);
    verifyNoMoreInteractions(webdriver);
  }

  @Test
  void canConfigureBrowserWindowSize() {
    Configuration.browserSize = "1600x800";

    factory.adjustBrowserSize(browser, webdriver);

    verify(webdriver.manage().window()).setSize(new Dimension(1600, 800));
  }

  @Test
  void canMaximizeBrowserWindow() {
    Configuration.startMaximized = true;

    factory.adjustBrowserSize(browser, webdriver);

    verify(webdriver.manage().window()).maximize();
  }

  @Test
  void canMaximizeBrowserWindow_chrome() {
    Configuration.startMaximized = true;
    doReturn(new Dimension(1600, 1200)).when(factory).getScreenSize();

    factory.adjustBrowserSize(new Browser("chrome", true), webdriver);

    verify(webdriver.manage().window()).setSize(new Dimension(1600, 1200));
    verify(webdriver.manage().window()).setPosition(new Point(0, 0));
  }

  @Test
  void canConfigureBrowserWindowPosition() {
    Configuration.browserPosition = "20x40";

    factory.adjustBrowserPosition(webdriver);

    verify(webdriver.manage().window()).setPosition(new Point(20, 40));
  }
}
