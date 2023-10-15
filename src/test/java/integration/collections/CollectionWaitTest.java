package integration.collections;

import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class CollectionWaitTest extends ITest {
  private long startedAt;

  @BeforeEach
  void openTestPage() {
    openFile("collection_with_delays.html");
    setTimeout(1000);
    startedAt = System.currentTimeMillis();
  }

  @Test
  void waitsUntilNthElementAppears() {
    $$("#collection li").get(5).shouldBe(visible);
    $$("#collection li").get(13).shouldBe(visible);
    $$("#collection li").get(19).shouldBe(visible);

    $$("#collection li").first().shouldBe(visible).shouldHave(text("Element #0"));
    $$("#collection li").last().shouldBe(visible).shouldHave(text("Element #19"));
  }

  @Test
  void failsIfWrongSize() {
    setTimeout(3);
    assertThatThrownBy(() -> $$("#collection li").shouldHave(size(-1)))
      .isInstanceOf(AssertionError.class)
      .hasMessageContaining("expected: = -1, actual: ")
      .hasMessageContaining("collection: #collection li")
      .hasMessageContaining("Timeout: 3 ms.");
    assertTestTookLessThan(500, MILLISECONDS);
  }

  @Test
  void waitsUntilCollectionsGetsLoaded() {
    $$("#collection li").shouldHave(size(20));
  }

  @Test
  void waitsUntilFirstNElementsGetLoaded() {
    $$("#collection li").first(3).shouldHave(texts("Element #0", "Element #1", "Element #2"));
  }

  @Test
  void waitsUntilLastNElementsGetLoaded() {
    $$("#collection li").last(2).shouldHave(texts("Element #18", "Element #19"));
  }

  @Test
  void firstNElements_TextsMismatchErrorMessage() {
    assertThatThrownBy(() -> $$("#collection li").first(2).shouldHave(texts("Element", "#wrong"), Duration.ofSeconds(1)))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageStartingWith(
        String.format("Text #1 mismatch (expected: \"#wrong\", actual: \"Element #1\")%n" +
                      "Actual (2): [Element #0, Element #1]%n" +
                      "Expected (2): [Element, #wrong]%n" +
                      "Collection: #collection li:first(2)"))
      .hasMessageContaining("Timeout: 1 s.");
    assertTestTookMoreThan(1, SECONDS);
  }

  @Test
  void firstNElements_TextsSizeMismatchErrorMessage() {
    assertThatThrownBy(() -> $$("#collection li").first(2).shouldHave(texts("Element #wrong")))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageStartingWith("List size mismatch (expected: 1, actual: 2)")
      .hasMessageContaining("Actual (2): [Element #0, Element #1]")
      .hasMessageContaining("Expected (1): [Element #wrong]");
    assertTestTookMoreThan(1, SECONDS);
  }

  @Test
  void lastNElements_errorMessage() {
    assertThatThrownBy(() -> $$("#collection li").last(2).shouldHave(texts("Element", "#wrong"), Duration.ofSeconds(1)))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageStartingWith("Text #1 mismatch (expected: \"#wrong\", actual: \"Element #19\")")
      .hasMessageContaining(String.format("Actual (2): [Element #18, Element #19]%n" +
        "Expected (2): [Element, #wrong]%n" +
        "Collection: #collection li:last(2)"))
        .hasMessageContaining("Timeout: 1 s.");
    assertTestTookMoreThan(1, SECONDS);
  }

  @Test
  void customTimeoutForCollections() {
    setTimeout(1);
    $$("#collection li").first(2).shouldHave(texts("Element #0", "Element #1"), Duration.ofSeconds(5));
    $$("#collection li").last(2).shouldHave(texts("Element #18", "Element #19"), Duration.ofSeconds(5));
  }

  @Test
  void waitsForCustomTimeoutForCollections() {
    setTimeout(1);
    assertThatThrownBy(() ->
      $$("#collection li").last(2).shouldHave(texts("Element #88888", "Element #99999"), Duration.ofMillis(999))
    )
      .isInstanceOf(TextsMismatch.class)
      .hasMessageContaining("Text #0 mismatch (expected: \"Element #88888\", actual: \"Element #18\")")
      .hasMessageContaining("Actual (2): [Element #18, Element #19]")
      .hasMessageContaining("Expected (2): [Element #88888, Element #99999]")
      .hasMessageContaining("Timeout: 999 ms.");
    assertTestTookMoreThan(999, MILLISECONDS);
  }

  @Test
  void waitsForElementInsideCollection() {
    setTimeout(100);
    assertThatThrownBy(() ->
      $$("h1").findBy(cssClass("active")).findAll("h2").shouldHave(texts("nothing else matters"))
    )
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Element not found {h1.findBy(css class \"active\")");
    assertTestTookMoreThan(100, MILLISECONDS);
  }

  private void assertTestTookMoreThan(int value, TimeUnit unit) {
    long endedAt = System.currentTimeMillis();
    assertThat(endedAt - startedAt).isGreaterThanOrEqualTo(unit.toMillis(value));
  }

  private void assertTestTookLessThan(int value, TimeUnit unit) {
    long endedAt = System.currentTimeMillis();
    assertThat(endedAt - startedAt).isLessThanOrEqualTo(unit.toMillis(value));
  }
}
