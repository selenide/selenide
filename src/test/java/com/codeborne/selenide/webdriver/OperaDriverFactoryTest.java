package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.SelenideConfig;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.opera.OperaOptions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchArgs;
import static org.mockito.Mockito.mock;

class OperaDriverFactoryTest implements WithAssertions {
  private Proxy proxy = mock(Proxy.class);
  private SelenideConfig config = new SelenideConfig().headless(false);

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

  @Test
  void additionalOptionsCanBeAddedFromCodeAsArray() {
    config.additionalOptions("blahhh", "anotherOption", "mahTests");
    OperaOptions operaOptions = new OperaDriverFactory().createOperaOptions(config, proxy);
    List<String> optionArguments = getBrowserLaunchArgs(OperaOptions.CAPABILITY, operaOptions);

    assertThat(optionArguments).contains("blahhh", "anotherOption", "mahTests");
  }

  @Test
  void additionalOptionsCanBeAddedFromCodeAsList() {
    config.additionalOptions(Arrays.asList("blahhh", "anotherOption", "mahTests"));
    OperaOptions operaOptions = new OperaDriverFactory().createOperaOptions(config, proxy);
    List<String> optionArguments = getBrowserLaunchArgs(OperaOptions.CAPABILITY, operaOptions);

    assertThat(optionArguments).contains("blahhh", "anotherOption", "mahTests");
  }
}
