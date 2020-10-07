package com.codeborne.selenide.drivercommands;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

final class BasicAuthUrlTest implements WithAssertions {
  private BasicAuthUrl basicAuthUrl = new BasicAuthUrl();

  @Test
  void appendBasicAuthToURL_absoluteUrl() {
    assertThat(basicAuthUrl.appendBasicAuthToURL("https://company.com/login", "domain-01", "login-01", "password-01"))
      .isEqualTo("https://domain-01%5Clogin-01:password-01@company.com/login");
  }

  @Test
  void appendBasicAuthToURL_relativeUrl() {
    assertThat(basicAuthUrl.appendBasicAuthToURL("/login", "domain-01", "login-01", "password-01"))
      .isEqualTo("domain-01%5Clogin-01:password-01@/login");
  }

  @Test
  void appendBasicAuthToURL_emptyDomain() {
    assertThat(basicAuthUrl.appendBasicAuthToURL("https://company.com/login", "", "login-01", "password-01"))
      .isEqualTo("https://login-01:password-01@company.com/login");
  }
}
