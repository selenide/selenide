package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.SelenideConfig;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.opera.OperaOptions;

import java.util.Map;

import static org.mockito.Mockito.mock;

@SuppressWarnings("unchecked")
class OperaDriverFactoryTest implements WithAssertions {
  private Proxy proxy = mock(Proxy.class);
  private SelenideConfig config = new SelenideConfig();

  @Test
  void browserBinaryCanBeSet() {
    config.browserBinary("c:/browser.exe");
    Capabilities caps = new OperaDriverFactory().createOperaOptions(config, proxy);
    Map options = (Map) caps.asMap().get(OperaOptions.CAPABILITY);
    assertThat(options.get("binary"))
      .isEqualTo("c:/browser.exe");
  }

  @Test
  void headlessCanNotBeSet() {
    config.headless(true);
    assertThatThrownBy(() -> new OperaDriverFactory().createOperaOptions(config, proxy))
      .isInstanceOf(InvalidArgumentException.class);
  }
}
