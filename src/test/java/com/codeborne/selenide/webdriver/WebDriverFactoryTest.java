package com.codeborne.selenide.webdriver;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.codeborne.selenide.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

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
