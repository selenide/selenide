package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.name;
import static com.codeborne.selenide.Selectors.byTestId;
import static com.codeborne.selenide.Selenide.$;

/**
 * Can find elements by "data-test-id" attribute
 */
final class FindByTestIdTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_labelled_inputs.html");
  }

  @Test
  void findByTestId() {
    $(byTestId("beneficiary-account")).shouldHave(name("beneficiaryAccount"));
    $(byTestId("remitter-account")).shouldHave(name("remitterAccount"));
    $(byTestId("intermediate-account")).shouldNot(exist);
  }
}
