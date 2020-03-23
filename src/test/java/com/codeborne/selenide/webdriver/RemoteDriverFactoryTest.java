package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.SelenideConfig;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.BrowserType;

import java.io.File;
import java.util.Map;

import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchPrefs;

final class RemoteDriverFactoryTest implements WithAssertions {
  private final RemoteDriverFactory factory = new RemoteDriverFactory();
  private final File browserDownloadsFolder = new File("build/downlao");
  private final SelenideConfig config = new SelenideConfig().remote("https://some.grid:1234/wd/");

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
      .isEqualTo(BrowserType.OPERA);
  }

  @Test
  void getBrowserNameForGrid_other_browsers() {
    config.browser("anotherWebdriver");
    assertThat(factory.getBrowserNameForGrid(config, new Browser("anotherWebdriver", false)))
      .isEqualTo("anotherWebdriver");
  }

  @SuppressWarnings("rawtypes")
  @Test
  void browserBinaryCanBeSetForFirefox() {
    config.browser("firefox");
    config.browserBinary("c:/browser.exe");
    MutableCapabilities firefoxOptions = new FirefoxDriverFactory().createCapabilities(config, browser(), null, browserDownloadsFolder);

    factory.setupCapabilities(config, browser(), firefoxOptions);

    Map options = (Map) firefoxOptions.asMap().get(FirefoxOptions.FIREFOX_OPTIONS);
    assertThat(options.get("binary"))
      .isEqualTo("c:/browser.exe");
  }

  @SuppressWarnings("rawtypes")
  @Test
  void browserBinaryCanBeSetForChrome() {
    config.browser("chrome");
    config.browserBinary("c:/browser.exe");
    MutableCapabilities chromeOptions = new ChromeDriverFactory().createCapabilities(config, browser(), null, browserDownloadsFolder);

    factory.setupCapabilities(config, browser(), chromeOptions);

    Map options = (Map) chromeOptions.asMap().get(ChromeOptions.CAPABILITY);
    assertThat(options.get("binary"))
      .isEqualTo("c:/browser.exe");
  }

  @Test
  void downloadsFolderShouldNotBeSetForChrome() {
    config.browser("chrome");
    MutableCapabilities chromeOptions = new ChromeDriverFactory().createCapabilities(config, browser(), null, browserDownloadsFolder);

    factory.setupCapabilities(config, browser(), chromeOptions);

    Map<String, Object> prefsMap = getBrowserLaunchPrefs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(prefsMap).doesNotContainKey("download.default_directory");
  }

  private Browser browser() {
    return new Browser(config.browser(), config.headless());
  }
}
