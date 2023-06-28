package com.codeborne.selenide.conditions.datetime;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.time.LocalDateTime;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class DateTimeBetweenTest {
  private final Driver driver = mock();
  private final WebElement element = mock();
  private final DateTimeFormatCondition formatCondition = mock();
  private final LocalDateTime start = LocalDateTime.of(2022, 10, 9, 10, 10, 10);
  private final LocalDateTime end = LocalDateTime.of(2022, 10, 12, 10, 10, 10);
  private final DateTimeBetween condition = new DateTimeBetween(start, end, formatCondition);

  @Test
  void correctDateValueWithCorrectFormat() {
    when(formatCondition.check(any(), any())).thenReturn(new CheckResult(ACCEPT, LocalDateTime.of(2022, 10, 11, 10, 10, 10)));
    when(formatCondition.format(any())).thenReturn("formatted date");

    CheckResult check = condition.check(driver, element);

    assertThat(check.verdict()).isEqualTo(ACCEPT);
    assertThat(check.actualValue()).isEqualTo("formatted date");

    verify(formatCondition).check(driver, element);
    verify(formatCondition).format(LocalDateTime.of(2022, 10, 11, 10, 10, 10));
  }

  @Test
  void returnFormatConditionCheckResultInCaseOfRejectedVerdict() {
    CheckResult value = new CheckResult(REJECT, "some value");
    when(formatCondition.check(any(), any())).thenReturn(value);

    assertThat(condition.check(driver, element)).isSameAs(value);

    verify(formatCondition).check(driver, element);
  }

  @Test
  void incorrectDateValue() {
    when(formatCondition.check(any(), any())).thenReturn(new CheckResult(ACCEPT, LocalDateTime.of(2021, 10, 12, 10, 10, 10)));
    when(formatCondition.format(any())).thenReturn("formatted date");

    CheckResult check = condition.check(driver, element);

    assertThat(check.verdict()).isEqualTo(REJECT);
    assertThat(check.actualValue()).isEqualTo("formatted date");

    verify(formatCondition).check(driver, element);
    verify(formatCondition).format(LocalDateTime.of(2021, 10, 12, 10, 10, 10));
  }

  @Test
  void stringRepresentation() {
    assertThat(new DateTimeBetween(start, end, "yyyy/MM/dd HH:mm:ss"))
      .hasToString("datetime value between \"2022/10/09 10:10:10\" and \"2022/10/12 10:10:10\"");
  }

  @Test
  void throwExceptionIfStartDateIsEqualToEndDate() {
    LocalDateTime date = LocalDateTime.of(2022, 10, 11, 10, 10, 10);
    assertThatThrownBy(() -> new DateTimeBetween(date, date, formatCondition))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("from (2022-10-11T10:10:10) must be before until (2022-10-11T10:10:10)");
  }

  @Test
  void throwExceptionIfStartDateAfterEndDate() {
    LocalDateTime startDate = LocalDateTime.of(2022, 10, 11, 10, 10, 10);
    LocalDateTime endDate = LocalDateTime.of(2021, 10, 11, 10, 10, 10);
    assertThatThrownBy(() -> new DateTimeBetween(startDate, endDate, formatCondition))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("from (2022-10-11T10:10:10) must be before until (2021-10-11T10:10:10)");
  }
}
