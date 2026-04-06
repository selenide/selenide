package com.codeborne.selenide.mcp;

import com.codeborne.selenide.SelenideConfig;
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
