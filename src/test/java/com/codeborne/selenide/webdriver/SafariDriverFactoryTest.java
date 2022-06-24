package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.safari.SafariOptions;

import java.io.File;

import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SafariDriverFactoryTest {
  private final SelenideConfig config = new SelenideConfig().headless(false).downloadsFolder("build/should-not-be-used");
  private final Browser browser = new Browser(config.browser(), config.headless());
  private final SafariDriverFactory factory = new SafariDriverFactory();

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

  @Test
  void mergesDefaultCapabilities() {
    SafariOptions options = factory.createCapabilities(config, browser, null, new File("/tmp/downloads-folder-12345"));
    assertThat(options.getCapability("acceptInsecureCerts")).isEqualTo(TRUE);
    assertThat(options.getCapability("browserName")).isEqualTo("safari");
  }
}
