package com.codeborne.selenide;

import org.junit.jupiter.api.Test;

import static org.openqa.selenium.remote.Browser.CHROME;
import static com.codeborne.selenide.Browsers.EDGE;
import static org.openqa.selenium.remote.Browser.FIREFOX;
import static org.openqa.selenium.remote.Browser.HTMLUNIT;
import static org.openqa.selenium.remote.Browser.IE;
import static org.openqa.selenium.remote.Browser.OPERA;
import static com.codeborne.selenide.Browsers.INTERNET_EXPLORER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.remote.Browser.SAFARI;
import static org.openqa.selenium.remote.Browser.SAFARI_TECH_PREVIEW;

final class BrowserTest {
  @Test
  void browserNameIsCaseInsensitive() {
    assertThat(new Browser(CHROME.browserName(), false).isChrome()).isTrue();
    assertThat(new Browser("chrome", false).isChrome()).isTrue();
    assertThat(new Browser("cHromE", false).isChrome()).isTrue();
    assertThat(new Browser("firefox", false).isChrome()).isFalse();
  }

  @Test
  void mostBrowsersSupportInsecureCerts() {
    assertThat(new Browser(CHROME.browserName(), false).supportsInsecureCerts()).isTrue();
    assertThat(new Browser(FIREFOX.browserName(), false).supportsInsecureCerts()).isTrue();
    assertThat(new Browser(OPERA.browserName(), false).supportsInsecureCerts()).isTrue();
    assertThat(new Browser(EDGE, false).supportsInsecureCerts()).isTrue();
  }

  @Test
  void microsoftBrowsersDoNotSupportInsecureCerts() {
    assertThat(new Browser(IE.browserName(), false).supportsInsecureCerts()).isFalse();
    assertThat(new Browser(INTERNET_EXPLORER, false).supportsInsecureCerts()).isFalse();
  }

  @Test
  void someBrowsersAreChromium() {
    assertThat(new Browser(CHROME.browserName(), false).isChromium()).isTrue();
    assertThat(new Browser(org.openqa.selenium.remote.Browser.EDGE.browserName(), false).isChromium()).isTrue();
    assertThat(new Browser(OPERA.browserName(), false).isChromium()).isTrue();
  }

  @Test
  void someBrowsersAreNotChromium() {
    assertThat(new Browser(SAFARI.browserName(), false).isChromium()).isFalse();
    assertThat(new Browser(FIREFOX.browserName(), false).isChromium()).isFalse();
    assertThat(new Browser(HTMLUNIT.browserName(), false).isChromium()).isFalse();
    assertThat(new Browser(IE.browserName(), false).isChromium()).isFalse();
    assertThat(new Browser(INTERNET_EXPLORER, false).supportsInsecureCerts()).isFalse();
    assertThat(new Browser(SAFARI_TECH_PREVIEW.browserName(), false).isChromium()).isFalse();
    assertThat(new Browser(EDGE, false).isChromium()).isFalse();
    assertThat(new Browser("foo bar", false).isChromium()).isFalse();
  }
}
