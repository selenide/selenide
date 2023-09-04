package integration.collections;

import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.ex.TextsMismatch;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.texts;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class CollectionBecauseTest extends ITest {
  @BeforeEach
  void openTestPage() {
    setTimeout(10);
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void canExplainWhyConditionIsExpected_textsMismatch() {
    assertThatThrownBy(() -> {
      $$("#dropdown-list-container option").shouldHave(
        texts("foo", "bar", "var", "buzz")
          .because("that's why")
      );
    })
      .isInstanceOf(TextsMismatch.class)
      .hasMessageStartingWith("Text #0 mismatch (expected: \"foo\", actual: \"@one.io\")")
      .hasMessageContaining("Actual (4): [@one.io, @two.eu, @three.com, @four.ee]")
      .hasMessageContaining("Expected (4): [foo, bar, var, buzz]")
      .hasMessageContaining("Because: that's why");
  }

  @Test
  void canExplainWhyConditionIsExpected_textsSizeMismatch() {
    assertThatThrownBy(() -> {
      $$("#dropdown-list-container option").shouldHave(
        texts("foo", "bar", "var, buzz")
          .because("that's why")
      );
    })
      .isInstanceOf(TextsMismatch.class)
      .hasMessageStartingWith("List size mismatch (expected: 3, actual: 4)")
      .hasMessageContaining("Actual (4): [@one.io, @two.eu, @three.com, @four.ee]")
      .hasMessageContaining("Expected (3): [foo, bar, var, buzz]")
      .hasMessageContaining("Because: that's why")
      .hasMessageContaining("Collection: #dropdown-list-container option");
  }

  @Test
  void canExplainWhyConditionIsExpected_exactTextsMismatch() {
    assertThatThrownBy(() -> {
      $$("#dropdown-list-container option").shouldHave(
        exactTexts("foo", "bar", "var", "buzz")
          .because("that's why")
      );
    })
      .isInstanceOf(TextsMismatch.class)
      .hasMessageStartingWith("Text #0 mismatch (expected: \"foo\", actual: \"@one.io\")")
      .hasMessageContaining("Actual (4): [@one.io, @two.eu, @three.com, @four.ee]")
      .hasMessageContaining("Expected (4): [foo, bar, var, buzz]")
      .hasMessageContaining("Because: that's why");
  }

  @Test
  void canExplainWhyConditionIsExpected_exactTextsSizeMismatch() {
    assertThatThrownBy(() -> {
      $$("#dropdown-list-container option").shouldHave(
        exactTexts("foo", "bar", "var, buzz").because("that's why"));
    })
      .isInstanceOf(TextsMismatch.class)
      .hasMessageStartingWith("List size mismatch (expected: 3, actual: 4)")
      .hasMessageContaining("Actual (4): [@one.io, @two.eu, @three.com, @four.ee]")
      .hasMessageContaining("Expected (3): [foo, bar, var, buzz]")
      .hasMessageContaining("Because: that's why")
      .hasMessageContaining("Collection: #dropdown-list-container option");
  }

  @Test
  void canExplainWhyConditionIsExpected_size() {
    assertThatThrownBy(() -> {
      $$("#radioButtons input").shouldHave(size(100).because("I expect many inputs"));
    })
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch: expected: = 100 (because I expect many inputs)," +
        " actual: 4, collection: #radioButtons input");
  }

  @Test
  void canExplainWhyConditionIsExpected_sizeGreaterThan() {
    assertThatThrownBy(() -> {
      $$("#radioButtons input").shouldHave(sizeGreaterThan(100).because("I expect many inputs"));
    })
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch: expected: > 100 (because I expect many inputs)," +
        " actual: 4, collection: #radioButtons input");
  }
}
