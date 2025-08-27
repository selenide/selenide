package com.codeborne.selenide.fullscreenshot;

import com.codeborne.selenide.impl.Photographer;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.impl.Plugins.inject;
import static org.assertj.core.api.Assertions.assertThat;

class FullSizePhotographerTest {
  @Test
  void injectsTheRightImplementation() {
    Photographer photographer = inject(Photographer.class);
    assertThat(photographer).isInstanceOf(FullSizePhotographer.class);
  }
}
