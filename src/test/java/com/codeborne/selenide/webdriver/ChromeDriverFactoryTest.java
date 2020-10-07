package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.SelenideConfig;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchArgs;
import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchPrefs;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;

final class ChromeDriverFactoryTest implements WithAssertions {
  private static final String CHROME_OPTIONS_PREFS = "chromeoptions.prefs";
  private static final String CHROME_OPTIONS_ARGS = "chromeoptions.args";
  private static final String DOWNLOADS_FOLDER = Paths.get("blah", "downloads").toString();

  private final Proxy proxy = mock(Proxy.class);
  private final SelenideConfig config = new SelenideConfig().downloadsFolder("build/should-not-be-used");
  private final File browserDownloadsFolder = new File(DOWNLOADS_FOLDER);
  private final Browser browser = new Browser(config.browser(), config.headless());
  private final ChromeDriverFactory factory = new ChromeDriverFactory();

  @BeforeEach
  @AfterEach
  void tearDown() {
    System.clearProperty(CHROME_OPTIONS_ARGS);
    System.clearProperty(CHROME_OPTIONS_PREFS);
  }

  @Test
  void defaultChromeOptions() {
    Capabilities chromeOptions = factory.createCapabilities(config, browser, proxy, browserDownloadsFolder);
    Map<String, Object> prefsMap = getBrowserLaunchPrefs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(prefsMap).hasSize(3);
    assertThat(prefsMap).containsEntry("credentials_enable_service", false);
    assertThat(prefsMap).containsEntry("plugins.always_open_pdf_externally", true);
    assertThat(prefsMap).containsEntry("download.default_directory",
      new File(DOWNLOADS_FOLDER).getAbsolutePath());
  }

  @Test
  void shouldNotSetupDownloadFolder_forRemoteWebdriver() {
    config.remote("https://some.remote:1234/wd");

    Capabilities chromeOptions = factory.createCapabilities(config, browser, proxy, browserDownloadsFolder);

    Map<String, Object> prefsMap = getBrowserLaunchPrefs(ChromeOptions.CAPABILITY, chromeOptions);
    assertThat(prefsMap).containsEntry("credentials_enable_service", false);
    assertThat(prefsMap).doesNotContainKey("download.default_directory");
  }

  @Test
  void transferChromeOptionArgumentsFromSystemPropsToDriver() {
    System.setProperty(CHROME_OPTIONS_ARGS, "abdd,--abcd,\"snc,snc\",xcvcd=123,\"abc emd\"");

    Capabilities chromeOptions = factory.createCapabilities(config, browser, proxy, browserDownloadsFolder);
    List<String> optionArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(optionArguments)
      .contains("abdd", "--abcd", "xcvcd=123", "snc,snc", "abc emd");
  }

  @Test
  void transferChromeOptionPreferencesFromSystemPropsToDriver() {
    System.setProperty(CHROME_OPTIONS_PREFS, "key1=stringval,key2=1,key3=false,key4=true," +
      "\"key5=abc,555\",key6=\"555 abc\"");

    Capabilities chromeOptions = factory.createCapabilities(config, browser, proxy, browserDownloadsFolder);
    Map<String, Object> prefsMap = getBrowserLaunchPrefs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(prefsMap)
      .containsEntry("key1", "stringval")
      .containsEntry("key2", 1)
      .containsEntry("key3", false)
      .containsEntry("key4", true)
      .containsEntry("key5", "abc,555")
      .containsEntry("key6", "555 abc");
  }

  @Test
  void transferChromeOptionPreferencesFromSystemPropsToDriverNoAssignmentStatement() {
    System.setProperty(CHROME_OPTIONS_PREFS, "key1=1,key2");

    Capabilities chromeOptions = factory.createCapabilities(config, browser, proxy, browserDownloadsFolder);
    Map<String, Object> prefsMap = getBrowserLaunchPrefs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(prefsMap).containsEntry("key1", 1);
    assertThat(prefsMap).doesNotContainKey("key2");
  }

  @Test
  void transferChromeOptionPreferencesFromSystemPropsToDriverTwoAssignmentStatement() {
    System.setProperty(CHROME_OPTIONS_PREFS, "key1=1,key2=1=false");

    Capabilities chromeOptions = factory.createCapabilities(config, browser, proxy, browserDownloadsFolder);
    Map<String, Object> prefsMap = getBrowserLaunchPrefs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(prefsMap).containsEntry("key1", 1);
    assertThat(prefsMap).doesNotContainKeys("key2", "key2=", "key2=1", "key2=1=", "key2=1=false");
  }

  @Test
  void browserBinaryCanBeSet() {
    config.browserBinary("c:/browser.exe");

    Capabilities caps = factory.createCapabilities(config, browser, proxy, browserDownloadsFolder);

    Map<String, Object> options = getChromeOptions(caps);
    assertThat(options.get("binary")).isEqualTo("c:/browser.exe");
  }

  @Test
  void headlessCanBeSet() {
    config.headless(true);

    Capabilities chromeOptions = factory.createCapabilities(config, browser, proxy, browserDownloadsFolder);
    List<String> optionArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(optionArguments).contains("--headless");
  }

  @Test
  void disablesUsingDevSharedMemory() {
    Capabilities chromeOptions = factory.createCapabilities(config, browser, proxy, browserDownloadsFolder);
    List<String> optionArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(optionArguments).contains("--disable-dev-shm-usage");
  }

  @Test
  void disablesSandbox() {
    Capabilities chromeOptions = factory.createCapabilities(config, browser, proxy, browserDownloadsFolder);
    List<String> optionArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(optionArguments).contains("--no-sandbox");
  }

  @Test
  void parseCSV() {
    assertThat(factory.parseCSV("123")).isEqualTo(singletonList("123"));
    assertThat(factory.parseCSV("foo bar")).isEqualTo(singletonList("foo bar"));
    assertThat(factory.parseCSV("bar,foo")).isEqualTo(asList("bar", "foo"));
  }

  @Test
  void parseCSV_empty() {
    assertThat(factory.parseCSV("")).isEmpty();
  }

  @Test
  void parseCSV_handles_quotes() {
    assertThat(factory.parseCSV("abdd,--abcd,\"snc,snc\",xcvcd=123,\"abc emd\""))
      .isEqualTo(asList("abdd", "--abcd", "\"snc,snc\"", "xcvcd=123", "\"abc emd\""));
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getChromeOptions(Capabilities caps) {
    return (Map<String, Object>) caps.asMap().get(ChromeOptions.CAPABILITY);
  }
}
