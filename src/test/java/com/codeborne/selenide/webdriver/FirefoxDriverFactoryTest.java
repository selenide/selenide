package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.SelenideConfig;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchArgs;
import static org.mockito.Mockito.mock;

class FirefoxDriverFactoryTest implements WithAssertions {
  private final Proxy proxy = mock(Proxy.class);
  private final FirefoxDriverFactory driverFactory = new FirefoxDriverFactory();
  private final SelenideConfig config = new SelenideConfig().downloadsFolder("/blah/downloads");
  private final Browser browser = new Browser(config.browser(), config.headless());

  @AfterEach
  void tearDown() {
    System.clearProperty("capabilities.some.cap");
    System.clearProperty("firefoxprofile.some.cap");
  }

  @Test
  void transfersStringCapabilitiesFromSystemPropsToDriver() {
    System.setProperty("capabilities.some.cap", "abcd");
    assertThat(driverFactory.createCommonCapabilities(config, browser, proxy).getCapability("some.cap")).isEqualTo("abcd");
  }

  @Test
  void transfersBooleanCapabilitiesFromSystemPropsToDriver() {
    System.setProperty("capabilities.some.cap", "true");
    assertThat(driverFactory.createCommonCapabilities(config, browser, proxy).getCapability("some.cap"))
      .isEqualTo(true);
  }

  @Test
  void transfersIntegerCapabilitiesFromSystemPropsToDriver() {
    System.setProperty("capabilities.some.cap", "25");
    assertThat(driverFactory.createCommonCapabilities(config, browser, proxy).getCapability("some.cap")).isEqualTo(25);
  }

  @Test
  void keepConfigurationFirefoxProfileWhenTransferPreferencesFromSystemPropsToDriver() {
    FirefoxProfile configurationProfile = new FirefoxProfile();
    configurationProfile.setPreference("some.conf.cap", 42);
    FirefoxOptions firefoxOptions = new FirefoxOptions().setProfile(configurationProfile);
    config.browserCapabilities(new DesiredCapabilities(firefoxOptions));
    System.setProperty("firefoxprofile.some.cap", "25");

    FirefoxProfile profile = driverFactory.createCapabilities(config, browser, proxy).getProfile();

    assertThat(profile.getIntegerPreference("some.cap", 0)).isEqualTo(25);
    assertThat(profile.getIntegerPreference("some.conf.cap", 0)).isEqualTo(42);
  }

  @Test
  void transferIntegerFirefoxProfilePreferencesFromSystemPropsToDriver() {
    System.setProperty("firefoxprofile.some.cap", "25");
    FirefoxProfile profile = driverFactory.createCapabilities(config, browser, proxy).getProfile();
    assertThat(profile.getIntegerPreference("some.cap", 0)).isEqualTo(25);
  }

  @Test
  void transferBooleanFirefoxProfilePreferencesFromSystemPropsToDriver() {
    System.setProperty("firefoxprofile.some.cap1", "faLSe");
    System.setProperty("firefoxprofile.some.cap2", "TRue");
    FirefoxProfile profile = driverFactory.createCapabilities(config, browser, proxy).getProfile();
    assertThat(profile.getBooleanPreference("some.cap1", true)).isEqualTo(false);
    assertThat(profile.getBooleanPreference("some.cap2", false)).isEqualTo(true);
  }

  @Test
  void transferStringFirefoxProfilePreferencesFromSystemPropsToDriver() {
    System.setProperty("firefoxprofile.some.cap", "abdd");
    FirefoxProfile profile = driverFactory.createCapabilities(config, browser, proxy).getProfile();
    assertThat(profile.getStringPreference("some.cap", "sjlj")).isEqualTo("abdd");
  }

  @Test
  void browserBinaryCanBeSet() {
    config.browserBinary("c:/browser.exe");
    Capabilities caps = driverFactory.createCapabilities(config, browser, proxy);
    Map options = (Map) caps.asMap().get(FirefoxOptions.FIREFOX_OPTIONS);
    assertThat(options.get("binary")).isEqualTo("c:/browser.exe");
  }

  @Test
  void headlessCanBeSet() {
    config.headless(true);
    FirefoxOptions options = driverFactory.createCapabilities(config, browser, proxy);
    List<String> optionArguments = getBrowserLaunchArgs(FirefoxOptions.FIREFOX_OPTIONS, options);
    assertThat(optionArguments).contains("-headless");
  }

  @Test
  void enablesProxyForLocalAddresses() {
    FirefoxOptions options = driverFactory.createCapabilities(config, browser, proxy);
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

  @Test
  void configuresDownloadFolder() {
    config.headless(true);

    FirefoxOptions options = driverFactory.createCapabilities(config, browser, proxy);

    FirefoxProfile profile = options.getProfile();
    assertThat(profile.getStringPreference("browser.download.dir", "")).isEqualTo(new File("/blah/downloads").getAbsolutePath());
    assertThat(profile.getStringPreference("browser.helperApps.neverAsk.saveToDisk", "")).contains("application/pdf");
    assertThat(profile.getBooleanPreference("pdfjs.disabled", false)).isTrue();
    assertThat(profile.getIntegerPreference("browser.download.folderList", 0)).isEqualTo(2);
  }

  @Test
  void shouldNotSetupDownloadFolder_forRemoteWebdriver() {
    config.headless(true);
    config.remote("https://some.remote.blah:1234/wd");

    FirefoxOptions options = driverFactory.createCapabilities(config, browser, proxy);

    FirefoxProfile profile = options.getProfile();
    assertThat(profile.getStringPreference("browser.download.dir", "")).isEqualTo("");
    assertThat(profile.getStringPreference("browser.helperApps.neverAsk.saveToDisk", "")).contains("application/pdf");
    assertThat(profile.getBooleanPreference("pdfjs.disabled", false)).isTrue();
    assertThat(profile.getIntegerPreference("browser.download.folderList", 0)).isEqualTo(2);
  }
}
