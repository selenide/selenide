package com.codeborne.selenide.conditions;

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

final class LocalDateTimeBetweenConditionTest {
  private final Driver driver = mock();
  private final WebElement element = mock();
  private final LocalDateTimeFormatCondition formatCondition = mock();
  private final LocalDateTime start = LocalDateTime.of(2022, 10, 9, 10, 10, 10);
  private final LocalDateTime end = LocalDateTime.of(2022, 10, 12, 10, 10, 10);
  private final LocalDateTimeBetweenCondition condition = new LocalDateTimeBetweenCondition(start, end, formatCondition);

  @Test
  void correctDateValueWithCorrectFormat() {
    when(formatCondition.check(any(), any())).thenReturn(new CheckResult(ACCEPT, LocalDateTime.of(2022, 10, 11, 10, 10, 10)));
    when(formatCondition.formatLocalDateTime(any())).thenReturn("formatted date");

    CheckResult check = condition.check(driver, element);

    assertThat(check.verdict()).isEqualTo(ACCEPT);
    assertThat(check.actualValue()).isEqualTo("formatted date");

    verify(formatCondition).check(driver, element);
    verify(formatCondition).formatLocalDateTime(LocalDateTime.of(2022, 10, 11, 10, 10, 10));
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
    when(formatCondition.formatLocalDateTime(any())).thenReturn("formatted date");

    CheckResult check = condition.check(driver, element);

    assertThat(check.verdict()).isEqualTo(REJECT);
    assertThat(check.actualValue()).isEqualTo("formatted date");

    verify(formatCondition).check(driver, element);
    verify(formatCondition).formatLocalDateTime(LocalDateTime.of(2021, 10, 12, 10, 10, 10));
  }

  @Test
  void stringRepresentationOfCondition() {
    var condition = new LocalDateTimeBetweenCondition(start, end, "yyyy/MM/dd HH:mm:ss");

    assertThat(condition.toString())
      .isEqualTo("datetime value between [\"2022/10/09 10:10:10\", \"2022/10/12 10:10:10\"] " +
        "(with datetime value format: \"yyyy/MM/dd HH:mm:ss\")");
  }

  @Test
  void throwExceptionIfStartDateIsEqualToEndDate() {
    LocalDateTime date = LocalDateTime.of(2022, 10, 11, 10, 10, 10);
    assertThatThrownBy(() -> new LocalDateTimeBetweenCondition(date, date, formatCondition))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("startDateTime must be before endDateTime");
  }

  @Test
  void throwExceptionIfStartDateAfterEndDate() {
    LocalDateTime startDate = LocalDateTime.of(2022, 10, 11, 10, 10, 10);
    LocalDateTime endDate = LocalDateTime.of(2021, 10, 11, 10, 10, 10);
    assertThatThrownBy(() -> new LocalDateTimeBetweenCondition(startDate, endDate, formatCondition))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("startDateTime must be before endDateTime");
  }
}
