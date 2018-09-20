package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.SelenideConfig;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.List;

import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchArgs;
import static org.mockito.Mockito.mock;

class RemoteDriverFactoryHeadlessOptionsTest implements WithAssertions {
  private RemoteDriverFactory factory = new RemoteDriverFactory();
  private Proxy proxy = mock(Proxy.class);
  private SelenideConfig config = new SelenideConfig();

  @Test
  void shouldNotAddChromeHeadlessOptions() {
    config.headless(true);
    config.browser("chrome");

    Capabilities headlessCapabilities = factory.getDriverCapabilities(config, new Browser("chrome", true), proxy);
    List<String> launchArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, headlessCapabilities);

    assertThat(launchArguments).contains("--headless");
    assertThat(launchArguments).contains("--disable-gpu");
  }

  @Test
  void shouldNotAddFirefoxHeadlessOptions() {
    config.headless(true);
    config.browser("firefox");

    Capabilities headlessCapabilities = factory.getDriverCapabilities(config, new Browser("firefox", true), proxy);
    List<String> launchArguments = getBrowserLaunchArgs(FirefoxOptions.FIREFOX_OPTIONS, headlessCapabilities);

    assertThat(launchArguments).doesNotContain("--headless");
  }

  @Test
  void shouldAddChromeHeadlessOptions() {
    config.browser("chrome");

    Capabilities headlessCapabilities = factory.getDriverCapabilities(config, new Browser("chrome", false), proxy);
    List<String> launchArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, headlessCapabilities);

    assertThat(launchArguments).doesNotContain("--headless");
    assertThat(launchArguments).doesNotContain("--disable-gpu");
  }

  @Test
  void shouldAddFirefoxHeadlessOptions() {
    config.browser("firefox");

    Capabilities headlessCapabilities = factory.getDriverCapabilities(config, new Browser("firefox", false), proxy);
    List<String> launchArguments = getBrowserLaunchArgs(FirefoxOptions.FIREFOX_OPTIONS, headlessCapabilities);

    assertThat(launchArguments).doesNotContain("--headless");
  }
}
