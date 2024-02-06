package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

final class IfWithTimeoutTest extends IntegrationTest {
  @BeforeEach
  void openPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void is_waits_and_returns_false() {
    boolean isDisplayed = $("#dynamic-content2").is(visible, Duration.ofMillis(22));
    assertThat(isDisplayed)
      .as("Expect element #dynamic-content2 to appear after ~150ms")
      .isFalse();
  }

  @Test
  void is_waits_and_returns_true() {
    boolean isDisplayed = $("#dynamic-content2").is(visible, Duration.ofSeconds(2));
    assertThat(isDisplayed)
      .as("Expect element #dynamic-content2 to appear after ~150ms")
      .isTrue();
  }

  @Test
  void has_waits() {
    boolean hasText = $("#dynamic-content2").has(text("dynamic content2"), Duration.ofSeconds(2));
    assertThat(hasText)
      .as("Expect element #dynamic-content2 to appear after ~150ms")
      .isTrue();
  }
}
