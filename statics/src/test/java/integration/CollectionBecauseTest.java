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
import static org.assertj.core.api.Assertions.assertThat;

final class CollectionBecauseTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    Configuration.timeout = 10;
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void canExplainWhyConditionIsExpected_textsMismatch() {
    try {
      $$("#dropdown-list-container option").shouldHave(texts("foo", "bar", "var", "buzz").because("that's why"));
    } catch (TextsMismatch expected) {
      assertThat(expected)
        .hasMessageStartingWith(String.format("Texts mismatch%n" +
          "Actual: [@livemail.ru, @myrambler.ru, @rusmail.ru, @мыло.ру]%n" +
          "Expected: [foo, bar, var, buzz]%n" +
          "Because: that's why%n"));
    }
  }

  @Test
  void canExplainWhyConditionIsExpected_textsSizeMismatch() {
    try {
      $$("#dropdown-list-container option").shouldHave(texts("foo", "bar", "var, buzz").because("that's why"));
    } catch (TextsSizeMismatch expected) {
      assertThat(expected)
        .hasMessageStartingWith(String.format("Texts size mismatch%n" +
          "Actual: [@livemail.ru, @myrambler.ru, @rusmail.ru, @мыло.ру], List size: 4%n" +
          "Expected: [foo, bar, var, buzz], List size: 3%n" +
          "Because: that's why%n"));
    }
  }

  @Test
  void canExplainWhyConditionIsExpected_exactTextsMismatch() {
    try {
      $$("#dropdown-list-container option").shouldHave(exactTexts("foo", "bar", "var", "buzz").because("that's why"));
    } catch (TextsMismatch expected) {
      assertThat(expected)
        .hasMessageStartingWith(String.format("Texts mismatch%n" +
          "Actual: [@livemail.ru, @myrambler.ru, @rusmail.ru, @мыло.ру]%n" +
          "Expected: [foo, bar, var, buzz]%n" +
          "Because: that's why%n"));
    }
  }

  @Test
  void canExplainWhyConditionIsExpected_exactTextsSizeMismatch() {
    try {
      $$("#dropdown-list-container option").shouldHave(exactTexts("foo", "bar", "var, buzz").because("that's why"));
    } catch (TextsSizeMismatch expected) {
      assertThat(expected)
        .hasMessageStartingWith(String.format("Texts size mismatch%n" +
          "Actual: [@livemail.ru, @myrambler.ru, @rusmail.ru, @мыло.ру], List size: 4%n" +
          "Expected: [foo, bar, var, buzz], List size: 3%n" +
          "Because: that's why%n"));
    }
  }

  @Test
  void canExplainWhyConditionIsExpected_size() {
    try {
      $$("#radioButtons input").shouldHave(size(100).because("I expect many inputs"));
    } catch (ListSizeMismatch expected) {
      assertThat(expected)
        .hasMessageStartingWith("List size mismatch: expected: = 100 (because I expect many inputs)," +
          " actual: 4, collection: #radioButtons input");
    }
  }

  @Test
  void canExplainWhyConditionIsExpected_sizeGreaterThan() {
    try {
      $$("#radioButtons input").shouldHave(sizeGreaterThan(100).because("I expect many inputs"));
    } catch (ListSizeMismatch expected) {
      assertThat(expected)
        .hasMessageStartingWith("List size mismatch: expected: > 100 (because I expect many inputs)," +
          " actual: 4, collection: #radioButtons input");
    }
  }
}
