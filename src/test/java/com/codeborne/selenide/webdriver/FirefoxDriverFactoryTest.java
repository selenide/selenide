package com.codeborne.selenide.webdriver;

import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchArgs;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import com.codeborne.selenide.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.*;

import java.util.*;

public class FirefoxDriverFactoryTest {

  private Proxy proxy = mock(Proxy.class);
  private FirefoxDriverFactory driverFactory;

  @Before
  public void setUp() {
    driverFactory = new FirefoxDriverFactory();
  }

  @After
  public void tearDown() {
    System.clearProperty("capabilities.some.cap");
    System.clearProperty("firefoxprofile.some.cap");
    Configuration.browserBinary = "";
    Configuration.headless = false;
  }

  @Test
  public void transfersStringCapabilitiesFromSystemPropsToDriver() {
    System.setProperty("capabilities.some.cap", "abcd");
    assertThat(driverFactory.createCommonCapabilities(proxy).getCapability("some.cap"), is("abcd"));
  }

  @Test
  public void transfersBooleanCapabilitiesFromSystemPropsToDriver() {
    System.setProperty("capabilities.some.cap", "true");
    assertThat(driverFactory.createCommonCapabilities(proxy).getCapability("some.cap"), is(true));
  }

  @Test
  public void transfersIntegerCapabilitiesFromSystemPropsToDriver() {
    System.setProperty("capabilities.some.cap", "25");
    assertThat(driverFactory.createCommonCapabilities(proxy).getCapability("some.cap"), is(25));
  }

  @Test
  public void transferIntegerFirefoxProfilePreferencesFromSystemPropsToDriver() {
    System.setProperty("firefoxprofile.some.cap", "25");
    FirefoxProfile profile = driverFactory.createFirefoxOptions(proxy).getProfile();
    assertThat(profile.getIntegerPreference("some.cap", 0), is(25));

  }

  @Test
  public void transferBooleanFirefoxProfilePreferencesFromSystemPropsToDriver() {
    System.setProperty("firefoxprofile.some.cap", "false");
    FirefoxProfile profile = driverFactory.createFirefoxOptions(proxy).getProfile();
    assertThat(profile.getBooleanPreference("some.cap", true), is(false));
  }

  @Test
  public void transferStringFirefoxProfilePreferencesFromSystemPropsToDriver() {
    System.setProperty("firefoxprofile.some.cap", "abdd");
    FirefoxProfile profile = driverFactory.createFirefoxOptions(proxy).getProfile();
    assertThat(profile.getStringPreference("some.cap", "sjlj"), is("abdd"));
  }

  @Test
  public void browserBinaryCanBeSet() {
    Configuration.browserBinary = "c:/browser.exe";
    Capabilities caps = driverFactory.createFirefoxOptions(proxy);
    Map options = (Map) caps.asMap().get(FirefoxOptions.FIREFOX_OPTIONS);
    assertThat(options.get("binary"), is("c:/browser.exe"));
  }

  @Test
  public void headlessCanBeSet() {
    Configuration.headless = true;
    FirefoxOptions options = driverFactory.createFirefoxOptions(proxy);
    List<String> optionArguments = getBrowserLaunchArgs(FirefoxOptions.FIREFOX_OPTIONS, options);
    assertThat(optionArguments, hasItems("-headless"));

  }
}
