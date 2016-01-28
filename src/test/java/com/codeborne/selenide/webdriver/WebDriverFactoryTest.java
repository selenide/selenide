package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

import java.awt.*;

import static org.mockito.Mockito.*;

public class WebDriverFactoryTest {
  WebDriver webdriver = mock(WebDriver.class, RETURNS_DEEP_STUBS);

  @Before
  public void setUp() {
    Configuration.browser = null;
    Configuration.browserSize = null;
    Configuration.startMaximized = false;
  }

  @Test
  public void doesNotChangeWindowSizeByDefault() {
    verifyNoMoreInteractions(webdriver);
  }

  @Test
  public void canConfigureBrowserWindowSize() {
    Configuration.browserSize = "1600x800";

    new WebDriverFactory().adjustBrowserSize(webdriver);
    
    verify(webdriver.manage().window()).setSize(new Dimension(1600, 800));
  }
  
  @Test
  public void canMaximizeBrowserWindow() {
    Configuration.startMaximized = true;
    
    new WebDriverFactory().adjustBrowserSize(webdriver);
    
    verify(webdriver.manage().window()).maximize();
  }
  
  @Test
  public void canMaximizeBrowserWindow_chrome() {
    Configuration.startMaximized = true;
    Configuration.browser = "chrome";

    new WebDriverFactory().adjustBrowserSize(webdriver);

    int expectedBrowserWidth = Toolkit.getDefaultToolkit().getScreenSize().width;  
    int expectedBrowserHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
    verify(webdriver.manage().window()).setSize(new Dimension(expectedBrowserWidth, expectedBrowserHeight));
    verify(webdriver.manage().window()).setPosition(new Point(0, 0));
  }
}
