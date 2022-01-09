package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.opera.OperaOptions;

import java.io.File;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

final class OperaDriverFactoryTest {
  private final Proxy proxy = mock(Proxy.class);
  private final File browserDownloadsFolder = new File("build/downlao").getAbsoluteFile();
  private final SelenideConfig config = new SelenideConfig().headless(false);
  private final Browser browser = new Browser(config.browser(), config.headless());
  private final OperaDriverFactory factory = new OperaDriverFactory();

  @Test
  void browserBinaryCanBeSet() {
    config.browserBinary("c:/browser.exe");
    Capabilities caps = factory.createCapabilities(config, browser, proxy, browserDownloadsFolder);
    Map<String, Object> options = options(caps);
    assertThat(options.get("binary")).isEqualTo("c:/browser.exe");
  }

  @Test
  void arguments() {
    Capabilities caps = factory.createCapabilities(config, browser, proxy, browserDownloadsFolder);
    Map<String, Object> options = options(caps);
    assertThat(options.get("args")).isEqualTo(singletonList("--ignore-certificate-errors"));
  }

  @Test
  void headlessCanNotBeSet() {
    config.headless(true);
    assertThatThrownBy(() -> factory.createCapabilities(config, browser, proxy, browserDownloadsFolder))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> options(Capabilities caps) {
    return (Map<String, Object>) caps.asMap().get(OperaOptions.CAPABILITY);
  }
}
