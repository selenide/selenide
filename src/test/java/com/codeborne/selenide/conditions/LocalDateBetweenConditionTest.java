package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class LocalDateBetweenConditionTest {
  private final Driver driver = mock();
  private final WebElement element = mock();
  private final LocalDateFormatCondition formatCondition = mock();
  private final LocalDate start = LocalDate.of(2022, 10, 9);
  private final LocalDate end = LocalDate.of(2022, 10, 12);
  private final LocalDateBetweenCondition condition = new LocalDateBetweenCondition(start, end, formatCondition);

  @Test
  void correctDateValueWithCorrectFormat() {
    when(formatCondition.check(any(), any())).thenReturn(new CheckResult(ACCEPT, LocalDate.of(2022, 10, 11)));
    when(formatCondition.format(any())).thenReturn("formatted date");

    CheckResult check = condition.check(driver, element);

    assertThat(check.verdict()).isEqualTo(ACCEPT);
    assertThat(check.actualValue()).isEqualTo("formatted date");

    verify(formatCondition).check(driver, element);
    verify(formatCondition).format(LocalDate.of(2022, 10, 11));
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
    when(formatCondition.check(any(), any())).thenReturn(new CheckResult(ACCEPT, LocalDate.of(2021, 10, 12)));
    when(formatCondition.format(any())).thenReturn("formatted date");

    CheckResult check = condition.check(driver, element);

    assertThat(check.verdict()).isEqualTo(REJECT);
    assertThat(check.actualValue()).isEqualTo("formatted date");

    verify(formatCondition).check(driver, element);
    verify(formatCondition).format(LocalDate.of(2021, 10, 12));
  }

  @Test
  void stringRepresentationOfCondition() {
    var condition = new LocalDateBetweenCondition(start, end, "yyyy/MM/dd");

    assertThat(condition.toString())
      .isEqualTo("date value between [\"2022/10/09\", \"2022/10/12\"] (with date value format: \"yyyy/MM/dd\")");
  }

  @Test
  void throwExceptionIfStartDateIsEqualToEndDate() {
    LocalDate date = LocalDate.of(2022, 10, 11);
    assertThatThrownBy(() -> new LocalDateBetweenCondition(date, date, formatCondition))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("startDate must be before endDate");
  }

  @Test
  void throwExceptionIfStartDateAfterEndDate() {
    LocalDate startDate = LocalDate.of(2022, 10, 11);
    LocalDate endDate = LocalDate.of(2021, 10, 11);
    assertThatThrownBy(() -> new LocalDateBetweenCondition(startDate, endDate, formatCondition))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("startDate must be before endDate");
  }
}
