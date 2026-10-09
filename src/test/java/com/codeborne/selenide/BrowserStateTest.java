package com.codeborne.selenide;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openqa.selenium.Cookie;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class BrowserStateTest {
  @TempDir
  private Path tempDir;

  private final Cookie sessionCookie = new Cookie.Builder("SID", "abc123")
    .domain("app.example.com")
    .path("/")
    .isSecure(true)
    .isHttpOnly(true)
    .sameSite("Lax")
    .build();

  private final Cookie persistentCookie = new Cookie.Builder("lang", "ru")
    .domain(".example.com")
    .path("/admin")
    .expiresOn(new Date(1_791_234_567_000L))
    .build();

  @Test
  void jsonRoundTrip() {
    BrowserState state = new BrowserState("https://app.example.com",
      List.of(sessionCookie, persistentCookie),
      Map.of("token", "секрет ☃", "empty", ""),
      Map.of("tab", "2"));

    BrowserState restored = BrowserState.fromJson(state.toJson());

    assertThat(restored.origin()).isEqualTo("https://app.example.com");
    assertThat(restored.localStorage()).isEqualTo(Map.of("token", "секрет ☃", "empty", ""));
    assertThat(restored.sessionStorage()).isEqualTo(Map.of("tab", "2"));
    assertThat(restored.cookies()).hasSize(2);
    assertSameCookie(restored.cookies().get(0), sessionCookie);
    assertSameCookie(restored.cookies().get(1), persistentCookie);
  }

  private static void assertSameCookie(Cookie actual, Cookie expected) {
    assertThat(actual.getName()).isEqualTo(expected.getName());
    assertThat(actual.getValue()).isEqualTo(expected.getValue());
    assertThat(actual.getDomain()).isEqualTo(expected.getDomain());
    assertThat(actual.getPath()).isEqualTo(expected.getPath());
    assertThat(actual.getExpiry()).isEqualTo(expected.getExpiry());
    assertThat(actual.isSecure()).isEqualTo(expected.isSecure());
    assertThat(actual.isHttpOnly()).isEqualTo(expected.isHttpOnly());
    assertThat(actual.getSameSite()).isEqualTo(expected.getSameSite());
  }

  @Test
  void sessionCookieHasNoExpiryInJson() {
    BrowserState state = new BrowserState("https://app.example.com",
      List.of(sessionCookie), Map.of(), Map.of());

    assertThat(state.toJson()).doesNotContain("expiry");
    assertThat(BrowserState.fromJson(state.toJson()).cookies().get(0).getExpiry()).isNull();
  }

  @Test
  void emptyState() {
    BrowserState state = BrowserState.fromJson("{\"origin\": \"http://localhost:8080\"}");

    assertThat(state.origin()).isEqualTo("http://localhost:8080");
    assertThat(state.cookies()).isEmpty();
    assertThat(state.localStorage()).isEmpty();
    assertThat(state.sessionStorage()).isEmpty();
  }

  @Test
  void originIsRequired() {
    assertThatThrownBy(() -> BrowserState.fromJson("{\"localStorage\": {\"token\": \"secret-token\"}}"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Missing \"origin\" in browser state");
  }

  @Test
  void fromFile() throws Exception {
    Path file = tempDir.resolve("state.json");
    Files.writeString(file, "{\"origin\": \"http://localhost:8080\", \"localStorage\": {\"token\": \"t-1\"}}", UTF_8);

    BrowserState state = BrowserState.fromFile(file);

    assertThat(state.origin()).isEqualTo("http://localhost:8080");
    assertThat(state.localStorage()).isEqualTo(Map.of("token", "t-1"));
  }

  @Test
  void fromFile_missingFile() {
    Path file = tempDir.resolve("missing.json");

    assertThatThrownBy(() -> BrowserState.fromFile(file))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Browser state file not found: " + file.toAbsolutePath());
  }

  @Test
  void toString_doesNotContainValues() {
    BrowserState state = new BrowserState("https://app.example.com",
      List.of(sessionCookie), Map.of("token", "jwt-secret"), Map.of("tab", "orders"));

    assertThat(state).hasToString(
      "BrowserState(https://app.example.com, cookies: [SID], localStorage: [token], sessionStorage: [tab])");
  }
}
