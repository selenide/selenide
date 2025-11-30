package integration;

import com.codeborne.selenide.ex.ElementShould;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.codeborne.selenide.SetValueOptions.withDateTime;
import static com.codeborne.selenide.conditions.datetime.DateTimeConditions.dateTime;
import static com.codeborne.selenide.conditions.datetime.DateTimeConditions.dateTimeBetween;
import static com.codeborne.selenide.conditions.datetime.DateTimeConditions.dateTimeFormat;
import static java.time.Month.AUGUST;
import static java.time.Month.DECEMBER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class DateTimeConditionsTest extends ITest {
  private final LocalDateTime birthday = LocalDateTime.of(1983, DECEMBER, 26, 23, 49, 59);
  private final LocalDateTime judgmentDay = LocalDateTime.of(1997, AUGUST, 29, 2, 14, 0);

  @BeforeEach
  void openTestPage() {
    openFile("page_with_datetime_inputs.html");
  }

  @Test
  public void canCheckValueAttribute() {
    $("#birthdate").shouldHave(dateTime(birthday));
    $("#birthdate").shouldHave(dateTime(birthday, "yyyy-MM-dd'T'HH:mm:ss"));
    $("#birthdate").shouldHave(dateTimeBetween(birthday.minusDays(1), birthday.plusDays(1), "yyyy-MM-dd'T'HH:mm:ss"));
    $("#birthdate").shouldHave(dateTimeFormat("yyyy-MM-dd'T'HH:mm:ss"));
  }

  @Test
  public void canCheckText() {
    $("#judgment-day #iso").shouldHave(dateTime(judgmentDay));
    $("#judgment-day #custom").shouldHave(dateTime(judgmentDay, "MMMM d, yyyy 'at' H:mm"));
    $("#judgment-day #iso").shouldHave(dateTimeBetween(judgmentDay.minusMinutes(2), judgmentDay.plusHours(1)));
    $("#judgment-day #mm-dd").shouldHave(dateTimeFormat("yyyy/MM/dd HH:mm"));
    $("#judgment-day #mm-dd").shouldHave(dateTime(judgmentDay, "yyyy/MM/dd HH:mm"));
  }

  @Test
  void dateCustomFormat() {
    LocalDateTime expected = LocalDateTime.of(2018, 10, 28, 14, 45);
    $("#issueDate").shouldHave(dateTime(expected, "yyyy/MM/dd HH:mm"));
    $("#issueDate").shouldHave(dateTimeBetween(expected.minusHours(1), expected.plusHours(1), "yyyy/MM/dd HH:mm"));
    $("#issueDate").shouldHave(dateTimeFormat("yyyy/MM/dd HH:mm"));
  }

  @Test
  void validateChangedDateTime() {
    LocalDateTime anotherBirthday = LocalDateTime.of(1981, 6, 8, 19, 43, 56);
    $("#birthdate").setValue(withDateTime(anotherBirthday));
    $("#birthdate").shouldHave(dateTime(anotherBirthday, "yyyy-MM-dd'T'HH:mm:ss"));
  }

  @Test
  public void dateTime_failure() {
    assertThatThrownBy(() -> $("#birthdate").shouldHave(dateTime(birthday.minusDays(1), "yyyy-MM-dd'T'HH:mm:ss")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have datetime value \"1983-12-25T23:49:59\" {#birthdate}")
      .hasMessageContaining("Element: '<input id=\"birthdate\" type=\"datetime-local\" value=\"1983-12-26T23:49:59\"></input>'")
      .hasMessageContaining("Actual value: 1983-12-26T23:49:59");
  }

  @Test
  public void dateTimeBetween_failure() {
    String anotherFormat = "yyyy/MM/dd HH:mm:ss";
    assertThatThrownBy(() -> $("#birthdate").shouldHave(dateTimeBetween(birthday.minusDays(2), birthday.minusDays(1), anotherFormat)))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have " +
                              "datetime value between \"1983/12/24 23:49:59\" and \"1983/12/25 23:49:59\" {#birthdate}")
      .hasMessageContaining("Element: '<input id=\"birthdate\" type=\"datetime-local\" value=\"1983-12-26T23:49:59\"></input>'")
      .hasMessageContaining("Actual value: 1983-12-26T23:49:59");
  }

  @Test
  public void dateTime_failure_wrongFormat() {
    assertThatThrownBy(() -> $("#birthdate").shouldHave(dateTime(birthday, "MM/dd/yyyy HH:mm:ss")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have datetime value \"12/26/1983 23:49:59\" {#birthdate}")
      .hasMessageContaining("Element: '<input id=\"birthdate\" type=\"datetime-local\" value=\"1983-12-26T23:49:59\"></input>'")
      .hasMessageContaining("Actual value: 1983-12-26T23:49:59");
  }

  @Test
  public void dateTimeBetween_failure_wrongFormat() {
    String anotherFormat = "yyyy/MM/dd HH:mm:ss";
    assertThatThrownBy(() -> $("#birthdate").shouldHave(dateTimeBetween(birthday.minusDays(1), birthday.plusDays(1), anotherFormat)))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have " +
                              "datetime value between \"1983/12/25 23:49:59\" and \"1983/12/27 23:49:59\" {#birthdate}")
      .hasMessageContaining("Element: '<input id=\"birthdate\" type=\"datetime-local\" value=\"1983-12-26T23:49:59\"></input>'")
      .hasMessageContaining("Actual value: 1983-12-26T23:49:59");
  }

  @Test
  public void dateTimeFormat_failure_wrongFormat() {
    assertThatThrownBy(() -> $("#birthdate").shouldHave(dateTimeFormat("yyyy/MM/dd HH:mm:ss")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have datetime format \"yyyy/MM/dd HH:mm:ss\" {#birthdate}")
      .hasMessageContaining("Element: '<input id=\"birthdate\" type=\"datetime-local\" value=\"1983-12-26T23:49:59\"></input>'")
      .hasMessageContaining("Actual value: 1983-12-26T23:49:59");
  }
}
