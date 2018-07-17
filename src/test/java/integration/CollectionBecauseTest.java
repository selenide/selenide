package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.ex.TextsMismatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Selenide.$$;

class CollectionBecauseTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    Configuration.timeout = 10;
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void canExplainWhyConditionIsExpected_texts() {
    try {
      $$("#dropdown-list-container option").shouldHave(texts("foo", "bar").because("that's why"));
    } catch (TextsMismatch expected) {

      assertThat(expected.toString())
        .contains("TextsMismatch \n" +
          "Actual: [@livemail.ru, @myrambler.ru, @rusmail.ru, @мыло.ру]\n" +
          "Expected: [foo, bar]\n" +
          "Because: that's why\n");
    }
  }

  @Test
  void canExplainWhyConditionIsExpected_exactTexts() {
    try {
      $$("#dropdown-list-container option").shouldHave(exactTexts("foo", "bar").because("that's why"));
    } catch (TextsMismatch expected) {
      assertThat(expected.toString())
        .contains("TextsMismatch \n" +
          "Actual: [@livemail.ru, @myrambler.ru, @rusmail.ru, @мыло.ру]\n" +
          "Expected: [foo, bar]\n" +
          "Because: that's why\n");
    }
  }

  @Test
  void canExplainWhyConditionIsExpected_size() {
    try {
      $$("#radioButtons input").shouldHave(size(100).because("I expect many inputs"));
    } catch (ListSizeMismatch expected) {
      assertThat(expected.toString())
        .contains("ListSizeMismatch : expected: = 100 (because I expect many inputs), actual: 4, collection: #radioButtons input");
    }
  }

  @Test
  void canExplainWhyConditionIsExpected_sizeGreaterThan() {
    try {
      $$("#radioButtons input").shouldHave(sizeGreaterThan(100).because("I expect many inputs"));
    } catch (ListSizeMismatch expected) {
      assertThat(expected.toString())
        .contains("ListSizeMismatch : expected: > 100 (because I expect many inputs), actual: 4, collection: #radioButtons input");
    }
  }
}
