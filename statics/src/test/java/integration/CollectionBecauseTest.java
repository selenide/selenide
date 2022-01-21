package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.ex.TextsSizeMismatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class CollectionBecauseTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    Configuration.timeout = 10;
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
      .hasMessageStartingWith("Texts mismatch")
      .hasMessageContaining("Actual: [@livemail.ru, @myrambler.ru, @rusmail.ru, @мыло.ру]")
      .hasMessageContaining("Expected: [foo, bar, var, buzz]")
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
      .isInstanceOf(TextsSizeMismatch.class)
      .hasMessageStartingWith("Texts size mismatch")
      .hasMessageContaining("Actual: [@livemail.ru, @myrambler.ru, @rusmail.ru, @мыло.ру], List size: 4")
      .hasMessageContaining("Expected: [foo, bar, var, buzz], List size: 3")
      .hasMessageContaining("Because: that's why");
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
      .hasMessageStartingWith("Texts mismatch")
      .hasMessageContaining("Actual: [@livemail.ru, @myrambler.ru, @rusmail.ru, @мыло.ру]")
      .hasMessageContaining("Expected: [foo, bar, var, buzz]")
      .hasMessageContaining("Because: that's why");
  }

  @Test
  void canExplainWhyConditionIsExpected_exactTextsSizeMismatch() {
    assertThatThrownBy(() -> {
      $$("#dropdown-list-container option").shouldHave(
        exactTexts("foo", "bar", "var, buzz")
          .because("that's why")
      );
    })
      .isInstanceOf(TextsSizeMismatch.class)
      .hasMessageStartingWith("Texts size mismatch")
      .hasMessageContaining("Actual: [@livemail.ru, @myrambler.ru, @rusmail.ru, @мыло.ру], List size: 4")
      .hasMessageContaining("Expected: [foo, bar, var, buzz], List size: 3")
      .hasMessageContaining("Because: that's why");
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
