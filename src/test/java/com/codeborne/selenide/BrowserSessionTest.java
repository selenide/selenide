package com.codeborne.selenide;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import java.util.Date;

import static com.codeborne.selenide.BrowserSession.normalize;
import static org.assertj.core.api.Assertions.assertThat;

final class BrowserSessionTest {
  private final Date expiry = new Date(1_791_234_567_000L);

  @Test
  void domainCookie_isAddedAsIs() {
    Cookie cookie = new Cookie.Builder("lang", "ru").domain(".example.com").sameSite("Lax").build();

    assertThat(normalize(cookie)).isSameAs(cookie);
  }

  @Test
  void hostOnlyCookie_isAddedWithoutDomain() {
    Cookie cookie = new Cookie.Builder("SID", "abc").domain("app.example.com").path("/admin")
      .expiresOn(expiry).isSecure(true).isHttpOnly(true).sameSite("Strict").build();

    Cookie normalized = normalize(cookie);

    assertThat(normalized.getDomain()).isNull();
    assertThat(normalized.getName()).isEqualTo("SID");
    assertThat(normalized.getValue()).isEqualTo("abc");
    assertThat(normalized.getPath()).isEqualTo("/admin");
    assertThat(normalized.getExpiry()).isEqualTo(expiry);
    assertThat(normalized.isSecure()).isTrue();
    assertThat(normalized.isHttpOnly()).isTrue();
    assertThat(normalized.getSameSite()).isEqualTo("Strict");
  }

  @Test
  void insecureCookie_withSameSiteNone_isAddedWithoutSameSite() {
    Cookie cookie = new Cookie.Builder("session_id", "123").domain(".example.com").sameSite("None").build();

    Cookie normalized = normalize(cookie);

    assertThat(normalized.getSameSite()).isNull();
    assertThat(normalized.getDomain()).isEqualTo(".example.com");
  }

  @Test
  void secureCookie_withSameSiteNone_keepsSameSite() {
    Cookie cookie = new Cookie.Builder("session_id", "123").domain(".example.com").isSecure(true).sameSite("None").build();

    assertThat(normalize(cookie)).isSameAs(cookie);
  }
}
