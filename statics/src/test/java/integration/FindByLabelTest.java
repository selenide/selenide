package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selectors.byLabel;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.TextMatchOptions.fullText;
import static com.codeborne.selenide.TextMatchOptions.partialText;

final class FindByLabelTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_labelled_inputs.html");
  }

  @Test
  void canFindInputByLabel() {
    $(byLabel("Recipient name")).shouldHave(value("Dear grandma"));
    $(byLabel("To account")).shouldHave(value("9999-888-77-6"));
    $(byLabel("From account")).shouldHave(value("1111-222-3-6666"));
  }

  @Test
  void fullTextMatch() {
    $(byLabel("Recipient name", fullText())).should(exist);
    $(byLabel("Recipient", fullText().ignoreWhitespaces())).shouldNot(exist);
  }

  @Test
  void partialTextMatch() {
    $(byLabel("Recipient name", partialText())).should(exist);
    $(byLabel("Recipient", partialText())).should(exist);
    $(byLabel("Recip", partialText())).should(exist);
    $(byLabel("nt name", partialText())).should(exist);
  }
}
