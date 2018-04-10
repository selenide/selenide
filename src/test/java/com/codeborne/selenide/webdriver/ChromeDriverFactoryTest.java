package com.codeborne.selenide.webdriver;

import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchArgs;
import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchPrefs;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@SuppressWarnings("unchecked")
public class ChromeDriverFactoryTest {

  private final String CHROME_OPTIONS_PREFS = "chromeoptions.prefs";
  private final String CHROME_OPTIONS_ARGS = "chromeoptions.args";
  private Proxy proxy = mock(Proxy.class);

  @After
  public void tearDown() {
    System.clearProperty("chromeoptions.args");
    System.clearProperty(CHROME_OPTIONS_ARGS);
    System.clearProperty(CHROME_OPTIONS_PREFS);
  }

  @Test
  public void transferChromeOptionArgumentsFromSystemPropsToDriver() {
    // abdd, --abcd, xcvcd=123, a="def", b="a\"d", --user-agent="A,B") should work
    System.setProperty(CHROME_OPTIONS_ARGS, "abdd,--abcd,xcvcd=123,a=\"def\",b=\"a\\\"d\",--user-agent=\"A,B\"");
    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(proxy);
    List<String> optionArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(optionArguments, hasItems("abdd"));
    assertThat(optionArguments, hasItems("--abcd"));
    assertThat(optionArguments, hasItems("xcvcd=123"));
    assertThat(optionArguments, hasItems("a=def"));
    assertThat(optionArguments, hasItems("b=a\"d"));
    assertThat(optionArguments, hasItems("--user-agent=A,B"));
  }

  @Test
  public void transferChromeOptionPreferencesFromSystemPropsToDriver() {
    System.setProperty(CHROME_OPTIONS_PREFS, "key1=stringval,key2=1,key3=false,key4=true");
    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(proxy);
    Map<String, Object> prefsMap = getBrowserLaunchPrefs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(prefsMap, hasEntry("key1", "stringval"));
    assertThat(prefsMap, hasEntry("key2", 1));
    assertThat(prefsMap, hasEntry("key3", false));
    assertThat(prefsMap, hasEntry("key4", true));
  }

  @Test
  public void transferChromeOptionPreferencesFromSystemPropsToDriverNoAssignmentStatement() {
    System.setProperty(CHROME_OPTIONS_PREFS, "key1=1,key2");
    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(proxy);
    Map<String, Object> prefsMap = getBrowserLaunchPrefs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(prefsMap.size(), is(1));
    assertThat(prefsMap, hasEntry("key1", 1));
  }

  @Test
  public void transferChromeOptionPreferencesFromSystemPropsToDriverTwoAssignmentStatement() {
    System.setProperty(CHROME_OPTIONS_PREFS, "key1=1,key2=1=false");
    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(proxy);
    Map<String, Object> prefsMap = getBrowserLaunchPrefs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(prefsMap.size(), is(1));
    assertThat(prefsMap, hasEntry("key1", 1));
  }

  @Test
  public void transferChromeOptionsAndPrefs() {
    System.setProperty(CHROME_OPTIONS_ARGS, "abdd,--abcd,xcvcd=123");
    System.setProperty(CHROME_OPTIONS_PREFS, "key1=stringval,key2=1,key3=false,key4=true");

    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(proxy);
    List<String> optionArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, chromeOptions);
    Map<String, Object> prefsMap = getBrowserLaunchPrefs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(optionArguments, hasItems("abdd"));
    assertThat(optionArguments, hasItems("--abcd"));
    assertThat(optionArguments, hasItems("xcvcd=123"));

    assertThat(prefsMap, hasEntry("key1", "stringval"));
    assertThat(prefsMap, hasEntry("key2", 1));
    assertThat(prefsMap, hasEntry("key3", false));
    assertThat(prefsMap, hasEntry("key4", true));
  }
}
