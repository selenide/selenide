package integration;

import com.codeborne.selenide.ex.ElementShould;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactVisibleText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visibleText;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

final class VisibleTextTest extends ITest {
  private static final String FULL_TEXT = "987 654 321 100 100.876543321 AUTOOQODS";
  private static final String VISIBLE_PREFIX = "987 654 321 100 100";

  @BeforeEach
  void openPage() {
    openFile("page_with_partial_visible_text.html");
  }

  @Test
  void textConditionMatchesFullDomTextEvenWhenOverflowIsHidden() {
    $("#partial").shouldHave(text(FULL_TEXT));
    $("#partial").shouldHave(exactText(FULL_TEXT));
  }

  @Test
  void visibleTextFailsWhenExpectedTextIsNotFullyVisible() {
    assumeThat(browser().isHtmlUnit()).isFalse();
    assertThatThrownBy(() -> $("#partial").shouldHave(visibleText(FULL_TEXT)))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have visible text \"%s\" {#partial}", FULL_TEXT)
      .hasMessageContaining("Actual value: text=\"%s\"", VISIBLE_PREFIX);
  }

  @Test
  void visibleTextMatchesTruncatedPortion() {
    $("#partial").shouldHave(visibleText(VISIBLE_PREFIX));
    $("#partial").shouldHave(visibleText("654 321"));
  }

  @Test
  void visibleTextIsCaseInsensitive() {
    assumeThat(browser().isHtmlUnit()).isFalse();
    $("#fully-visible").shouldHave(visibleText("hello world"));
    $("#fully-visible").shouldHave(exactVisibleText("HELLO WORLD"));
  }

  @Test
  void exactVisibleTextExcludesCharactersReplacedByEllipsis() {
    assumeThat(browser().isHtmlUnit()).isFalse();
    $("#partial").shouldHave(exactVisibleText(VISIBLE_PREFIX));
  }

  @Test
  void exactVisibleTextOfElementInsideClippedAncestor() {
    assumeThat(browser().isHtmlUnit()).isFalse();
    $("#field_value").shouldHave(exactVisibleText(VISIBLE_PREFIX));
  }

  @Test
  void exactVisibleTextFailsWhenExpectedTextIsNotFullyVisible() {
    assumeThat(browser().isHtmlUnit()).isFalse();
    assertThatThrownBy(() -> $("#partial").shouldHave(exactVisibleText(FULL_TEXT)))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have exact visible text \"%s\" {#partial}", FULL_TEXT)
      .hasMessageContaining("Actual value: text=\"%s\"", VISIBLE_PREFIX);
  }

  @Test
  void overflowHiddenWithoutEllipsis() {
    assumeThat(browser().isHtmlUnit()).isFalse();
    $("#clipped").shouldHave(exactVisibleText("0123456789abcdefghij"));
  }

  @Test
  void respectsStylesOfNestedElements() {
    assumeThat(browser().isHtmlUnit()).isFalse();
    $("#nested-styles").shouldHave(exactVisibleText("0123456789ABCD"));
  }

  @Test
  void respectsCssRulesWithAncestorSelectors() {
    assumeThat(browser().isHtmlUnit()).isFalse();
    $("#ancestor-scoped-css").shouldHave(exactVisibleText("012345678"));
  }

  @Test
  void rightToLeftTextIsTruncatedOnTheLeftSide() {
    assumeThat(browser().isHtmlUnit()).isFalse();
    $("#rtl").shouldHave(exactVisibleText("hijklmnopqrstuvwxyz"));
  }

  @Test
  void excludesLinesHiddenByVerticalOverflow() {
    assumeThat(browser().isHtmlUnit()).isFalse();
    $("#multiline").shouldHave(exactVisibleText("aaaa bbbb cccc dddd"));
  }

  @Test
  void excludesHiddenChildren() {
    assumeThat(browser().isHtmlUnit()).isFalse();
    $("#with-hidden-parts").shouldHave(exactVisibleText("Hello World"));
  }

  @Test
  void lineBreakSeparatesWords() {
    $("#with-line-break").shouldHave(exactVisibleText("Hello World"));
  }

  @Test
  void exactVisibleTextRejectsPartialMatchOnFullyVisibleElement() {
    assumeThat(browser().isHtmlUnit()).isFalse();
    assertThatThrownBy(() -> $("#fully-visible").shouldHave(exactVisibleText("Hello")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have exact visible text \"Hello\" {#fully-visible}")
      .hasMessageContaining("Actual value: text=\"Hello World\"");
  }

  @Test
  void exactVisibleTextMatchesFullyVisibleElement() {
    assumeThat(browser().isHtmlUnit()).isFalse();
    $("#fully-visible").shouldHave(exactVisibleText("Hello World"));
  }
}
