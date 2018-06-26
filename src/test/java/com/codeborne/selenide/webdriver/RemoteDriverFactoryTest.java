package com.codeborne.selenide.webdriver;

import java.util.Map;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RemoteDriverFactoryTest {
  private RemoteDriverFactory factory = new RemoteDriverFactory();

  @AfterEach
  void tearDown() {
    Configuration.browser = "";
    Configuration.browserBinary = "";
  }

  @Test
  void getBrowserNameForGrid_legacy_firefox() {
    Configuration.browser = "legacy_firefox";
    assertEquals(BrowserType.FIREFOX, factory.getBrowserNameForGrid());
  }

  @Test
  void getBrowserNameForGrid_internet_explorer() {
    Configuration.browser = "internet explorer";
    assertEquals(BrowserType.IE, factory.getBrowserNameForGrid());
  }

  @Test
  void getBrowserNameForGrid_ie() {
    Configuration.browser = "ie";
    assertEquals(BrowserType.IE, factory.getBrowserNameForGrid());
  }

  @Test
  void getBrowserNameForGrid_edge() {
    Configuration.browser = "edge";
    assertEquals(BrowserType.EDGE, factory.getBrowserNameForGrid());
  }

  @Test
  void getBrowserNameForGrid_opera() {
    Configuration.browser = "opera";
    assertEquals(BrowserType.OPERA_BLINK, factory.getBrowserNameForGrid());
  }

  @Test
  void getBrowserNameForGrid_other_browsers() {
    Configuration.browser = "anotherWebdriver";
    assertEquals("anotherWebdriver", factory.getBrowserNameForGrid());
  }

  @Test
  void browserBinaryCanBeSetForFirefox() {
    Configuration.browser = "firefox";
    Configuration.browserBinary = "c:/browser.exe";
    Capabilities caps = factory.getBrowserBinaryCapabilites();
    Map options = (Map) caps.asMap().get(FirefoxOptions.FIREFOX_OPTIONS);
    assertThat(options.get("binary"), is("c:/browser.exe"));
  }

  @Test
  void browserBinaryCanBeSetForChrome() {
    Configuration.browser = "chrome";
    Configuration.browserBinary = "c:/browser.exe";
    Capabilities caps = factory.getBrowserBinaryCapabilites();
    Map options = (Map) caps.asMap().get(ChromeOptions.CAPABILITY);
    assertThat(options.get("binary"), is("c:/browser.exe"));
  }

  @Test
  void browserBinaryCanNotBeSetForOtherBrowsers() {
    Configuration.browserBinary = "c:/browser.exe";
    Configuration.browser = "opera";
    assertEquals(new DesiredCapabilities(), factory.getBrowserBinaryCapabilites());
    Configuration.browser = "edge";
    assertEquals(new DesiredCapabilities(), factory.getBrowserBinaryCapabilites());
    Configuration.browser = "ie";
    assertEquals(new DesiredCapabilities(), factory.getBrowserBinaryCapabilites());
  }
}
