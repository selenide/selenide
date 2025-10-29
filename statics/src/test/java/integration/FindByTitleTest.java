package integration;

import com.codeborne.selenide.TextMatchOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.tagName;
import static com.codeborne.selenide.Selectors.byTitle;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.TextMatchOptions.fullText;
import static com.codeborne.selenide.TextMatchOptions.partialText;

/**
 * Can search element by title attribute
 */
final class FindByTitleTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void fullTextMatch() {
    $(byTitle("Login form")).shouldHave(tagName("fieldset"));
    $(byTitle("Login")).shouldNot(exist);
    $(byTitle("Login f")).shouldNot(exist);
  }

  @Test
  void partialTextMatch() {
    $(byTitle("Login f", partialText())).shouldHave(tagName("fieldset"));
    $(byTitle("LOGIN FOR", partialText().caseInsensitive())).shouldHave(tagName("fieldset"));
  }

  @Test
  void caseInsensitiveMatch() {
    $(byTitle("Login FORM", fullText().caseInsensitive())).shouldHave(tagName("fieldset"));
    $(byTitle("LOGIN form", partialText().caseInsensitive())).shouldHave(tagName("fieldset"));
    $(byTitle("loGIN fORM", partialText().caseInsensitive())).shouldHave(tagName("fieldset"));
  }

  @Test
  void ignoringWhitespaces() {
    TextMatchOptions config = fullText().ignoreWhitespaces();

    $(byTitle("Login   form", config)).shouldHave(tagName("fieldset"));
    $(byTitle("Loginform", config)).shouldHave(tagName("fieldset"));
    $(byTitle("  Login        form   ", config)).shouldHave(tagName("fieldset"));
  }

  @Test
  void preservingWhitespaces() {
    TextMatchOptions config = fullText().preserveWhitespaces();

    $(byTitle("Login form", config)).shouldHave(tagName("fieldset"));
    $(byTitle("  Login        form   ", config)).shouldNot(exist);
    $(byTitle("Login  form", config)).shouldNot(exist);
  }
}
