package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class WebDriverFactoryTest {
  WebDriverFactory factory = spy(new WebDriverFactory());
  WebDriver webdriver = mock(WebDriver.class, RETURNS_DEEP_STUBS);
  Proxy proxy = mock(Proxy.class);

  @Before
  public void setUp() {
    Configuration.browser = null;
    Configuration.browserSize = null;
    Configuration.startMaximized = false;
  }

  @Test
  public void doesNotChangeWindowSizeByDefault() {
    factory.adjustBrowserSize(webdriver);
    verifyNoMoreInteractions(webdriver);
  }

  @Test
  public void canConfigureBrowserWindowSize() {
    Configuration.browserSize = "1600x800";

    factory.adjustBrowserSize(webdriver);

    verify(webdriver.manage().window()).setSize(new Dimension(1600, 800));
  }

  @Test
  public void canMaximizeBrowserWindow() {
    Configuration.startMaximized = true;

    factory.adjustBrowserSize(webdriver);

    verify(webdriver.manage().window()).maximize();
  }

  @Test
  public void canMaximizeBrowserWindow_chrome() {
    Configuration.startMaximized = true;
    Configuration.browser = "chrome";
    doReturn(new Dimension(1600, 1200)).when(factory).getScreenSize();

    factory.adjustBrowserSize(webdriver);

    verify(webdriver.manage().window()).setSize(new Dimension(1600, 1200));
    verify(webdriver.manage().window()).setPosition(new Point(0, 0));
  }

  @Test
  public void transfersStringCapabilitiesFromSystemPropsToDriver() {
    System.setProperty("capabilities.some.cap", "abcd");
    assertThat(factory.createCommonCapabilities(proxy).getCapability("some.cap"), is("abcd"));
  }

  @Test
  public void transfersBooleanCapabilitiesFromSystemPropsToDriver() {
    System.setProperty("capabilities.some.cap", "true");
    assertThat(factory.createCommonCapabilities(proxy).getCapability("some.cap"), is(true));
  }

  @Test
  public void transfersIntegerCapabilitiesFromSystemPropsToDriver() {
    System.setProperty("capabilities.some.cap", "25");
    assertThat(factory.createCommonCapabilities(proxy).getCapability("some.cap"), is(25));
  }

  @Test
  public void transferIntegerFirefoxProfilePreferencesFromSystemPropsToDriver() {
    System.setProperty("firefoxprofile.some.cap", "25");
    FirefoxProfile profile = (FirefoxProfile) factory.createFirefoxCapabilities(proxy).getCapability(FirefoxDriver.PROFILE);
    assertThat(profile.getIntegerPreference("some.cap", 0), is(25));
  }

  @Test
  public void transferBooleanFirefoxProfilePreferencesFromSystemPropsToDriver() {
    System.setProperty("firefoxprofile.some.cap", "false");
    FirefoxProfile profile = (FirefoxProfile) factory.createFirefoxCapabilities(proxy).getCapability(FirefoxDriver.PROFILE);
    assertThat(profile.getBooleanPreference("some.cap", true), is(false));
  }

  @Test
  public void transferStringFirefoxProfilePreferencesFromSystemPropsToDriver() {
    System.setProperty("firefoxprofile.some.cap", "abdd");
    FirefoxProfile profile = (FirefoxProfile) factory.createFirefoxCapabilities(proxy).getCapability(FirefoxDriver.PROFILE);
    assertThat(profile.getStringPreference("some.cap", "sjlj"), is("abdd"));
  }

  @Test
  public void transferChromeOptionArgumentsFromSystemPropsToDriver() throws IOException {
    System.setProperty("chromeoptions.args", "abdd,--abcd,xcvcd=123");
    String arrayOfArguments = factory.createChromeOptions().toJson().getAsJsonObject().getAsJsonArray("args").toString();
    assertThat(arrayOfArguments, containsString("abdd"));
    assertThat(arrayOfArguments, containsString("--abcd"));
    assertThat(arrayOfArguments, containsString("xcvcd=123"));
  }

  @Test
  public void transferChromeOptionBinaryFromSystemPropsToDriver() throws IOException {
    System.setProperty("chromeoptions.binary", "/tmp/chrome");
    String binary = factory.createChromeOptions().toJson().getAsJsonObject().getAsJsonPrimitive("binary").getAsString();
    assertThat(binary, is("/tmp/chrome"));
  }

  @After
  public void tearDown() {
    System.clearProperty("capabilities.some.cap");
  }
}
