package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Configuration;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.remote.BrowserType;

import static org.junit.Assert.*;

public class RemoteDriverFactoryTest {
  RemoteDriverFactory factory = new RemoteDriverFactory();
  
  @After
  public void tearDown() {
    Configuration.browser = null;
  }

  @Test
  public void getBrowserNameForGrid_legacy_firefox() {
    Configuration.browser = "legacy_firefox";
    assertEquals(BrowserType.FIREFOX, factory.getBrowserNameForGrid());
  }

  @Test
  public void getBrowserNameForGrid_internet_explorer() {
    Configuration.browser = "internet explorer";
    assertEquals(BrowserType.IE, factory.getBrowserNameForGrid());
  }

  @Test
  public void getBrowserNameForGrid_ie() {
    Configuration.browser = "ie";
    assertEquals(BrowserType.IE, factory.getBrowserNameForGrid());
  }

  @Test
  public void getBrowserNameForGrid_edge() {
    Configuration.browser = "edge";
    assertEquals(BrowserType.EDGE, factory.getBrowserNameForGrid());
  }

  @Test
  public void getBrowserNameForGrid_opera() {
    Configuration.browser = "opera";
    assertEquals(BrowserType.OPERA_BLINK, factory.getBrowserNameForGrid());
  }

  @Test
  public void getBrowserNameForGrid_other_browsers() {
    Configuration.browser = "anotherWebdriver";
    assertEquals("anotherWebdriver", factory.getBrowserNameForGrid());
  }
}