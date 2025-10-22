package integration;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.TextMatchOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selectors.byAltText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.TextMatchOptions.fullText;
import static com.codeborne.selenide.TextMatchOptions.partialText;

/**
 * Can search element by title attribute
 */
final class FindByAltTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_images.html");
  }

  @Test
  void fullTextMatch() {
    $(byAltText("Selenide logo")).shouldHave(cssClass("selenide-logo"));
    $(byAltText("Imaginary logo")).shouldHave(cssClass("imaginary-logo"));
    $(byAltText("logo")).shouldNot(exist);
    $(byAltText("Selenide")).shouldNot(exist);
  }

  @Test
  void partialTextMatch() {
    $(byAltText("Selenide ", partialText())).shouldHave(cssClass("selenide-logo"));
    $(byAltText("Selenide lo", partialText())).shouldHave(cssClass("selenide-logo"));
    $(byAltText("ide lo", partialText())).shouldHave(cssClass("selenide-logo"));

    $$(byAltText(" logo", partialText())).shouldHave(
      size(2),
      CollectionCondition.attributes("class", "selenide-logo", "imaginary-logo")
    );
  }

  @Test
  void caseInsensitiveMatch() {
    $(byAltText("selenide logo", fullText().caseInsensitive())).shouldHave(cssClass("selenide-logo"));
    $(byAltText("SELENIDE LOGO", partialText().caseInsensitive())).shouldHave(cssClass("selenide-logo"));
    $(byAltText("selenIDE loGO", partialText().caseInsensitive())).shouldHave(cssClass("selenide-logo"));
  }

  @Test
  void ignoringWhitespaces() {
    TextMatchOptions config = fullText().ignoreWhitespaces();

    $(byAltText("Selenide logo", config)).shouldHave(cssClass("selenide-logo"));
    $(byAltText("Selenidelogo", config)).shouldHave(cssClass("selenide-logo"));
    $(byAltText("       Selenide   logo       ", config)).shouldHave(cssClass("selenide-logo"));
  }

  @Test
  void preservingWhitespaces() {
    TextMatchOptions config = fullText().preserveWhitespaces();

    $(byAltText("Selenide logo", config)).shouldHave(cssClass("selenide-logo"));
    $(byAltText("Selenidelogo", config)).shouldNot(exist);
    $(byAltText("       Selenide   logo       ", config)).shouldNot(exist);
  }
}
