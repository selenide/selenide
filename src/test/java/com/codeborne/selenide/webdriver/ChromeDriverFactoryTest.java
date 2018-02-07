package com.codeborne.selenide.webdriver;

import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchArgs;
import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchPrefs;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeDriverFactoryTest {

  private Proxy proxy = mock(Proxy.class);

  @After
  public void tearDown() {
    System.clearProperty("chromeoptions.args");
    System.clearProperty("chromeoptions.prefs");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void transferChromeOptionArgumentsFromSystemPropsToDriver() {
    System.setProperty("chromeoptions.args", "abdd,--abcd,xcvcd=123");
    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(proxy);
    List<String> optionArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, chromeOptions);

    assertThat(optionArguments, hasItems("abdd"));
    assertThat(optionArguments, hasItems("--abcd"));
    assertThat(optionArguments, hasItems("xcvcd=123"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void transferChromeOptionPreferencesFromSystemPropsToDriver() {
    System.setProperty("chromeoptions.prefs", "key1=stringval,key2=1,key3=false,key4=true");
    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(proxy);
    Map<String, Object> optionArguments = getBrowserLaunchPrefs(ChromeOptions.CAPABILITY, chromeOptions);

    assertEquals("string pref value", optionArguments.get("key1"), "stringval");
    assertEquals("int pref value", optionArguments.get("key2"), 1);
    assertEquals("boolean pref value", optionArguments.get("key3"), false);
    assertEquals("boolean pref value", optionArguments.get("key4"), true);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void transferChromeOptionPreferencesFromSystemPropsToDriverNoAssignmentStatement() {
    System.setProperty("chromeoptions.prefs", "key1=1,key2");
    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(proxy);
    Map<String, Object> optionArguments = getBrowserLaunchPrefs(ChromeOptions.CAPABILITY, chromeOptions);

    assertEquals("parsed key value pairs amount", optionArguments.size(), 1);
    assertTrue("parsed wrong key=value pair", optionArguments.containsKey("key1"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void transferChromeOptionPreferencesFromSystemPropsToDriverTwoAssignmentStatement() {
    System.setProperty("chromeoptions.prefs", "key1=1,key2=1=false");
    ChromeOptions chromeOptions = new ChromeDriverFactory().createChromeOptions(proxy);
    Map<String, Object> optionArguments = getBrowserLaunchPrefs(ChromeOptions.CAPABILITY, chromeOptions);

    assertEquals("parsed key value pairs amount", optionArguments.size(), 1);
    assertTrue("parsed wrong key=value pair", optionArguments.containsKey("key1"));
  }

}
