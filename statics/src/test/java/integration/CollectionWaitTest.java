package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.ex.TextsSizeMismatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CollectionWaitTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("collection_with_delays.html");
    Configuration.timeout = 1000;
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
    assertThatThrownBy(() -> $$("#collection li").shouldHave(size(-1)))
      .isInstanceOf(AssertionError.class)
      .hasMessageContaining("expected: = -1, actual: 20, collection: #collection li");
  }

  @Test
  void canDetermineSize() {
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
    assertThatThrownBy(() -> $$("#collection li").first(2).shouldHave(texts("Element", "#wrong")))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageContaining(String.format("Actual: [Element #0, Element #1]%n" +
        "Expected: [Element, #wrong]%n" +
        "Collection: #collection li.first(2)"));
  }

  @Test
  void firstNElements_TextsSizeMismatchErrorMessage() {
    assertThatThrownBy(() -> $$("#collection li").first(2).shouldHave(texts("Element #wrong")))
      .isInstanceOf(TextsSizeMismatch.class)
      .hasMessageContaining(String.format("Actual: [Element #0, Element #1], List size: 2%n" +
        "Expected: [Element #wrong], List size: 1%n" +
        "Collection: #collection li.first(2)"));
  }

  @Test
  void lastNElements_errorMessage() {
    assertThatThrownBy(() -> $$("#collection li").last(2).shouldHave(texts("Element", "#wrong")))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageContaining(String.format("Actual: [Element #18, Element #19]%n" +
        "Expected: [Element, #wrong]%n" +
        "Collection: #collection li.last(2)"));
  }

  @Test
  void customTimeoutForCollections() {
    Configuration.timeout = 1;
    $$("#collection li").last(2).shouldHave(texts("Element #18", "Element #19"), 5000);
  }
}
