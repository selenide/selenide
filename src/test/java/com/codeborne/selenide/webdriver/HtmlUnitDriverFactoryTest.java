package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.htmlunit.options.HtmlUnitDriverOptions;

import java.io.File;

import static com.codeborne.selenide.Browsers.HTMLUNIT;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.remote.CapabilityType.ACCEPT_INSECURE_CERTS;
import static org.openqa.selenium.remote.CapabilityType.PROXY;

final class HtmlUnitDriverFactoryTest {
  private final SelenideConfig config = new SelenideConfig().browser(HTMLUNIT).downloadsFolder("build/should-not-be-used");
  private final Browser browser = new Browser(config.browser(), config.headless());
  private final HtmlUnitDriverFactory factory = new HtmlUnitDriverFactory();
  private final File downloadsFolder = new File("/tmp/downloads-folder-12345");

  @Test
  void enablesJavaScript() {
    HtmlUnitDriverOptions options = factory.createCapabilities(config, browser, null, downloadsFolder);
    assertThat(options.isJavaScriptEnabled()).isTrue();
  }

  @Test
  void acceptsInsecureCerts() {
    HtmlUnitDriverOptions options = factory.createCapabilities(config, browser, null, downloadsFolder);
    assertThat(options.getCapability(ACCEPT_INSECURE_CERTS)).isEqualTo(TRUE);
  }

  @Test
  void browserNameIsHtmlUnit() {
    HtmlUnitDriverOptions options = factory.createCapabilities(config, browser, null, downloadsFolder);
    assertThat(options.getBrowserName()).isEqualTo(HTMLUNIT);
  }

  @Test
  void setsProxy() {
    Proxy proxy = new Proxy().setHttpProxy("127.0.0.1:8888");
    HtmlUnitDriverOptions options = factory.createCapabilities(config, browser, proxy, downloadsFolder);
    assertThat(options.getCapability(PROXY)).isEqualTo(proxy);
  }

  @Test
  void canEmulateGivenBrowserVersion() {
    config.browserVersion("firefox");
    HtmlUnitDriverOptions options = factory.createCapabilities(config, browser, null, downloadsFolder);
    assertThat(options.getWebClientVersion().isFirefox()).isTrue();
  }
}
