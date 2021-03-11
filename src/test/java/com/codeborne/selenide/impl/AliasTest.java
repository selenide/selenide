package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AliasTest {
  @Test
  void userCanProvideAliasForSomething() {
    assertThat(new Alias("login button").getOrElse(() -> "//[id=login]")).isEqualTo("login button");
  }

  @Test
  void defaultAliasIsNone() {
    assertThat(Alias.NONE.getOrElse(() -> "//[id=login]")).isEqualTo("//[id=login]");
  }

  @Test
  void emptyAliasNotAllowed() {
    assertThatThrownBy(() -> new Alias(""))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Empty alias not allowed");
  }

  @Test
  void canGetTextOfAlias() {
    assertThat(new Alias("login button").getText()).isEqualTo("login button");
    assertThat(Alias.NONE.getText()).isNull();
  }
}
