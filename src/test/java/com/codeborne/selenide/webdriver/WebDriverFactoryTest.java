package com.codeborne.selenide.webdriver;

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

class WebDriverFactoryTest {

  private WebDriverFactory factory = spy(new WebDriverFactory());
  private WebDriver webdriver = mock(WebDriver.class, RETURNS_DEEP_STUBS);

  @BeforeEach
  void setUp() {
    Configuration.browser = "";
    Configuration.browserSize = null;
    Configuration.browserPosition = null;
    Configuration.startMaximized = false;
  }

  @Test
  void doesNotChangeWindowSizeByDefault() {
    factory.adjustBrowserSize(webdriver);
    verifyNoMoreInteractions(webdriver);
  }

  @Test
  void canConfigureBrowserWindowSize() {
    Configuration.browserSize = "1600x800";

    factory.adjustBrowserSize(webdriver);

    verify(webdriver.manage().window()).setSize(new Dimension(1600, 800));
  }

  @Test
  void canMaximizeBrowserWindow() {
    Configuration.startMaximized = true;

    factory.adjustBrowserSize(webdriver);

    verify(webdriver.manage().window()).maximize();
  }

  @Test
  void canMaximizeBrowserWindow_chrome() {
    Configuration.startMaximized = true;
    Configuration.browser = "chrome";
    doReturn(new Dimension(1600, 1200)).when(factory).getScreenSize();

    factory.adjustBrowserSize(webdriver);

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
