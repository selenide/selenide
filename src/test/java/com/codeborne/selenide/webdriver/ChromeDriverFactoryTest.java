package com.codeborne.selenide.webdriver;

import java.util.List;
import java.util.Map;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.UnitTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;

import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchArgs;
import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchPrefs;
import static org.mockito.Mockito.mock;

@SuppressWarnings("unchecked")
class ChromeDriverFactoryTest extends UnitTest {
  private final String CHROME_OPTIONS_PREFS = "chromeoptions.prefs";
  private final String CHROME_OPTIONS_ARGS = "chromeoptions.args";
  private Proxy proxy = mock(Proxy.class);

  @AfterEach
  void tearDown() {
    System.clearProperty(CHROME_OPTIONS_ARGS);
    System.clearProperty(CHROME_OPTIONS_PREFS);
    Configuration.browserBinary = "";
    Configuration.headless = false;
  }

  @Test
  void transferChromeOptionArgumentsFromSystemPropsToDriver() {
    System.setProperty(CHROME_OPTIONS_ARGS, "abdd,--abcd,xcvcd=123");
    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(proxy);
    List<String> optionArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(optionArguments)
      .contains("abdd", "--abcd", "xcvcd=123");
  }

  @Test
  void transferChromeOptionPreferencesFromSystemPropsToDriver() {
    System.setProperty(CHROME_OPTIONS_PREFS, "key1=stringval,key2=1,key3=false,key4=true");
    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(proxy);
    Map<String, Object> prefsMap = getBrowserLaunchPrefs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(prefsMap)
      .containsEntry("key1", "stringval")
      .containsEntry("key2", 1)
      .containsEntry("key3", false)
      .containsEntry("key4", true);
  }

  @Test
  void transferChromeOptionPreferencesFromSystemPropsToDriverNoAssignmentStatement() {
    System.setProperty(CHROME_OPTIONS_PREFS, "key1=1,key2");
    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(proxy);
    Map<String, Object> prefsMap = getBrowserLaunchPrefs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(prefsMap)
      .hasSize(1);
    assertThat(prefsMap)
      .containsEntry("key1", 1);
  }

  @Test
  void transferChromeOptionPreferencesFromSystemPropsToDriverTwoAssignmentStatement() {
    System.setProperty(CHROME_OPTIONS_PREFS, "key1=1,key2=1=false");
    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(proxy);
    Map<String, Object> prefsMap = getBrowserLaunchPrefs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(prefsMap)
      .hasSize(1);
    assertThat(prefsMap)
      .containsEntry("key1", 1);
  }

  @Test
  void transferChromeOptionsAndPrefs() {
    System.setProperty(CHROME_OPTIONS_ARGS, "abdd,--abcd,xcvcd=123");
    System.setProperty(CHROME_OPTIONS_PREFS, "key1=stringval,key2=1,key3=false,key4=true");

    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(proxy);
    List<String> optionArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, chromeOptions);
    Map<String, Object> prefsMap = getBrowserLaunchPrefs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(optionArguments)
      .contains("abdd", "--abcd", "xcvcd=123");

    assertThat(prefsMap)
      .containsEntry("key1", "stringval")
      .containsEntry("key2", 1)
      .containsEntry("key3", false)
      .containsEntry("key4", true);
  }

  @Test
  void browserBinaryCanBeSet() {
    Configuration.browserBinary = "c:/browser.exe";
    Capabilities caps = new ChromeDriverFactory().createChromeOptions(proxy);
    Map options = (Map) caps.asMap().get(ChromeOptions.CAPABILITY);

    assertThat(options.get("binary"))
      .isEqualTo("c:/browser.exe");
  }

  @Test
  void headlessCanBeSet() {
    Configuration.headless = true;
    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(proxy);
    List<String> optionArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(optionArguments)
      .contains("--headless");
  }
}
