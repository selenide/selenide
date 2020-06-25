package com.codeborne.selenide.webdriver;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CdpClientTest {
  private final CdpClient cdp = new CdpClient();

  @Test
  void escapeForJson() {
    assertThat(cdp.escapeForJson("build/downloads")).isEqualTo("build/downloads");
    assertThat(cdp.escapeForJson("c:\\users\\An\"rie\\Downloads")).isEqualTo("c:\\users\\An\\\"rie\\Downloads");
  }
}
