package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.SelenideConfig;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchArgs;
import static org.mockito.Mockito.mock;

class FirefoxDriverFactoryTest implements WithAssertions {
  private Proxy proxy = mock(Proxy.class);
  private FirefoxDriverFactory driverFactory = new FirefoxDriverFactory();
  private SelenideConfig config = new SelenideConfig();

  @AfterEach
  void tearDown() {
    System.clearProperty("capabilities.some.cap");
    System.clearProperty("firefoxprofile.some.cap");
  }

  @Test
  void transfersStringCapabilitiesFromSystemPropsToDriver() {
    System.setProperty("capabilities.some.cap", "abcd");
    DesiredCapabilities commonCapabilities = new DesiredCapabilities();
    driverFactory.setupCommonCapabilities(commonCapabilities, config, proxy);
    assertThat(commonCapabilities.getCapability("some.cap")).isEqualTo("abcd");
  }

  @Test
  void transfersBooleanCapabilitiesFromSystemPropsToDriver() {
    System.setProperty("capabilities.some.cap", "true");
    DesiredCapabilities commonCapabilities = new DesiredCapabilities();
    driverFactory.setupCommonCapabilities(commonCapabilities, config, proxy);
    assertThat(commonCapabilities.getCapability("some.cap"))
      .isEqualTo(true);
  }

  @Test
  void transfersIntegerCapabilitiesFromSystemPropsToDriver() {
    System.setProperty("capabilities.some.cap", "25");
    DesiredCapabilities commonCapabilities = new DesiredCapabilities();
    driverFactory.setupCommonCapabilities(commonCapabilities, config, proxy);
    assertThat(commonCapabilities.getCapability("some.cap")).isEqualTo(25);
  }

  @Test
  void transferIntegerFirefoxProfilePreferencesFromSystemPropsToDriver() {
    System.setProperty("firefoxprofile.some.cap", "25");
    FirefoxProfile profile = driverFactory.createFirefoxOptions(config, proxy).getProfile();
    assertThat(profile.getIntegerPreference("some.cap", 0)).isEqualTo(25);
  }

  @Test
  void transferBooleanFirefoxProfilePreferencesFromSystemPropsToDriver() {
    System.setProperty("firefoxprofile.some.cap1", "faLSe");
    System.setProperty("firefoxprofile.some.cap2", "TRue");
    FirefoxProfile profile = driverFactory.createFirefoxOptions(config, proxy).getProfile();
    assertThat(profile.getBooleanPreference("some.cap1", true)).isEqualTo(false);
    assertThat(profile.getBooleanPreference("some.cap2", false)).isEqualTo(true);
  }

  @Test
  void transferStringFirefoxProfilePreferencesFromSystemPropsToDriver() {
    System.setProperty("firefoxprofile.some.cap", "abdd");
    FirefoxProfile profile = driverFactory.createFirefoxOptions(config, proxy).getProfile();
    assertThat(profile.getStringPreference("some.cap", "sjlj")).isEqualTo("abdd");
  }

  @Test
  void browserBinaryCanBeSet() {
    config.browserBinary("c:/browser.exe");
    Capabilities caps = driverFactory.createFirefoxOptions(config, proxy);
    Map options = (Map) caps.asMap().get(FirefoxOptions.FIREFOX_OPTIONS);
    assertThat(options.get("binary")).isEqualTo("c:/browser.exe");
  }

  @Test
  void headlessCanBeSet() {
    config.headless(true);
    FirefoxOptions options = driverFactory.createFirefoxOptions(config, proxy);
    List<String> optionArguments = getBrowserLaunchArgs(FirefoxOptions.FIREFOX_OPTIONS, options);
    assertThat(optionArguments).contains("-headless");
  }

  @Test
  void enablesProxyForLocalAddresses() {
    FirefoxOptions options = driverFactory.createFirefoxOptions(config, proxy);
    FirefoxProfile firefoxProfile = (FirefoxProfile) options.asMap().get("firefox_profile");
    assertThat(firefoxProfile.getStringPreference("network.proxy.no_proxies_on", "localhost")).isEqualTo("");
    assertThat(firefoxProfile.getBooleanPreference("network.proxy.allow_hijacking_localhost", false)).isTrue();
  }

  @Test
  void downloadsAllPopularContentTypesWithoutDialog() {
    assertThat(driverFactory.popularContentTypes()).contains(";application/pdf;");
    assertThat(driverFactory.popularContentTypes()).contains(";application/octet-stream;");
    assertThat(driverFactory.popularContentTypes()).contains(";application/msword;");
    assertThat(driverFactory.popularContentTypes()).contains(";application/vnd.ms-excel;");
    assertThat(driverFactory.popularContentTypes()).contains(";application/zip;");
    assertThat(driverFactory.popularContentTypes()).contains(";text/csv;");
  }
}
