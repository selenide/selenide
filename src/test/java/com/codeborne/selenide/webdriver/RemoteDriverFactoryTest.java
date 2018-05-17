package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Configuration;
import org.junit.*;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.*;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class RemoteDriverFactoryTest {
  RemoteDriverFactory factory = new RemoteDriverFactory();

  @After
  public void tearDown() {
    Configuration.browser = "";
    Configuration.browserBinary = "";
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

  @Test
  public void browserBinaryCanBeSetForFirefox() {
    Configuration.browser = "firefox";
    Configuration.browserBinary = "c:/browser.exe";
    Capabilities caps = factory.getBrowserBinaryCapabilites();
    Map options = (Map) caps.asMap().get(FirefoxOptions.FIREFOX_OPTIONS);
    assertThat(options.get("binary"), is("c:/browser.exe"));
  }

  @Test
  public void browserBinaryCanBeSetForChrome() {
    Configuration.browser = "chrome";
    Configuration.browserBinary = "c:/browser.exe";
    Capabilities caps = factory.getBrowserBinaryCapabilites();
    Map options = (Map) caps.asMap().get(ChromeOptions.CAPABILITY);
    assertThat(options.get("binary"), is("c:/browser.exe"));
  }

  @Test
  public void browserBinaryCanNotBeSetForOtherBrowsers() {
    Configuration.browserBinary = "c:/browser.exe";
    Configuration.browser = "opera";
    assertEquals(new DesiredCapabilities(), factory.getBrowserBinaryCapabilites());
    Configuration.browser = "edge";
    assertEquals(new DesiredCapabilities(), factory.getBrowserBinaryCapabilites());
    Configuration.browser = "ie";
    assertEquals(new DesiredCapabilities(), factory.getBrowserBinaryCapabilites());
  }
}
