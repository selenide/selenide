package com.codeborne.selenide.webdriver;

import java.util.List;

import com.codeborne.selenide.Configuration;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchArgs;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.mockito.Mockito.mock;

class RemoteDriverFactoryHeadlessOptionsTest {

  private RemoteDriverFactory factory = new RemoteDriverFactory();
  private Proxy proxy = mock(Proxy.class);

  @AfterEach
  void tearDown() {
    Configuration.browser = "";
    Configuration.headless = false;
  }

  @Test
  void shouldNotAddChromeHeadlessOptions() {
    Configuration.headless = true;
    Configuration.browser = "chrome";

    Capabilities headlessCapabilities = factory.getDriverCapabilities(proxy);
    List<String> launchArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, headlessCapabilities);

    MatcherAssert.assertThat(launchArguments, hasItems("--headless"));
    MatcherAssert.assertThat(launchArguments, hasItems("--disable-gpu"));
  }

  @Test
  void shouldNotAddFirefoxHeadlessOptions() {
    Configuration.headless = true;
    Configuration.browser = "firefox";

    Capabilities headlessCapabilities = factory.getDriverCapabilities(proxy);
    List<String> launchArguments = getBrowserLaunchArgs(FirefoxOptions.FIREFOX_OPTIONS, headlessCapabilities);

    MatcherAssert.assertThat(launchArguments, hasItems("-headless"));
  }

  @Test
  void shouldAddChromeHeadlessOptions() {
    Configuration.browser = "chrome";

    Capabilities headlessCapabilities = factory.getDriverCapabilities(proxy);
    List<String> launchArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, headlessCapabilities);

    MatcherAssert.assertThat(launchArguments, not(hasItems("--headless")));
    MatcherAssert.assertThat(launchArguments, not(hasItems("--disable-gpu")));
  }

  @Test
  void shouldAddFirefoxHeadlessOptions() {
    Configuration.browser = "firefox";

    Capabilities headlessCapabilities = factory.getDriverCapabilities(proxy);
    List<String> launchArguments = getBrowserLaunchArgs(FirefoxOptions.FIREFOX_OPTIONS, headlessCapabilities);

    MatcherAssert.assertThat(launchArguments, not(hasItems("-headless")));
  }
}
