package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.SelenideConfig;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;
import java.util.List;

import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchArgs;

final class RemoteDriverFactoryHeadlessOptionsTest implements WithAssertions {
  private final RemoteDriverFactory factory = new RemoteDriverFactory();
  private final File browserDownloadsFolder = new File("build/downlao");
  private final SelenideConfig config = new SelenideConfig().headless(false);

  @Test
  void shouldAddChromeHeadlessOptions() {
    config.headless(true);
    config.browser("chrome");
    MutableCapabilities chromeOptions = new ChromeDriverFactory().createCapabilities(config, browser(), null, browserDownloadsFolder);

    factory.setupCapabilities(config, browser(), chromeOptions);
    List<String> launchArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(launchArguments).contains("--headless");
  }

  @Test
  void shouldNotAddFirefoxHeadlessOptions() {
    config.headless(true);
    config.browser("firefox");
    MutableCapabilities firefoxOptions = new FirefoxDriverFactory().createCapabilities(config, browser(), null, browserDownloadsFolder);

    factory.setupCapabilities(config, browser(), firefoxOptions);
    List<String> launchArguments = getBrowserLaunchArgs(FirefoxOptions.FIREFOX_OPTIONS, firefoxOptions);

    assertThat(launchArguments).doesNotContain("--headless");
  }

  @Test
  void shouldNotAddChromeHeadlessOptions() {
    config.browser("chrome");
    config.headless(false);
    MutableCapabilities chromeOptions = new ChromeDriverFactory().createCapabilities(config, browser(), null, browserDownloadsFolder);

    factory.setupCapabilities(config, browser(), chromeOptions);
    List<String> launchArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(launchArguments).doesNotContain("--headless");
    assertThat(launchArguments).doesNotContain("--disable-gpu");
  }

  @Test
  void shouldAddFirefoxHeadlessOptions() {
    config.browser("firefox");
    config.headless(false);
    MutableCapabilities firefoxOptions = new FirefoxDriverFactory().createCapabilities(config, browser(), null, browserDownloadsFolder);

    factory.setupCapabilities(config, browser(), firefoxOptions);
    List<String> launchArguments = getBrowserLaunchArgs(FirefoxOptions.FIREFOX_OPTIONS, firefoxOptions);

    assertThat(launchArguments).doesNotContain("--headless");
  }

  private Browser browser() {
    return new Browser(config.browser(), config.headless());
  }
}
