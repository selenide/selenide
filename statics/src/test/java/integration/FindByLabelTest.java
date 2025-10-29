package integration;

import com.codeborne.selenide.TextMatchOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.attributes;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selectors.byLabel;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
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
    $(byLabel("Recipient name", fullText())).shouldHave(value("Dear grandma"));
    $(byLabel("Recipient", fullText().ignoreWhitespaces())).shouldNot(exist);
    $(byLabel("Payment  Date", fullText())).shouldHave(value("01.12.2024"));
    $(byLabel("Payment", fullText())).shouldNot(exist);
  }

  @Test
  void ignoreWhitespaces() {
    TextMatchOptions config = fullText().ignoreWhitespaces();

    $(byLabel("Recipient name", config)).shouldHave(value("Dear grandma"));
    $(byLabel("Recipientname", config)).shouldHave(value("Dear grandma"));
    $(byLabel("   Recipient  name ", config)).shouldHave(value("Dear grandma"));
  }

  @Test
  void preserveWhitespaces() {
    TextMatchOptions config = fullText().preserveWhitespaces();

    $(byLabel("Recipient name%n          ".formatted(), config)).shouldHave(value("Dear grandma"));
    $(byLabel("Recipient name", config)).shouldNot(exist);
    $(byLabel("To account", config)).shouldHave(value("9999-888-77-6"));
    $(byLabel("To  account", config)).shouldNot(exist);
  }

  @Test
  void partialTextMatch() {
    $(byLabel("Recipient name", partialText())).should(exist);
    $(byLabel("Recipient", partialText())).should(exist);
    $(byLabel("Recip", partialText())).should(exist);
    $(byLabel("nt name", partialText())).should(exist);
    $(byLabel("Payment Da", partialText())).shouldHave(value("01.12.2024"));
  }

  @Test
  void canFindMultipleElementsByLabel() {
    $$(byLabel("payment Date", fullText().caseInsensitive())).shouldHave(size(5), attributes("value",
      "01.12.2024", "02.12.2024", "03.12.2024", "04.12.2024", "05.12.2024")
    );
    $$(byLabel("Payment D", partialText())).shouldHave(size(2), attributes("value",
      "01.12.2024", "05.12.2024")
    );
    $$(byLabel("Payment D", partialText().caseInsensitive())).shouldHave(size(5), attributes("value",
      "01.12.2024", "02.12.2024", "03.12.2024", "04.12.2024", "05.12.2024")
    );
    $$(byLabel("Payment Date")).shouldHave(size(2), attributes("value", "01.12.2024", "05.12.2024"));
    $$(byLabel("Payment Date", fullText().preserveWhitespaces())).shouldHave(size(1), attributes("value", "01.12.2024"));
  }

  @Test
  void canFindInsideElement() {
    $("form").$$(byLabel("To account"))
      .shouldHave(size(1), attributes("value", "9999-888-77-6"));
  }
}
