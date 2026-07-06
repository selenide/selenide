package integration;

import com.codeborne.selenide.ex.ElementShould;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visibleText;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class VisibleTextTest extends ITest {
  private static final String FULL_TEXT = "987 654 321 100 100.876543321 AUTOOQODS";
  private static final String VISIBLE_PREFIX = "987 654 321 100 100.87";

  @BeforeEach
  void openPage() {
    openFile("page_with_partial_visible_text.html");
  }

  @Test
  void textConditionMatchesFullDomTextEvenWhenOverflowIsHidden() {
    withLongTimeout(() -> $("#partial").shouldHave(text(FULL_TEXT)));
  }

  @Test
  void exactTextConditionMatchesFullDomTextEvenWhenOverflowIsHidden() {
    withLongTimeout(() -> $("#partial").shouldHave(exactText(FULL_TEXT)));
  }

  @Test
  void visibleTextFailsWhenExpectedTextIsNotFullyVisible() {
    assertThatThrownBy(() -> withLongTimeout(() -> $("#partial").shouldHave(visibleText(FULL_TEXT))))
      .isInstanceOf(ElementShould.class);
  }

  @Test
  void visibleTextMatchesTruncatedPortion() {
    withLongTimeout(() -> $("#partial").shouldHave(visibleText(VISIBLE_PREFIX)));
  }

  @Test
  void visibleTextIsCaseInsensitive() {
    withLongTimeout(() -> $("#partial").shouldHave(visibleText(VISIBLE_PREFIX.toLowerCase())));
  }

  @Test
  void visibleTextMatchesFullyVisibleElement() {
    withLongTimeout(() -> $("#fully-visible").shouldHave(visibleText("Hello World")));
  }
}
