package com.codeborne.selenide;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;

import java.util.Map;

import static com.codeborne.selenide.Browsers.CHROME;
import static com.codeborne.selenide.Browsers.EDGE;
import static com.codeborne.selenide.Browsers.FIREFOX;
import static com.codeborne.selenide.Browsers.IE;
import static com.codeborne.selenide.Browsers.INTERNET_EXPLORER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class BrowserTest {
  @Test
  void browserNameIsCaseInsensitive() {
    assertThat(new Browser(CHROME, false).isChrome()).isTrue();
    assertThat(new Browser("chrome", false).isChrome()).isTrue();
    assertThat(new Browser("cHromE", false).isChrome()).isTrue();
    assertThat(new Browser("firefox", false).isChrome()).isFalse();
  }

  @Test
  void chromiumBrowserTest() {
    assertThat(new Browser(CHROME, false).isChromium()).isTrue();
    assertThat(new Browser(EDGE, false).isChromium()).isTrue();
    assertThat(new Browser(FIREFOX, false).isChromium()).isFalse();
    assertThat(new Browser(IE, false).isChromium()).isFalse();
    assertThat(new Browser(INTERNET_EXPLORER, false).isChromium()).isFalse();
  }

  @Test
  void edgeNameFromCapabilitiesTest() {
    assertThat(new Browser("MicrosoftEdge", false).isChromium()).isTrue();
  }

  @Test
  void detectsChromiumFromWebDriverCapabilities() {
    assertThat(Browser.isChromium(driverWithBrowserName(CHROME))).isTrue();
    assertThat(Browser.isChromium(driverWithBrowserName(EDGE))).isTrue();
    assertThat(Browser.isChromium(driverWithBrowserName(FIREFOX))).isFalse();
  }

  @Test
  void isNotChromium_whenDriverDoesNotExposeCapabilities() {
    WebDriver driver = mock();
    assertThat(Browser.isChromium(driver)).isFalse();
  }

  private interface CapableWebDriver extends WebDriver, HasCapabilities {
  }

  private static WebDriver driverWithBrowserName(String browserName) {
    CapableWebDriver driver = mock();
    when(driver.getCapabilities()).thenReturn(new MutableCapabilities(Map.of("browserName", browserName)));
    return driver;
  }

  @Test
  void mostBrowsersSupportInsecureCerts() {
    assertThat(new Browser(CHROME, false).supportsInsecureCerts()).isTrue();
    assertThat(new Browser(FIREFOX, false).supportsInsecureCerts()).isTrue();
    assertThat(new Browser(EDGE, false).supportsInsecureCerts()).isTrue();
  }

  @Test
  void microsoftBrowsersDoNotSupportInsecureCerts() {
    assertThat(new Browser(IE, false).supportsInsecureCerts()).isFalse();
    assertThat(new Browser(INTERNET_EXPLORER, false).supportsInsecureCerts()).isFalse();
  }

  @Test
  void stringRepresentationInLogs() {
    assertThat(new Browser(CHROME, false)).hasToString("Browser{chrome}");
    assertThat(new Browser(IE, false)).hasToString("Browser{ie}");
    assertThat(new Browser(IE, true)).hasToString("Browser{ie:headless}");
    assertThat(new Browser(INTERNET_EXPLORER, true)).hasToString("Browser{internet explorer:headless}");
  }
}
