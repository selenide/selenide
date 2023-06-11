package integration;

import com.codeborne.selenide.ex.ElementShould;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.codeborne.selenide.Condition.datetime;
import static com.codeborne.selenide.conditions.datetime.DateTimeConditionOptions.between;
import static com.codeborne.selenide.conditions.datetime.DateTimeConditionOptions.eq;
import static com.codeborne.selenide.conditions.datetime.DateTimeConditionOptions.withFormat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class DateTimeConditionsTest extends ITest {

  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_datetime_inputs.html");
  }

  @Test
  public void successCases() {
    LocalDateTime birthday = LocalDateTime.of(2022, 10, 11, 12, 13, 14);
    $("#birthdate").shouldHave(datetime(eq(birthday, "yyyy/MM/dd HH:mm:ss")));
    $("#birthdate").shouldHave(datetime(eq(birthday).format("yyyy/MM/dd HH:mm:ss")));
    $("#birthdate").shouldHave(datetime(between(birthday.minusDays(1), birthday.plusDays(1), "yyyy/MM/dd HH:mm:ss")));
    $("#birthdate").shouldHave(datetime(between(birthday.minusDays(1), birthday.plusDays(1)).format("yyyy/MM/dd HH:mm:ss")));
    $("#birthdate").shouldHave(datetime(withFormat("yyyy/MM/dd HH:mm:ss")));
  }

  @Test
  public void failure_wrongDateTime() {
    LocalDateTime birthday = LocalDateTime.of(2022, 10, 11, 12, 13, 14);

    assertThatThrownBy(() ->
      $("#birthdate").shouldHave(datetime(eq(birthday.minusDays(1), "yyyy/MM/dd HH:mm:ss")))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("""
        Element should have datetime value: "2022/10/10 12:13:14" (with datetime value format: "yyyy/MM/dd HH:mm:ss") {#birthdate}
        """)
      .hasMessageContaining("""
        Element: '<input id="birthdate" type="text" value="2022/10/11 12:13:14"></input>'
        """)
      .hasMessageContaining("Actual value: 2022/10/11 12:13:14");

    assertThatThrownBy(() ->
      $("#birthdate").shouldHave(datetime(between(birthday.minusDays(2), birthday.minusDays(1), "yyyy/MM/dd HH:mm:ss")))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("""
        Element should have datetime value between ["2022/10/09 12:13:14", "2022/10/10 12:13:14"] \
        (with datetime value format: "yyyy/MM/dd HH:mm:ss") {#birthdate}
        """
      )
      .hasMessageContaining("""
        Element: '<input id="birthdate" type="text" value="2022/10/11 12:13:14"></input>'
        """)
      .hasMessageContaining("Actual value: 2022/10/11 12:13:14");
  }

  @Test
  public void failure_wrongDateTimeFormat() {
    LocalDateTime birthday = LocalDateTime.of(2022, 10, 11, 12, 13, 14);
    assertThatThrownBy(() ->
      $("#birthdate").shouldHave(datetime(eq(birthday, "yyyy-MM-dd HH:mm:ss")))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("""
        Element should have datetime value: "2022-10-11 12:13:14" (with datetime value format: "yyyy-MM-dd HH:mm:ss") {#birthdate}
        """)
      .hasMessageContaining("""
        Element: '<input id="birthdate" type="text" value="2022/10/11 12:13:14"></input>'
        """)
      .hasMessageContaining("Actual value: 2022/10/11 12:13:14");

    assertThatThrownBy(() ->
      $("#birthdate").shouldHave(datetime(between(birthday.minusDays(1), birthday.plusDays(1), "yyyy-MM-dd HH:mm:ss")))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("""
        Element should have datetime value between ["2022-10-10 12:13:14", "2022-10-12 12:13:14"] \
        (with datetime value format: "yyyy-MM-dd HH:mm:ss") {#birthdate}
        """)
      .hasMessageContaining("""
        Element: '<input id="birthdate" type="text" value="2022/10/11 12:13:14"></input>'
        """)
      .hasMessageContaining("Actual value: 2022/10/11 12:13:14");

    assertThatThrownBy(() ->
      $("#birthdate").shouldHave(datetime(withFormat("yyyy-MM-dd HH:mm:ss")))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("""
        Element should have datetime value format: "yyyy-MM-dd HH:mm:ss" {#birthdate}
        """)
      .hasMessageContaining("""
        Element: '<input id="birthdate" type="text" value="2022/10/11 12:13:14"></input>'
        """)
      .hasMessageContaining("Actual value: 2022/10/11 12:13:14");
  }
}
