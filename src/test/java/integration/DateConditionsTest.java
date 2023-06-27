package integration;

import com.codeborne.selenide.ex.ElementShould;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.codeborne.selenide.Condition.date;
import static com.codeborne.selenide.conditions.datetime.DateConditionOptions.between;
import static com.codeborne.selenide.conditions.datetime.DateConditionOptions.eq;
import static com.codeborne.selenide.conditions.datetime.DateConditionOptions.withFormat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class DateConditionsTest extends ITest {

  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_date_inputs.html");
  }

  @Test
  public void successCases() {
    LocalDate birthday = LocalDate.of(2022, 10, 11);
    $("#birthdate").shouldHave(date(eq(birthday, "yyyy/MM/dd")));
    $("#birthdate").shouldHave(date(eq(birthday).format("yyyy/MM/dd")));
    $("#birthdate").shouldHave(date(between(birthday.minusDays(1), birthday, "yyyy/MM/dd")));
    $("#birthdate").shouldHave(date(withFormat("yyyy/MM/dd")));
  }

  @Test
  public void failure_wrongDates() {
    assertThatThrownBy(() -> $("#birthdate").shouldHave(date(eq(LocalDate.of(2022, 11, 11), "yyyy/MM/dd"))))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("""
        Element should have date value: "2022/11/11" (with date value format: "yyyy/MM/dd") {#birthdate}
        """)
      .hasMessageContaining("""
        Element: '<input id="birthdate" type="text" value="2022/10/11"></input>'
        """)
      .hasMessageContaining("Actual value: 2022/10/11");

    assertThatThrownBy(() ->
      $("#birthdate").shouldHave(date(between(LocalDate.of(2022, 10, 12), LocalDate.of(2022, 10, 13), "yyyy/MM/dd")))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("""
        Element should have date value between ["2022/10/12", "2022/10/13"] (with date value format: "yyyy/MM/dd") {#birthdate}
        """)
      .hasMessageContaining("""
        Element: '<input id="birthdate" type="text" value="2022/10/11"></input>'
        """)
      .hasMessageContaining("Actual value: 2022/10/11");
  }

  @Test
  public void failure_wrongDateFormat() {
    assertThatThrownBy(() ->
      $("#birthdate").shouldHave(date(eq(LocalDate.of(2022, 11, 11), "yyyy-MM-dd")))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("""
        Element should have date value: "2022-11-11" (with date value format: "yyyy-MM-dd") {#birthdate}
        """)
      .hasMessageContaining("""
        Element: '<input id="birthdate" type="text" value="2022/10/11"></input>'
        """)
      .hasMessageContaining("Actual value: 2022/10/11");

    assertThatThrownBy(() ->
      $("#birthdate").shouldHave(date(between(LocalDate.of(2022, 10, 12), LocalDate.of(2022, 10, 13), "yyyy-MM-dd")))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("""
        Element should have date value between ["2022-10-12", "2022-10-13"] (with date value format: "yyyy-MM-dd") {#birthdate}
        """)
      .hasMessageContaining("""
        Element: '<input id="birthdate" type="text" value="2022/10/11"></input>'
        """)
      .hasMessageContaining("Actual value: 2022/10/11");

    assertThatThrownBy(() -> $("#birthdate").shouldHave(date(withFormat("yyyy-MM-dd"))))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("""
        Element should have date value format: "yyyy-MM-dd" {#birthdate}
        """)
      .hasMessageContaining("""
        Element: '<input id="birthdate" type="text" value="2022/10/11"></input>'
        """)
      .hasMessageContaining("Actual value: 2022/10/11");
  }
}
