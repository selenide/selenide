package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.impl.Plugins.inject;
import static org.assertj.core.api.Assertions.assertThat;

final class PluginsTest {
  @Test
  void loadsDefaultImplementationsFromMetaInf() {
    assertThat(inject(ElementDescriber.class)).isInstanceOf(SelenideElementDescriber.class);
    assertThat(inject(Photographer.class)).isInstanceOf(WebdriverPhotographer.class);
  }
}
