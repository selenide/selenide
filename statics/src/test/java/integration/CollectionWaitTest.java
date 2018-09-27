package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.TextsMismatch;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;

class CollectionWaitTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("collection_with_delays.html");
  }

  @Test
  void waitsUntilNthElementAppears() {
    $$("#collection li").get(5).shouldBe(visible);
    $$("#collection li").get(33).shouldBe(visible);
    $$("#collection li").get(49).shouldBe(visible);

    $$("#collection li").first().shouldBe(visible).shouldHave(text("Element #0"));
    $$("#collection li").last().shouldBe(visible).shouldHave(text("Element #49"));
  }

  @Test
  void failsIfWrongSize() {
    assertThatThrownBy(() -> $$("#collection li").shouldHave(size(-1)))
      .isInstanceOf(AssertionError.class)
      .hasMessageContaining("expected: = -1, actual: 50, collection: #collection li");
  }

  @Test
  void canDetermineSize() {
    $$("#collection li").shouldHave(size(50));
  }

  @Test
  void waitsUntilFirstNElementsGetLoaded() {
    $$("#collection li").first(3).shouldHave(texts("Element #0", "Element #1", "Element #2"));
  }

  @Test
  void waitsUntilLastNElementsGetLoaded() {
    $$("#collection li").last(2).shouldHave(texts("Element #48", "Element #49"));
  }

  @Test
  void firstNElements_errorMessage() {
    Configuration.timeout = 4000;
    Assertions.assertThatThrownBy(() -> $$("#collection li").first(2).shouldHave(texts("Element #wrong")))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageContaining("Actual: [Element #0, Element #1]\n" +
        "Expected: [Element #wrong]\n" +
        "Collection: #collection li.first(2)");
  }

  @Test
  void lastNElements_errorMessage() {
    Configuration.timeout = 4000;
    Assertions.assertThatThrownBy(() -> $$("#collection li").last(2).shouldHave(texts("Element #wrong")))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageContaining("Actual: [Element #48, Element #49]\n" +
        "Expected: [Element #wrong]\n" +
        "Collection: #collection li.last(2)");
  }

  @Test
  void customTimeoutForCollections() {
    Configuration.timeout = 1;
    $$("#collection li").last(2).shouldHave(texts("Element #48", "Element #49"), 5000);
  }
}
