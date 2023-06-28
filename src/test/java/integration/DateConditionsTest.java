package integration;

import com.codeborne.selenide.ex.ElementShould;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.codeborne.selenide.SetValueOptions.withDate;
import static com.codeborne.selenide.conditions.datetime.DateConditions.date;
import static com.codeborne.selenide.conditions.datetime.DateConditions.dateBetween;
import static com.codeborne.selenide.conditions.datetime.DateConditions.dateFormat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class DateConditionsTest extends ITest {
  private final LocalDate birthday = LocalDate.of(1993, 12, 30);

  @BeforeEach
  void openTestPage() {
    openFile("page_with_date_inputs.html");
  }

  @Test
  public void successCases() {
    $("#birthdate").shouldHave(date(birthday, "yyyy-MM-dd"));
    $("#birthdate").shouldHave(date(birthday));
    $("#birthdate").shouldHave(dateBetween(birthday.minusDays(1), birthday, "yyyy-MM-dd"));
    $("#birthdate").shouldHave(dateFormat("yyyy-MM-dd"));
  }

  @Test
  void validateChangedDateTime() {
    $("#birthdate").setValue(withDate(LocalDate.of(1981, 6, 8)));
    $("#birthdate").shouldHave(date(LocalDate.of(1981, 6, 8), "yyyy-MM-dd"));
  }

  @Test
  public void date_failure_wrongFormat() {
    assertThatThrownBy(() -> $("#birthdate").shouldHave(date(birthday, "yyyy/MM/dd")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have date value \"1993/12/30\" {#birthdate}")
      .hasMessageContaining("Element: '<input id=\"birthdate\" type=\"date\" value=\"1993-12-30\"></input>'")
      .hasMessageContaining("Actual value: 1993-12-30");
  }

  @Test
  public void dateBetween_failure_wrongFormat() {
    assertThatThrownBy(() -> $("#birthdate").shouldHave(dateBetween(birthday.minusYears(1), birthday.plusYears(1), "yyyy/MM/dd")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have date between \"1992/12/30\" and \"1994/12/30\" {#birthdate}")
      .hasMessageContaining("Element: '<input id=\"birthdate\" type=\"date\" value=\"1993-12-30\"></input>'")
      .hasMessageContaining("Actual value: 1993-12-30");
  }

  @Test
  public void date_failure() {
    assertThatThrownBy(() -> $("#birthdate").shouldHave(date(LocalDate.of(2022, 11, 22), "yyyy-MM-dd")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have date value \"2022-11-22\" {#birthdate}")
      .hasMessageContaining("Element: '<input id=\"birthdate\" type=\"date\" value=\"1993-12-30\"></input>'")
      .hasMessageContaining("Actual value: 1993-12-30");
  }

  @Test
  public void dateBetween_failure() {
    assertThatThrownBy(() ->
      $("#birthdate").shouldHave(dateBetween(LocalDate.of(2022, 10, 12), LocalDate.of(2022, 10, 13), "yyyy-MM-dd"))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have date between \"2022-10-12\" and \"2022-10-13\" {#birthdate}")
      .hasMessageContaining("Element: '<input id=\"birthdate\" type=\"date\" value=\"1993-12-30\"></input>'")
      .hasMessageContaining("Actual value: 1993-12-30");
  }

  @Test
  public void dateFormat_failure() {
    assertThatThrownBy(() -> $("#birthdate").shouldHave(dateFormat("yyyy/MM/dd")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have date format \"yyyy/MM/dd\" {#birthdate}")
      .hasMessageContaining("Element: '<input id=\"birthdate\" type=\"date\" value=\"1993-12-30\"></input>'")
      .hasMessageContaining("Actual value: 1993-12-30");
  }
}
