package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.BasicAuthCredentials;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class BasicAuthUrlTest {
  private final BasicAuthUrl basicAuthUrl = new BasicAuthUrl();

  @Test
  void appendBasicAuthToURL_absoluteUrl() {
    assertThat(basicAuthUrl
      .appendBasicAuthToURL(
        "https://company.com/login",
        new BasicAuthCredentials("domain-01", "login-01", "password-01")))
      .isEqualTo("https://domain-01%5Clogin-01:password-01@company.com/login");
  }

  @Test
  void appendBasicAuthToURL_relativeUrl() {
    assertThat(basicAuthUrl.appendBasicAuthToURL(
      "/login",
      new BasicAuthCredentials("domain-01", "login-01", "password-01")))
      .isEqualTo("domain-01%5Clogin-01:password-01@/login");
  }

  @Test
  void appendBasicAuthToURL_emptyDomain() {
    assertThat(basicAuthUrl.appendBasicAuthToURL(
      "https://company.com/login",
      new BasicAuthCredentials("", "login-01", "password-01")))
      .isEqualTo("https://login-01:password-01@company.com/login");
  }
}
