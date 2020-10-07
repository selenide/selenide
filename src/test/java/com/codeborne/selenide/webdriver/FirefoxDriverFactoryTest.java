package com.codeborne.selenide.webdriver;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchArgs;
import static org.mockito.Mockito.mock;

final class FirefoxDriverFactoryTest implements WithAssertions {
  private static final String DOWNLOADS_FOLDER = Paths.get("blah", "downloads").toString();

  private final Proxy proxy = mock(Proxy.class);
  private final FirefoxDriverFactory driverFactory = new FirefoxDriverFactory();
  private final SelenideConfig config = new SelenideConfig().downloadsFolder("build/should-not-be-used");
  private final File browserDownloadsFolder = new File(DOWNLOADS_FOLDER);
  private final Browser browser = new Browser(config.browser(), config.headless());
  private final Set<String> systemProperties = new HashSet<>();

  @AfterEach
  void tearDown() {
    for (String name : systemProperties) {
      System.clearProperty(name);
    }
  }

  private void givenSystemProperty(String name, String value) {
    systemProperties.add(name);
    System.setProperty(name, value);
  }

  @Test
  void transfersStringCapabilitiesFromSystemPropsToDriver() {
    givenSystemProperty("capabilities.some.cap", "abcd");
    assertThat(driverFactory.createCommonCapabilities(config, browser, proxy).getCapability("some.cap")).isEqualTo("abcd");
  }

  @Test
  void transfersBooleanCapabilitiesFromSystemPropsToDriver() {
    givenSystemProperty("capabilities.some.cap", "true");
    assertThat(driverFactory.createCommonCapabilities(config, browser, proxy).getCapability("some.cap"))
      .isEqualTo(true);
  }

  @Test
  void transfersIntegerCapabilitiesFromSystemPropsToDriver() {
    givenSystemProperty("capabilities.some.cap", "25");
    assertThat(driverFactory.createCommonCapabilities(config, browser, proxy).getCapability("some.cap")).isEqualTo(25);
  }

  @Test
  void keepConfigurationFirefoxProfileWhenTransferPreferencesFromSystemPropsToDriver() {
    FirefoxProfile configurationProfile = new FirefoxProfile();
    configurationProfile.setPreference("some.conf.cap", 42);
    FirefoxOptions firefoxOptions = new FirefoxOptions().setProfile(configurationProfile);
    config.browserCapabilities(new DesiredCapabilities(firefoxOptions));
    givenSystemProperty("firefoxprofile.some.cap", "25");

    FirefoxProfile profile = driverFactory.createCapabilities(config, browser, proxy, browserDownloadsFolder).getProfile();

    assertThat(profile.getIntegerPreference("some.cap", 0)).isEqualTo(25);
    assertThat(profile.getIntegerPreference("some.conf.cap", 0)).isEqualTo(42);
  }

  @Test
  void transferIntegerFirefoxProfilePreferencesFromSystemPropsToDriver() {
    givenSystemProperty("firefoxprofile.some.cap", "25");
    FirefoxProfile profile = driverFactory.createCapabilities(config, browser, proxy, browserDownloadsFolder).getProfile();
    assertThat(profile.getIntegerPreference("some.cap", 0)).isEqualTo(25);
  }

  @Test
  void transferBooleanFirefoxProfilePreferencesFromSystemPropsToDriver() {
    givenSystemProperty("firefoxprofile.some.cap1", "faLSe");
    givenSystemProperty("firefoxprofile.some.cap2", "TRue");
    FirefoxProfile profile = driverFactory.createCapabilities(config, browser, proxy, browserDownloadsFolder).getProfile();
    assertThat(profile.getBooleanPreference("some.cap1", true)).isEqualTo(false);
    assertThat(profile.getBooleanPreference("some.cap2", false)).isEqualTo(true);
  }

  @Test
  void transferStringFirefoxProfilePreferencesFromSystemPropsToDriver() {
    givenSystemProperty("firefoxprofile.some.cap", "abdd");
    FirefoxProfile profile = driverFactory.createCapabilities(config, browser, proxy, browserDownloadsFolder).getProfile();
    assertThat(profile.getStringPreference("some.cap", "sjlj")).isEqualTo("abdd");
  }

  @Test
  void browserBinaryCanBeSet() {
    config.browserBinary("c:/browser.exe");
    Capabilities caps = driverFactory.createCapabilities(config, browser, proxy, browserDownloadsFolder);
    Map options = (Map) caps.asMap().get(FirefoxOptions.FIREFOX_OPTIONS);
    assertThat(options.get("binary")).isEqualTo("c:/browser.exe");
  }

  @Test
  void headlessCanBeSet() {
    config.headless(true);
    FirefoxOptions options = driverFactory.createCapabilities(config, browser, proxy, browserDownloadsFolder);
    List<String> optionArguments = getBrowserLaunchArgs(FirefoxOptions.FIREFOX_OPTIONS, options);
    assertThat(optionArguments).contains("-headless");
  }

  @Test
  void enablesProxyForLocalAddresses() {
    FirefoxOptions options = driverFactory.createCapabilities(config, browser, proxy, browserDownloadsFolder);

    Map<String, Object> prefs = prefs(options);
    assertThat(prefs.get("network.proxy.no_proxies_on")).isEqualTo("");
    assertThat(prefs.get("network.proxy.allow_hijacking_localhost")).isEqualTo(true);
    assertThat(options.asMap().get("firefox_profile")).isNull();
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

    FirefoxOptions options = driverFactory.createCapabilities(config, browser, proxy, browserDownloadsFolder);

    Map<String, Object> prefs = prefs(options);
    assertThat(prefs.get("browser.download.dir")).isEqualTo(new File(DOWNLOADS_FOLDER).getAbsolutePath());
    assertThat((String) prefs.get("browser.helperApps.neverAsk.saveToDisk")).contains("application/pdf");
    assertThat(prefs.get("pdfjs.disabled")).isEqualTo(true);
    assertThat(prefs.get("browser.download.folderList")).isEqualTo(2);
    assertThat(options.getProfile()).isNull();
  }

  @Test
  void shouldNotSetupDownloadFolder_forRemoteWebdriver() {
    config.headless(true);
    config.remote("https://some.remote.blah:1234/wd");

    FirefoxOptions options = driverFactory.createCapabilities(config, browser, proxy, browserDownloadsFolder);

    Map<String, Object> prefs = prefs(options);
    assertThat(prefs.get("browser.download.dir")).isNull();
    assertThat((String) prefs.get("browser.helperApps.neverAsk.saveToDisk")).contains("application/pdf");
    assertThat(prefs.get("pdfjs.disabled")).isEqualTo(true);
    assertThat(prefs.get("browser.download.folderList")).isEqualTo(2);
    assertThat(options.getProfile()).isNull();
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> prefs(FirefoxOptions options) {
    Map<String, Object> firefoxOptions = (Map<String, Object>) options.asMap().get("moz:firefoxOptions");
    return (Map<String, Object>) firefoxOptions.get("prefs");
  }
}
