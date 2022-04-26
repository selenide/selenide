package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.SetValueMethod.JS;
import static com.codeborne.selenide.SetValueOptions.withDate;

final class SetDateValueTest extends IntegrationTest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_date_input.html");
  }

  @Test
  void canSetDateValue_sendKeys() {
    LocalDate birthday = LocalDate.parse("1979-12-31");
    $("#birthday").setValue(withDate(birthday));
    $("#user-summary").shouldHave(text("User birthday: 1979-12-31"));
  }

  @Test
  void canSetDateValue_usingJavascript() {
    LocalDate birthday = LocalDate.parse("1978-11-30");
    $("#birthday").setValue(withDate(birthday).usingMethod(JS));
    $("#user-summary").shouldHave(text("User birthday: 1978-11-30"));
  }
}
