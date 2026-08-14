package com.codeborne.selenide.cli;

import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CliConfigTest {
  @Test
  void appliesBrowser() {
    assertThat(CliConfig.toConfig(new String[]{"--browser=firefox"}).browser()).isEqualTo("firefox");
  }

  @Test
  void appliesHeadless() {
    assertThat(CliConfig.toConfig(new String[]{"--headless"}).headless()).isTrue();
  }

  @Test
  void appliesBaseUrlAndSize() {
    SelenideConfig config = CliConfig.toConfig(new String[]{"--base-url=https://example.com", "--browser-size=800x600"});
    assertThat(config.baseUrl()).isEqualTo("https://example.com");
    assertThat(config.browserSize()).isEqualTo("800x600");
  }

  @Test
  void appliesTimeout() {
    assertThat(CliConfig.toConfig(new String[]{"--timeout=9000"}).timeout()).isEqualTo(9000L);
  }

  @Test
  void appliesRemote() {
    assertThat(CliConfig.toConfig(new String[]{"--remote=http://grid:4444"}).remote()).isEqualTo("http://grid:4444");
  }

  @Test
  void ignoresPositionalUrlAndUnknownFlags() {
    SelenideConfig config = CliConfig.toConfig(new String[]{"https://example.com", "--browser=edge"});
    assertThat(config.browser()).isEqualTo("edge");
  }
}
