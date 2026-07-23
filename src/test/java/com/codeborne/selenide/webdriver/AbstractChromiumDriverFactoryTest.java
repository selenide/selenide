package com.codeborne.selenide.webdriver;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

class AbstractChromiumDriverFactoryTest {
  private final AbstractChromiumDriverFactory factory = spy();

  @Test
  void convertToChromiumFormat() {
    assertThat(factory.convertToChromiumFormat("100x200")).isEqualTo("100,200");
    assertThat(factory.convertToChromiumFormat("200x67")).isEqualTo("200,67");
  }
}
