package com.codeborne.selenide.webdriver;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.SelenideConfig;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SafariDriverFactoryTest {
  private SelenideConfig config = new SelenideConfig();
  private final Browser browser = new Browser(config.browser(), config.headless());
  private final SafariDriverFactory factory = new SafariDriverFactory();

  @BeforeEach
  void cleanup() {
    config = new SelenideConfig().downloadsFolder("build/should-not-be-used");
  }

  @Test
  void browserBinaryCannotBeSet() {
    config.browserBinary("c:/browser.exe");
    assertThatThrownBy(() -> factory.createCapabilities(config, browser, null, new File("/tmp/downloads-folder-12345")))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("browser binary path not supported in Safari. Reset browserBinary setting.");
  }

  @Test
  void headlessCanNotBeSet() {
    config.headless(true);
    assertThatThrownBy(() -> factory.createCapabilities(config, browser, null, new File("/tmp/downloads-folder-12345")))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("headless browser not supported in Safari. Set headless property to false.");
  }
}
