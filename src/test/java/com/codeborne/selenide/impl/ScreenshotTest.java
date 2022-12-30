package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Test;

import java.io.File;

import static java.lang.System.lineSeparator;
import static org.assertj.core.api.Assertions.assertThat;

class ScreenshotTest {
  @Test
  void summary() {
    assertThat(new Screenshot(new File("shot.png"), "/home/user/shot.png", "/home/user/shot.html").summary())
      .isEqualTo("Screenshot: /home/user/shot.png" +
        lineSeparator() + "Page source: /home/user/shot.html");
  }

  @Test
  void summary_is_empty_if_no_files() {
    assertThat(Screenshot.none().summary()).isEqualTo("");
  }

  @Test
  void summary_with_only_image() {
    assertThat(new Screenshot(new File("shot.png"), "/home/user/shot.png", null).summary())
      .isEqualTo("Screenshot: /home/user/shot.png");
  }

  @Test
  void summary_with_only_page_source() {
    assertThat(new Screenshot(null, null, "/home/user/shot.html").summary())
      .isEqualTo("Page source: /home/user/shot.html");
  }

  @Test
  void isPresent_ifHasImage() {
    assertThat(new Screenshot(new File("shot.png"), "/home/user/shot.png", null).isPresent()).isTrue();
  }

  @Test
  void isPresent_ifHasSource() {
    assertThat(new Screenshot(null, null, "/home/user/shot.html").isPresent()).isTrue();
  }

  @Test
  void isNotPresent() {
    assertThat(Screenshot.none().isPresent()).isFalse();
  }
}
