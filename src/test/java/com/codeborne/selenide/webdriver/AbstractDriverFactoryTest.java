package com.codeborne.selenide.webdriver;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

class AbstractDriverFactoryTest {
  private final AbstractDriverFactory factory = spy(AbstractDriverFactory.class);

  @Test
  void majorVersion() {
    assertThat(factory.majorVersion(null)).isEqualTo(0);
    assertThat(factory.majorVersion("")).isEqualTo(0);
    assertThat(factory.majorVersion("1")).isEqualTo(1);
    assertThat(factory.majorVersion("2.3")).isEqualTo(2);
    assertThat(factory.majorVersion("6.17134")).isEqualTo(6);
    assertThat(factory.majorVersion("82.0.459.1")).isEqualTo(82);
  }
}
