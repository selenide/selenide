package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.ex.TextsMismatch;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Selenide.$$;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class CollectionBecauseTest extends IntegrationTest {
  @Before
  public void openTestPage() {
    Configuration.timeout = 10;
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  public void canExplainWhyConditionIsExpected_texts() {
    try {
      $$("#dropdown-list-container option").shouldHave(texts("foo", "bar").because("that's why"));
    } catch (TextsMismatch expected) {
      assertThat(expected.toString(), containsString("TextsMismatch \n" +
        "Actual: [@livemail.ru, @myrambler.ru, @rusmail.ru, @мыло.ру]\n" +
        "Expected: [foo, bar]\n" +
        "Because: that's why\n"));
    }
  }

  @Test
  public void canExplainWhyConditionIsExpected_exactTexts() {
    try {
      $$("#dropdown-list-container option").shouldHave(exactTexts("foo", "bar").because("that's why"));
    } catch (TextsMismatch expected) {
      assertThat(expected.toString(), containsString("TextsMismatch \n" +
        "Actual: [@livemail.ru, @myrambler.ru, @rusmail.ru, @мыло.ру]\n" +
        "Expected: [foo, bar]\n" +
        "Because: that's why\n"));
    }
  }

  @Test
  public void canExplainWhyConditionIsExpected_size() {
    try {
      $$("#radioButtons input").shouldHave(size(100).because("I expect many inputs"));
    } catch (ListSizeMismatch expected) {
      assertThat(expected.toString(), containsString(
        "ListSizeMismatch : expected: = 100 (because I expect many inputs), actual: 4, collection: #radioButtons input"));
    }
  }

  @Test
  public void canExplainWhyConditionIsExpected_sizeGreaterThan() {
    try {
      $$("#radioButtons input").shouldHave(sizeGreaterThan(100).because("I expect many inputs"));
    } catch (ListSizeMismatch expected) {
      assertThat(expected.toString(), containsString(
        "ListSizeMismatch : expected: > 100 (because I expect many inputs), actual: 4, collection: #radioButtons input"));
    }
  }
}
