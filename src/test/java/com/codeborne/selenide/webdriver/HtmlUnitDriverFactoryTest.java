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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.openqa.selenium.remote.CapabilityType.ACCEPT_INSECURE_CERTS;
import static org.openqa.selenium.remote.CapabilityType.PROXY;

final class HtmlUnitDriverFactoryTest {
  private final SelenideConfig config = new SelenideConfig().browser(HTMLUNIT).downloadsFolder("build/should-not-be-used");
  private final Browser browser = new Browser(config.browser(), config.headless());
  private final HtmlUnitDriverFactory factory = new HtmlUnitDriverFactory();
  private final File downloadsFolder = new File("/tmp/downloads-folder-12345");

  @Test
  void enablesJavaScript() {
    HtmlUnitDriverOptions options = options(null);
    assertThat(options.isJavaScriptEnabled()).isTrue();
  }

  @Test
  void acceptsInsecureCerts() {
    HtmlUnitDriverOptions options = options(null);
    assertThat(options.getCapability(ACCEPT_INSECURE_CERTS)).isEqualTo(TRUE);
  }

  @Test
  void browserNameIsHtmlUnit() {
    HtmlUnitDriverOptions options = options(null);
    assertThat(options.getBrowserName()).isEqualTo(HTMLUNIT);
  }

  @Test
  void setsProxy() {
    Proxy proxy = new Proxy().setHttpProxy("127.0.0.1:8888");
    HtmlUnitDriverOptions options = options(proxy);
    assertThat(options.getCapability(PROXY)).isEqualTo(proxy);
  }

  @Test
  void canEmulateGivenBrowserVersion() {
    config.browserVersion("firefox");
    HtmlUnitDriverOptions options = options(null);
    assertThat(options.getWebClientVersion().isFirefox()).isTrue();
  }

  @Test
  void reportsMissingHtmlUnitDependency() {
    HtmlUnitDriverFactory factoryWithoutHtmlUnit = new HtmlUnitDriverFactory("org.example.MissingHtmlUnitDriver");
    assertThatThrownBy(() -> factoryWithoutHtmlUnit.createCapabilities(config, browser, null, downloadsFolder))
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("HtmlUnit driver not found. Add dependency \"org.seleniumhq.selenium:htmlunit3-driver\" to your project.")
      .hasCauseInstanceOf(ClassNotFoundException.class);
  }

  private HtmlUnitDriverOptions options(Proxy proxy) {
    return (HtmlUnitDriverOptions) factory.createCapabilities(config, browser, proxy, downloadsFolder);
  }
}
