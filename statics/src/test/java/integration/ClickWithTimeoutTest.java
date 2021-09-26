package integration;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

final class ClickWithTimeoutTest extends IntegrationTest {

  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void clickWithGivenTimeout() {
    $("#dynamic-content2").click(Duration.ofSeconds(3));
  }

  @Test
  void clickWithGivenTimeoutThrowsErrorAfterTimeout() {
    long timeout = 1100;
    long startMs = System.currentTimeMillis();
    assertThatCode(() ->
      $("#non-existing-element").click(Duration.ofMillis(timeout)))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Timeout: 1.100 s.");
    long elapsedTimeMs = System.currentTimeMillis() - startMs;
    assertThat(elapsedTimeMs).isBetween(timeout, timeout * 2);
  }

  @Test
  void clickWithOffsetAndGivenTimeout() {
    $("#dynamic-content2").click(5, 5, Duration.ofSeconds(3));
  }

  @Test
  void clickWithOffsetAndTimeoutThrowsErrorAfterTimeout() {
    long timeout = 1100;
    long startMs = System.currentTimeMillis();
    assertThatCode(() ->
      $("#non-existing-element").click(5, 5, Duration.ofMillis(timeout)))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Timeout: 1.100 s.");
    long elapsedTimeMs = System.currentTimeMillis() - startMs;
    assertThat(elapsedTimeMs).isBetween(timeout, timeout * 2);
  }

  @Test
  void clickWithClickOptionsAndGivenTimeout() {
    $("#dynamic-content2").click(ClickOptions.usingDefaultMethod(), Duration.ofSeconds(3));
  }

  @Test
  void clickWithClickOptionsAndTimeoutThrowsErrorAfterTimeout() {
    long timeout = 1100;
    long startMs = System.currentTimeMillis();
    assertThatCode(() ->
      $("#non-existing-element").click(ClickOptions.usingDefaultMethod(), Duration.ofMillis(timeout)))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Timeout: 1.100 s.");
    long elapsedTimeMs = System.currentTimeMillis() - startMs;
    assertThat(elapsedTimeMs).isBetween(timeout, timeout * 2);
  }
}
