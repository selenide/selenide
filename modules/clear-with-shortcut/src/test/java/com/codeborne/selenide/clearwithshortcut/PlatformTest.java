package com.codeborne.selenide.clearwithshortcut;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlatformTest {
  @Test
  void platformCanBeUnknown() {
    assertThat(new Platform(null).isUnknown()).isTrue();
    assertThat(new Platform("?").isUnknown()).isTrue();
    assertThat(new Platform("").isUnknown()).isTrue();
    assertThat(new Platform(" ").isUnknown()).isTrue();
    assertThat(new Platform("        ").isUnknown()).isTrue();
    assertThat(new Platform("MacIntel").isUnknown()).isFalse();
  }

  @Test
  void isApple() {
    assertThat(new Platform("MacIntel").isApple()).isTrue();
    assertThat(new Platform("Win32").isApple()).isFalse();
    assertThat(new Platform("Win64").isApple()).isFalse();
    assertThat(new Platform("Linux x86_64").isApple()).isFalse();
  }

  @Test
  void isApple_forUnknownPlatform() {
    assertThatThrownBy(() -> new Platform("?").isApple())
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void modifierKey() {
    assertThat(new Platform("MacIntel").modifierKey()).isEqualTo(Keys.COMMAND);
    assertThat(new Platform("Win32").modifierKey()).isEqualTo(Keys.CONTROL);
    assertThat(new Platform("Linux x86_64").modifierKey()).isEqualTo(Keys.CONTROL);
  }

  @Test
  void modifierKey_forUnknownPlatform() {
    assertThatThrownBy(() -> new Platform("?").modifierKey())
      .isInstanceOf(IllegalStateException.class);
  }
}
