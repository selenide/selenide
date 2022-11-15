package com.codeborne.selenide;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Browsers.CHROME;
import static com.codeborne.selenide.Browsers.EDGE;
import static com.codeborne.selenide.Browsers.FIREFOX;
import static com.codeborne.selenide.Browsers.IE;
import static com.codeborne.selenide.Browsers.INTERNET_EXPLORER;
import static org.assertj.core.api.Assertions.assertThat;

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
}
