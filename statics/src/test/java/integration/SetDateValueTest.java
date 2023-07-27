package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.SetValueMethod.JS;
import static com.codeborne.selenide.SetValueOptions.withDate;
import static com.codeborne.selenide.SetValueOptions.withDateTime;
import static com.codeborne.selenide.SetValueOptions.withTime;
import static com.codeborne.selenide.conditions.datetime.DateTimeConditions.dateTime;

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

  @Test
  void canSetDateTime() {
    openFile("page_with_datetime_inputs.html");
    $("#birthdate").setValue(withDateTime(LocalDateTime.of(1981, 6, 8, 23, 58, 59)));
    $("#birthdate").shouldHave(exactValue("1981-06-08T23:58:59"));
    $("#birthdate").shouldHave(dateTime(LocalDateTime.of(1981, 6, 8, 23, 58, 59)));
  }

  @Test
  void canSetTime() {
    openFile("page_with_datetime_inputs.html");
    $("#open").setValue(withTime(LocalTime.of(7, 12)));
    $("#close").setValue(withTime(LocalTime.of(17, 59, 58)));
    $("#open").shouldHave(exactValue("07:12:00.000"));
    $("#close").shouldHave(exactValue("17:59:58.000"));
  }
}
