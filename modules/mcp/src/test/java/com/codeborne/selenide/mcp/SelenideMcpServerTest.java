package com.codeborne.selenide.mcp;

import com.codeborne.selenide.AssertionMode;
import com.codeborne.selenide.FileDownloadMode;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelectorMode;
import com.codeborne.selenide.TextCheck;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SelenideMcpServerTest {

  @Test
  void parseConfigDefaults() {
    SelenideConfig config = SelenideMcpServer.parseConfig(new String[]{});
    assertThat(config.browser()).isNotBlank();
    assertThat(config.headless()).isFalse();
  }

  @Test
  void parseConfigBrowser() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--browser=firefox"});
    assertThat(config.browser()).isEqualTo("firefox");
  }

  @Test
  void parseConfigHeadless() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--headless"});
    assertThat(config.headless()).isTrue();
  }

  @Test
  void parseConfigBaseUrl() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--base-url=https://example.com"});
    assertThat(config.baseUrl()).isEqualTo("https://example.com");
  }

  @Test
  void parseConfigTimeout() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--timeout=10000"});
    assertThat(config.timeout()).isEqualTo(10000);
  }

  @Test
  void parseConfigInvalidTimeoutThrows() {
    assertThatThrownBy(() ->
      SelenideMcpServer.parseConfig(new String[]{"--timeout=abc"})
    ).isInstanceOf(NumberFormatException.class);
  }

  @Test
  void parseConfigMultipleArgs() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--browser=chrome", "--headless", "--timeout=5000"});
    assertThat(config.browser()).isEqualTo("chrome");
    assertThat(config.headless()).isTrue();
    assertThat(config.timeout()).isEqualTo(5000);
  }

  @Test
  void parseConfigBrowserVersion() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--browser-version=120"});
    assertThat(config.browserVersion()).isEqualTo("120");
  }

  @Test
  void parseConfigBrowserSize() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--browser-size=1920x1080"});
    assertThat(config.browserSize()).isEqualTo("1920x1080");
  }

  @Test
  void parseConfigBrowserBinary() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--browser-binary=/usr/bin/chromium"});
    assertThat(config.browserBinary()).isEqualTo("/usr/bin/chromium");
  }

  @Test
  void parseConfigBrowserPosition() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--browser-position=100x200"});
    assertThat(config.browserPosition()).isEqualTo("100x200");
  }

  @Test
  void parseConfigRemote() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--remote=http://selenium-hub:4444/wd/hub"});
    assertThat(config.remote()).isEqualTo("http://selenium-hub:4444/wd/hub");
  }

  @Test
  void parseConfigPollingInterval() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--polling-interval=500"});
    assertThat(config.pollingInterval()).isEqualTo(500);
  }

  @Test
  void parseConfigRemoteReadTimeout() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--remote-read-timeout=120000"});
    assertThat(config.remoteReadTimeout()).isEqualTo(120000);
  }

  @Test
  void parseConfigRemoteConnectionTimeout() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--remote-connection-timeout=15000"});
    assertThat(config.remoteConnectionTimeout()).isEqualTo(15000);
  }

  @Test
  void parseConfigPageLoadStrategy() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--page-load-strategy=eager"});
    assertThat(config.pageLoadStrategy()).isEqualTo("eager");
  }

  @Test
  void parseConfigPageLoadTimeout() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--page-load-timeout=60000"});
    assertThat(config.pageLoadTimeout()).isEqualTo(60000);
  }

  @Test
  void parseConfigDownloadsFolder() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--downloads-folder=/tmp/downloads"});
    assertThat(config.downloadsFolder()).isEqualTo("/tmp/downloads");
  }

  @Test
  void parseConfigProxyEnabled() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--proxy-enabled"});
    assertThat(config.proxyEnabled()).isTrue();
  }

  @Test
  void parseConfigProxyHost() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--proxy-host=proxy.internal"});
    assertThat(config.proxyHost()).isEqualTo("proxy.internal");
  }

  @Test
  void parseConfigProxyPort() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--proxy-port=8080"});
    assertThat(config.proxyPort()).isEqualTo(8080);
  }

  @Test
  void parseConfigFastSetValue() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--fast-set-value"});
    assertThat(config.fastSetValue()).isTrue();
  }

  @Test
  void parseConfigClickViaJs() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--click-via-js"});
    assertThat(config.clickViaJs()).isTrue();
  }

  @Test
  void parseConfigWebdriverLogs() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--webdriver-logs"});
    assertThat(config.webdriverLogsEnabled()).isTrue();
  }

  @Test
  void parseConfigNoScreenshots() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--no-screenshots"});
    assertThat(config.screenshots()).isFalse();
  }

  @Test
  void parseConfigNoSavePageSource() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--no-save-page-source"});
    assertThat(config.savePageSource()).isFalse();
  }

  @Test
  void parseConfigNoReopenBrowserOnFail() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--no-reopen-browser-on-fail"});
    assertThat(config.reopenBrowserOnFail()).isFalse();
  }

  @Test
  void parseConfigReportsFolder() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--reports-folder=/tmp/reports"});
    assertThat(config.reportsFolder()).isEqualTo("/tmp/reports");
  }

  @Test
  void parseConfigTextCheck() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--text-check=FULL_TEXT"});
    assertThat(config.textCheck()).isEqualTo(TextCheck.FULL_TEXT);
  }

  @Test
  void parseConfigSelectorMode() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--selector-mode=Sizzle"});
    assertThat(config.selectorMode()).isEqualTo(SelectorMode.Sizzle);
  }

  @Test
  void parseConfigAssertionMode() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--assertion-mode=SOFT"});
    assertThat(config.assertionMode()).isEqualTo(AssertionMode.SOFT);
  }

  @Test
  void parseConfigFileDownload() {
    SelenideConfig config = SelenideMcpServer.parseConfig(
      new String[]{"--file-download=PROXY"});
    assertThat(config.fileDownload()).isEqualTo(FileDownloadMode.PROXY);
  }

  @Test
  void hasCapabilityFindsMatch() {
    assertThat(SelenideMcpServer.hasCapability(
      new String[]{"--caps=codegen,inspect"}, "codegen")).isTrue();
  }

  @Test
  void hasCapabilityNoMatch() {
    assertThat(SelenideMcpServer.hasCapability(
      new String[]{"--caps=inspect"}, "codegen")).isFalse();
  }

  @Test
  void hasCapabilityDoesNotSubstringMatch() {
    assertThat(SelenideMcpServer.hasCapability(
      new String[]{"--caps=nocodegen"}, "codegen")).isFalse();
  }

  @Test
  void hasCapabilityMultipleCaps() {
    assertThat(SelenideMcpServer.hasCapability(
      new String[]{"--caps=inspect,codegen"}, "codegen")).isTrue();
  }

  @Test
  void hasCapabilityNoFlag() {
    assertThat(SelenideMcpServer.hasCapability(
      new String[]{"--browser=chrome"}, "codegen")).isFalse();
  }

  @Test
  void hasCapabilityEmptyArgs() {
    assertThat(SelenideMcpServer.hasCapability(
      new String[]{}, "codegen")).isFalse();
  }
}
