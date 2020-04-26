package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.SelenideConfig;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchArgs;
import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchPrefs;
import static org.mockito.Mockito.mock;

class ChromeDriverFactoryTest implements WithAssertions {
  private static final String CHROME_OPTIONS_PREFS = "chromeoptions.prefs";
  private static final String CHROME_OPTIONS_ARGS = "chromeoptions.args";

  private final Proxy proxy = mock(Proxy.class);
  private final SelenideConfig config = new SelenideConfig().downloadsFolder("/blah/downloads");

  @BeforeEach
  @AfterEach
  void tearDown() {
    System.clearProperty(CHROME_OPTIONS_ARGS);
    System.clearProperty(CHROME_OPTIONS_PREFS);
  }

  @Test
  void defaultChromeOptions() {
    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(config, proxy);
    Map<String, Object> prefsMap = getBrowserLaunchPrefs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(prefsMap).hasSize(2);
    assertThat(prefsMap).containsEntry("credentials_enable_service", false);
    assertThat(prefsMap).containsEntry("download.default_directory",
      new File("/blah/downloads").getAbsolutePath());
  }

  @Test
  void transferChromeOptionArgumentsFromSystemPropsToDriver() {
    System.setProperty(CHROME_OPTIONS_ARGS, "abdd,--abcd,\"snc,snc\",xcvcd=123,\"abc emd\"");

    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(config, proxy);
    List<String> optionArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(optionArguments)
      .contains("abdd", "--abcd", "xcvcd=123", "snc,snc", "abc emd");
  }

  @Test
  void transferChromeOptionPreferencesFromSystemPropsToDriver() {
    System.setProperty(CHROME_OPTIONS_PREFS, "key1=stringval,key2=1,key3=false,key4=true," +
      "\"key5=abc,555\",key6=\"555 abc\"");

    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(config, proxy);
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

    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(config, proxy);
    Map<String, Object> prefsMap = getBrowserLaunchPrefs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(prefsMap).containsEntry("key1", 1);
    assertThat(prefsMap).doesNotContainKey("key2");
  }

  @Test
  void transferChromeOptionPreferencesFromSystemPropsToDriverTwoAssignmentStatement() {
    System.setProperty(CHROME_OPTIONS_PREFS, "key1=1,key2=1=false");

    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(config, proxy);
    Map<String, Object> prefsMap = getBrowserLaunchPrefs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(prefsMap).containsEntry("key1", 1);
    assertThat(prefsMap).doesNotContainKeys("key2", "key2=", "key2=1", "key2=1=", "key2=1=false");
  }

  @Test
  void browserBinaryCanBeSet() {
    config.browserBinary("c:/browser.exe");

    Capabilities caps = new ChromeDriverFactory().createChromeOptions(config, proxy);
    Map options = (Map) caps.asMap().get(ChromeOptions.CAPABILITY);

    assertThat(options.get("binary")).isEqualTo("c:/browser.exe");
  }

  @Test
  void headlessCanBeSet() {
    config.headless(true);

    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(config, proxy);
    List<String> optionArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(optionArguments).contains("--headless");
  }
}
