package com.codeborne.selenide;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BasicAuthCredentialsTest {
  @Test
  void encodesCredentialsInBase64() {
    assertThat(new BasicAuthCredentials("aladdin", "opensesame").encode()).isEqualTo("YWxhZGRpbjpvcGVuc2VzYW1l");
  }

  @Test
  void usesEmptyDomainByDefault() {
    assertThat(new BasicAuthCredentials("aladdin", "opensesame").domain()).isEqualTo("");
  }

  @Test
  void masksPasswordInToString() {
    assertThat(new BasicAuthCredentials("admin", "secret")).hasToString(":admin:******");
    assertThat(new BasicAuthCredentials("monsters.inc", "admin", "secret")).hasToString("monsters.inc:admin:******");
  }
}
