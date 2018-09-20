package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.SelenideConfig;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

class RemoteDriverFactoryTest implements WithAssertions {
  private RemoteDriverFactory factory = new RemoteDriverFactory();
  private SelenideConfig config = new SelenideConfig();

  @Test
  void getBrowserNameForGrid_legacy_firefox() {
    config.browser("legacy_firefox");
    assertThat(factory.getBrowserNameForGrid(config, new Browser("legacy_firefox", false)))
      .isEqualTo(BrowserType.FIREFOX);
  }

  @Test
  void getBrowserNameForGrid_internet_explorer() {
    config.browser("internet explorer");
    assertThat(factory.getBrowserNameForGrid(config, new Browser("internet explorer", false)))
      .isEqualTo(BrowserType.IE);
  }

  @Test
  void getBrowserNameForGrid_ie() {
    config.browser("ie");
    assertThat(factory.getBrowserNameForGrid(config, new Browser("ie", false)))
      .isEqualTo(BrowserType.IE);
  }

  @Test
  void getBrowserNameForGrid_edge() {
    config.browser("edge");
    assertThat(factory.getBrowserNameForGrid(config, new Browser("edge", false)))
      .isEqualTo(BrowserType.EDGE);
  }

  @Test
  void getBrowserNameForGrid_opera() {
    assertThat(factory.getBrowserNameForGrid(config, new Browser("opera", false)))
      .isEqualTo(BrowserType.OPERA_BLINK);
  }

  @Test
  void getBrowserNameForGrid_other_browsers() {
    config.browser("anotherWebdriver");
    assertThat(factory.getBrowserNameForGrid(config, new Browser("anotherWebdriver", false)))
      .isEqualTo("anotherWebdriver");
  }

  @Test
  void browserBinaryCanBeSetForFirefox() {
    config.browser("firefox");
    config.browserBinary("c:/browser.exe");
    Capabilities caps = factory.getBrowserBinaryCapabilities(config, new Browser("firefox", false));
    Map options = (Map) caps.asMap().get(FirefoxOptions.FIREFOX_OPTIONS);
    assertThat(options.get("binary"))
      .isEqualTo("c:/browser.exe");
  }

  @Test
  void browserBinaryCanBeSetForChrome() {
    config.browser("chrome");
    config.browserBinary("c:/browser.exe");
    Capabilities caps = factory.getBrowserBinaryCapabilities(config, new Browser("chrome", false));
    Map options = (Map) caps.asMap().get(ChromeOptions.CAPABILITY);
    assertThat(options.get("binary"))
      .isEqualTo("c:/browser.exe");
  }

  @Test
  void browserBinaryCanNotBeSetForOtherBrowsers() {
    config.browserBinary("c:/browser.exe");
    config.browser("opera");
    assertThat(factory.getBrowserBinaryCapabilities(config, new Browser("opera", false)))
      .isEqualTo(new DesiredCapabilities());

    assertThat(factory.getBrowserBinaryCapabilities(config, new Browser("edge", false)))
      .isEqualTo(new DesiredCapabilities());

    assertThat(factory.getBrowserBinaryCapabilities(config, new Browser("ie", false)))
      .isEqualTo(new DesiredCapabilities());
  }
}
