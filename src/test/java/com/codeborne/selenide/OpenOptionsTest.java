package com.codeborne.selenide;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.OpenOptions.withBrowserState;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class OpenOptionsTest {
  @TempDir
  private Path tempDir;

  @Test
  void withBrowserState_readsFileLazily() {
    Path file = tempDir.resolve("missing.json");
    OpenOptions options = withBrowserState(file);

    assertThatThrownBy(options::browserState)
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Browser state file not found: " + file.toAbsolutePath());
  }

  @Test
  void withBrowserState_inMemory() {
    BrowserState state = new BrowserState("http://localhost", List.of(), Map.of("token", "t-1"), Map.of());

    assertThat(withBrowserState(state).browserState()).isSameAs(state);
  }

  @Test
  void orLogin_requiresLoggedInCheck() {
    OpenOptions options = withBrowserState("build/auth.json");

    assertThatThrownBy(() -> options.orLogin(() -> { }))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageStartingWith("Call loggedInIf() before orLogin()");
  }

  @Test
  void orLogin_requiresFile() {
    BrowserState state = new BrowserState("http://localhost", List.of(), Map.of(), Map.of());
    OpenOptions options = withBrowserState(state).loggedInIf(() -> true);

    assertThatThrownBy(() -> options.orLogin(() -> { }))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageStartingWith("orLogin() requires browser state file");
  }

  @Test
  void originOfUrl() {
    assertThat(OpenWithBrowserState.originOf("https://App.Example.com/dashboard?x=1")).isEqualTo("https://app.example.com");
    assertThat(OpenWithBrowserState.originOf("https://app.example.com:443/")).isEqualTo("https://app.example.com");
    assertThat(OpenWithBrowserState.originOf("http://localhost:80/a")).isEqualTo("http://localhost");
    assertThat(OpenWithBrowserState.originOf("http://127.0.0.1:8080/a")).isEqualTo("http://127.0.0.1:8080");
    assertThat(OpenWithBrowserState.originOf("https://INDIGO.com/")).isEqualTo("https://indigo.com");
  }
}
