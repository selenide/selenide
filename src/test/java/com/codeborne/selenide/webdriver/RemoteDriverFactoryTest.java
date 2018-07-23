package com.codeborne.selenide.webdriver;

import java.util.Map;

import com.codeborne.selenide.Configuration;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;

class RemoteDriverFactoryTest implements WithAssertions {
  private RemoteDriverFactory factory = new RemoteDriverFactory();

  @AfterEach
  void tearDown() {
    Configuration.browser = "";
    Configuration.browserBinary = "";
  }

  @Test
  void getBrowserNameForGrid_legacy_firefox() {
    Configuration.browser = "legacy_firefox";
    assertThat(factory.getBrowserNameForGrid())
      .isEqualTo(BrowserType.FIREFOX);
  }

  @Test
  void getBrowserNameForGrid_internet_explorer() {
    Configuration.browser = "internet explorer";
    assertThat(factory.getBrowserNameForGrid())
      .isEqualTo(BrowserType.IE);
  }

  @Test
  void getBrowserNameForGrid_ie() {
    Configuration.browser = "ie";
    assertThat(factory.getBrowserNameForGrid())
      .isEqualTo(BrowserType.IE);
  }

  @Test
  void getBrowserNameForGrid_edge() {
    Configuration.browser = "edge";
    assertThat(factory.getBrowserNameForGrid())
      .isEqualTo(BrowserType.EDGE);
  }

  @Test
  void getBrowserNameForGrid_opera() {
    Configuration.browser = "opera";
    assertThat(factory.getBrowserNameForGrid())
      .isEqualTo(BrowserType.OPERA_BLINK);
  }

  @Test
  void getBrowserNameForGrid_other_browsers() {
    Configuration.browser = "anotherWebdriver";
    assertThat(factory.getBrowserNameForGrid())
      .isEqualTo("anotherWebdriver");
  }

  @Test
  void browserBinaryCanBeSetForFirefox() {
    Configuration.browser = "firefox";
    Configuration.browserBinary = "c:/browser.exe";
    Capabilities caps = factory.getBrowserBinaryCapabilites();
    Map options = (Map) caps.asMap().get(FirefoxOptions.FIREFOX_OPTIONS);
    assertThat(options.get("binary"))
      .isEqualTo("c:/browser.exe");
  }

  @Test
  void browserBinaryCanBeSetForChrome() {
    Configuration.browser = "chrome";
    Configuration.browserBinary = "c:/browser.exe";
    Capabilities caps = factory.getBrowserBinaryCapabilites();
    Map options = (Map) caps.asMap().get(ChromeOptions.CAPABILITY);
    assertThat(options.get("binary"))
      .isEqualTo("c:/browser.exe");
  }

  @Test
  void browserBinaryCanNotBeSetForOtherBrowsers() {
    Configuration.browserBinary = "c:/browser.exe";
    Configuration.browser = "opera";
    assertThat(factory.getBrowserBinaryCapabilites())
      .isEqualTo(new DesiredCapabilities());
    Configuration.browser = "edge";
    assertThat(factory.getBrowserBinaryCapabilites())
      .isEqualTo(new DesiredCapabilities());
    Configuration.browser = "ie";
    assertThat(factory.getBrowserBinaryCapabilites())
      .isEqualTo(new DesiredCapabilities());
  }
}
