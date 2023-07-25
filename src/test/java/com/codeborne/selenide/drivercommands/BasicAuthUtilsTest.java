package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.BasicAuthCredentials;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.drivercommands.BasicAuthUtils.appendBasicAuthToURL;
import static com.codeborne.selenide.drivercommands.BasicAuthUtils.getHostname;
import static com.codeborne.selenide.drivercommands.BasicAuthUtils.uriMatchesDomain;
import static org.assertj.core.api.Assertions.assertThat;

final class BasicAuthUtilsTest {
  @Test
  void appendBasicAuthToURL_absoluteUrl() {
    assertThat(appendBasicAuthToURL(
      "https://company.com/login",
      new BasicAuthCredentials("domain-01", "login-01", "password-01")))
      .isEqualTo("https://login-01:password-01@company.com/login");
  }

  @Test
  void appendBasicAuthToURL_relativeUrl() {
    assertThat(appendBasicAuthToURL("/login", new BasicAuthCredentials("domain-01", "login-01", "password-01")))
      .isEqualTo("login-01:password-01@/login");
  }

  @Test
  void appendBasicAuthToURL_emptyDomain() {
    assertThat(appendBasicAuthToURL("https://company.com/login", new BasicAuthCredentials("", "login-01", "password-01")))
      .isEqualTo("https://login-01:password-01@company.com/login");
  }

  @Test
  void shouldAddHeaderOnlyOnSameDomain() {
    String domain = "burger-queen.com";
    assertThat(uriMatchesDomain("http://burger-queen.com", domain)).isTrue();
    assertThat(uriMatchesDomain("https://burger-queen.com/", domain)).isTrue();
    assertThat(uriMatchesDomain("https://burger-queen.com/", domain)).isTrue();
    assertThat(uriMatchesDomain("https://burger-queen.com/dashboard", domain)).isTrue();
    assertThat(uriMatchesDomain("ftp://burger-queen.com/dashboard", domain)).isTrue();
    assertThat(uriMatchesDomain("https://other.burger-queen.com", domain)).isFalse();
    assertThat(uriMatchesDomain("https://burger-queen.com.eu/", domain)).isFalse();
    assertThat(uriMatchesDomain("https://burger-queen-com/dashboard", domain)).isFalse();
    assertThat(uriMatchesDomain("https://s3.aws.com", domain)).isFalse();
    assertThat(uriMatchesDomain("file:///foo/bar", domain)).isFalse();
    assertThat(uriMatchesDomain("foo", domain)).isFalse();
  }

  @Test
  void shouldAddHeaderIfDomainIsEmpty_legacyMode() {
    assertThat(uriMatchesDomain("https://burger-queen.com", "")).isTrue();
    assertThat(uriMatchesDomain("https://s3.aws.com", "")).isTrue();
    assertThat(uriMatchesDomain("file:///foo/bar", "")).isTrue();
    assertThat(uriMatchesDomain("foo", "")).isTrue();
  }

  @Test
  void canSetMultipleDomains() {
    String domain = "burger-queen.com,burger-queen.eu,burger-queen.ee";
    assertThat(uriMatchesDomain("http://burger-queen.com/login", domain)).isTrue();
    assertThat(uriMatchesDomain("http://BURGER-queen.eu/login", domain)).isTrue();
    assertThat(uriMatchesDomain("http://burger-queen.EE/login", domain)).isTrue();
    assertThat(uriMatchesDomain("http://burger-queen.au/login", domain)).isFalse();
    assertThat(uriMatchesDomain("http://burger-queen.es/login", domain)).isFalse();
  }

  @Test
  void extractsHostName() {
    assertThat(getHostname("http://burger-queen.com")).isEqualTo("burger-queen.com");
    assertThat(getHostname("https://burger-queen.com.eu/zoo")).isEqualTo("burger-queen.com.eu");
    assertThat(getHostname("file:///foo/bar")).isNull();
    assertThat(getHostname("foobar")).isNull();
  }
}
