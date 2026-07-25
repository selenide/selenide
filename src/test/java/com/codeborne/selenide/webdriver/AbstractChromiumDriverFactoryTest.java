package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

class AbstractChromiumDriverFactoryTest {
  private final AbstractChromiumDriverFactory factory = spy();
  private final SelenideConfig config = new SelenideConfig().browser("firefox").downloadsFolder("not-used");

  @Test
  void convertToChromiumFormat() {
    assertThat(factory.convertToChromiumFormat("100x200")).isEqualTo("100,200");
    assertThat(factory.convertToChromiumFormat("200x67")).isEqualTo("200,67");
  }

  @Test
  void enablesProxyForLocalhost() {
    List<String> args = factory.createChromiumArguments(config, "");

    assertThat(args).contains("--proxy-bypass-list=<-loopback>");
  }

  @Test
  void setsBrowserSizeWithChromiumArgument() {
    config.browserSize("1096x812");

    List<String> args = factory.createChromiumArguments(config, "");

    assertThat(args).contains("--window-size=1096,812");
  }

  @Test
  void setsBrowserPositionWithChromiumArgument() {
    config.browserPosition("200x300");

    List<String> args = factory.createChromiumArguments(config, "");

    assertThat(args).contains("--window-position=200,300");
  }
}
