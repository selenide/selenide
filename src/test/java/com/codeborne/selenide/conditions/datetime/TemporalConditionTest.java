package com.codeborne.selenide.conditions.datetime;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class TemporalConditionTest {
  private final Driver driver = mock();
  private final WebElement element = mock();
  private final DateFormatCondition formatCondition = mock();
  private final DateEquals condition = new DateEquals(LocalDate.of(2022, 10, 11), formatCondition);

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
    when(formatCondition.check(any(), any())).thenReturn(new CheckResult(ACCEPT, LocalDate.of(2023, 10, 11)));
    when(formatCondition.format(any())).thenReturn("formatted date");

    CheckResult check = condition.check(driver, element);

    assertThat(check.verdict()).isEqualTo(REJECT);
    assertThat(check.actualValue()).isEqualTo("formatted date");

    verify(formatCondition).check(driver, element);
    verify(formatCondition).format(LocalDate.of(2023, 10, 11));
  }

  @Test
  void stringRepresentation() {
    assertThat(new DateEquals(LocalDate.of(2022, 10, 11), "yyyy/MM/dd"))
      .hasToString("date value \"2022/10/11\"");
  }
}
