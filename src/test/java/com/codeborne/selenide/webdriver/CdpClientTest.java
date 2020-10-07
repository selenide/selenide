package com.codeborne.selenide.webdriver;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class CdpClientTest {
  private final CdpClient cdp = new CdpClient();

  @Test
  void escapeForJson() {
    assertThat(cdp.escapeForJson("build/downloads"))
      .as("Any string without special characters is left as is")
      .isEqualTo("build/downloads");
  }

  @Test
  void escapeForJson_quotes() {
    assertThat(cdp.escapeForJson("/Users/An\"rie/Down\"loads"))
      .as("Quotes should be escaped")
      .isEqualTo("/Users/An\\\"rie/Down\\\"loads");
  }

  @Test
  void escapeForJson_backslashes() {
    assertThat(cdp.escapeForJson("C:\\dev\\sources\\middleware\\build\\downloads"))
      .as("Backslashes should be escaped")
      .isEqualTo("C:\\\\dev\\\\sources\\\\middleware\\\\build\\\\downloads");
  }

  @Test
  void escapeForJson_quotes_and_backslashes() {
    assertThat(cdp.escapeForJson("c:\\users\\An\"rie\\Downloads"))
      .as("Both backslashes and quotes")
      .isEqualTo("c:\\\\users\\\\An\\\"rie\\\\Downloads");
  }
}
