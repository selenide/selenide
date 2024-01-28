package com.codeborne.selenide;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BearerTokenCredentialsTest {
  @Test
  void encodeReturnsTokenAsIs() {
    assertThat(new BearerTokenCredentials("", "token1").encode()).isEqualTo("token1");
  }

  @Test
  void masksTokenInToString() {
    assertThat(new BearerTokenCredentials("", "token1")).hasToString(":******");
    assertThat(new BearerTokenCredentials("monsters.inc", "token2")).hasToString("monsters.inc:******");
  }
}
