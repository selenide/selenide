package com.codeborne.selenide.webdriver;

import java.util.List;
import java.util.Map;

import com.codeborne.selenide.Configuration;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchArgs;
import static org.mockito.Mockito.mock;

class FirefoxDriverFactoryTest implements WithAssertions {
  private Proxy proxy = mock(Proxy.class);
  private FirefoxDriverFactory driverFactory;

  @BeforeEach
  void setUp() {
    driverFactory = new FirefoxDriverFactory();
  }

  @AfterEach
  void tearDown() {
    System.clearProperty("capabilities.some.cap");
    System.clearProperty("firefoxprofile.some.cap");
    Configuration.browserBinary = "";
    Configuration.headless = false;
  }

  @Test
  void transfersStringCapabilitiesFromSystemPropsToDriver() {
    System.setProperty("capabilities.some.cap", "abcd");
    assertThat(driverFactory.createCommonCapabilities(proxy).getCapability("some.cap"))
      .isEqualTo("abcd");
  }

  @Test
  void transfersBooleanCapabilitiesFromSystemPropsToDriver() {
    System.setProperty("capabilities.some.cap", "true");
    assertThat(driverFactory.createCommonCapabilities(proxy).getCapability("some.cap"))
      .isEqualTo(true);
  }

  @Test
  void transfersIntegerCapabilitiesFromSystemPropsToDriver() {
    System.setProperty("capabilities.some.cap", "25");
    assertThat(driverFactory.createCommonCapabilities(proxy).getCapability("some.cap"))
      .isEqualTo(25);
  }

  @Test
  void transferIntegerFirefoxProfilePreferencesFromSystemPropsToDriver() {
    System.setProperty("firefoxprofile.some.cap", "25");
    FirefoxProfile profile = driverFactory.createFirefoxOptions(proxy).getProfile();
    assertThat(profile.getIntegerPreference("some.cap", 0))
      .isEqualTo(25);
  }

  @Test
  void transferBooleanFirefoxProfilePreferencesFromSystemPropsToDriver() {
    System.setProperty("firefoxprofile.some.cap", "false");
    FirefoxProfile profile = driverFactory.createFirefoxOptions(proxy).getProfile();
    assertThat(profile.getBooleanPreference("some.cap", true))
      .isEqualTo(false);
  }

  @Test
  void transferStringFirefoxProfilePreferencesFromSystemPropsToDriver() {
    System.setProperty("firefoxprofile.some.cap", "abdd");
    FirefoxProfile profile = driverFactory.createFirefoxOptions(proxy).getProfile();
    assertThat(profile.getStringPreference("some.cap", "sjlj"))
      .isEqualTo("abdd");
  }

  @Test
  void browserBinaryCanBeSet() {
    Configuration.browserBinary = "c:/browser.exe";
    Capabilities caps = driverFactory.createFirefoxOptions(proxy);
    Map options = (Map) caps.asMap().get(FirefoxOptions.FIREFOX_OPTIONS);
    assertThat(options.get("binary"))
      .isEqualTo("c:/browser.exe");
  }

  @Test
  void headlessCanBeSet() {
    Configuration.headless = true;
    FirefoxOptions options = driverFactory.createFirefoxOptions(proxy);
    List<String> optionArguments = getBrowserLaunchArgs(FirefoxOptions.FIREFOX_OPTIONS, options);
    assertThat(optionArguments)
      .contains("-headless");
  }
}
