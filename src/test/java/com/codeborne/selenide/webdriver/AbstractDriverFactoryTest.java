package com.codeborne.selenide.webdriver;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

final class AbstractDriverFactoryTest {
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

  @Test
  void convertStringToNearestObjectType() {
    assertThat(factory.convertStringToNearestObjectType("false")).as("Boolean").isEqualTo(false);
    assertThat(factory.convertStringToNearestObjectType("FALsE")).as("Boolean").isEqualTo(false);
    assertThat(factory.convertStringToNearestObjectType("tRUE")).as("Boolean").isEqualTo(true);
    assertThat(factory.convertStringToNearestObjectType("true")).as("Boolean").isEqualTo(true);

    assertThat(factory.convertStringToNearestObjectType("0")).as("Integer").isEqualTo(0);
    assertThat(factory.convertStringToNearestObjectType("-42")).as("Integer").isEqualTo(-42);

    assertThat(factory.convertStringToNearestObjectType("")).as("String").isEqualTo("");

    assertThat(factory.convertStringToNearestObjectType("Hottabych 2 false"))
      .as("any other value")
      .isEqualTo("Hottabych 2 false");
  }
}
