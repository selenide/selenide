package com.codeborne.selenide.impl.windows;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FrameByIdOrNameTest {
  @Test
  void searchesFrameOrIframeByIdOrName() {
    assertThat(new FrameByIdOrName("menu"))
      .hasToString("frame to be available: By.cssSelector: frame#menu,frame[name=menu],iframe#menu,iframe[name=menu]");
  }
}
