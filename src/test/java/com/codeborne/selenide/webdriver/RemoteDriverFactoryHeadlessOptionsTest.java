package com.codeborne.selenide.webdriver;

import java.util.List;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.UnitTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchArgs;
import static org.mockito.Mockito.mock;

class RemoteDriverFactoryHeadlessOptionsTest extends UnitTest {
  private RemoteDriverFactory factory = new RemoteDriverFactory();
  private Proxy proxy = mock(Proxy.class);

  @BeforeEach
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

    assertThat(launchArguments)
      .contains("--headless");
    assertThat(launchArguments)
      .contains("--disable-gpu");
  }

  @Test
  void shouldNotAddFirefoxHeadlessOptions() {
    Configuration.headless = true;
    Configuration.browser = "firefox";

    Capabilities headlessCapabilities = factory.getDriverCapabilities(proxy);
    List<String> launchArguments = getBrowserLaunchArgs(FirefoxOptions.FIREFOX_OPTIONS, headlessCapabilities);

    assertThat(launchArguments)
      .doesNotContain("--headless");
  }

  @Test
  void shouldAddChromeHeadlessOptions() {
    Configuration.browser = "chrome";

    Capabilities headlessCapabilities = factory.getDriverCapabilities(proxy);
    List<String> launchArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, headlessCapabilities);

    assertThat(launchArguments)
      .doesNotContain("--headless");
    assertThat(launchArguments)
      .doesNotContain("--disable-gpu");
  }

  @Test
  void shouldAddFirefoxHeadlessOptions() {
    Configuration.browser = "firefox";

    Capabilities headlessCapabilities = factory.getDriverCapabilities(proxy);
    List<String> launchArguments = getBrowserLaunchArgs(FirefoxOptions.FIREFOX_OPTIONS, headlessCapabilities);

    assertThat(launchArguments)
      .doesNotContain("--headless");
  }
}
