package integration;

import com.codeborne.selenide.TextMatchOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selectors.byPlaceholder;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.TextMatchOptions.partialText;

/**
 * Can find elements by placeholder
 */
final class FindByPlaceholderTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_labelled_inputs.html");
  }

  @Test
  void fullTextMatch() {
    $(byPlaceholder("Beneficiary name, e.g. John Smith")).shouldHave(value("Dear grandma"));
    $(byPlaceholder("To account, e.g. EE111222333444")).shouldHave(value("9999-888-77-6"));
  }

  @Test
  void partialTextMatch() {
    $(byPlaceholder("Beneficiary name, e.g.", partialText())).shouldHave(value("Dear grandma"));
    $(byPlaceholder("John Smith", partialText())).shouldHave(value("Dear grandma"));
    $(byPlaceholder("To account", partialText())).shouldHave(value("9999-888-77-6"));
    $(byPlaceholder("e.g. EE111222333444", partialText())).shouldHave(value("9999-888-77-6"));
  }

  @Test
  void trimWhitespaces() {
    TextMatchOptions config = partialText().trimWhitespaces();

    $(byPlaceholder("Beneficiary name", config)).shouldHave(value("Dear grandma"));
    $(byPlaceholder("Beneficiary  name", config)).shouldHave(value("Dear grandma"));
    $(byPlaceholder("    Beneficiary  name     ", config)).shouldHave(value("Dear grandma"));
    $(byPlaceholder("Beneficiaryname", config)).shouldNot(exist);
  }

  @Test
  void ignoreWhitespaces() {
    TextMatchOptions config = partialText().ignoreWhitespaces();

    $(byPlaceholder("Beneficiary name", config)).shouldHave(value("Dear grandma"));
    $(byPlaceholder("Beneficiaryname", config)).shouldHave(value("Dear grandma"));
    $(byPlaceholder("  Beneficiary  name  ", config)).shouldHave(value("Dear grandma"));
  }

  @Test
  void preserveWhitespaces() {
    TextMatchOptions config = partialText().preserveWhitespaces();

    $("form").$(byPlaceholder("Beneficiary name", config)).shouldHave(value("Dear grandma"));
    $("form").$(byPlaceholder("Beneficiaryname", config)).shouldNot(exist);
    $("form").$(byPlaceholder("Beneficiary   name", config)).shouldNot(exist);
    $("form").$(byPlaceholder(" Beneficiary name", config)).shouldNot(exist);
    $("form").$(byPlaceholder("Beneficiary name ", config)).shouldNot(exist);
  }
}
