package com.codeborne.selenide.conditions.datetime;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.time.LocalDateTime;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class DateTimeEqualsTest {
  private final Driver driver = mock();
  private final WebElement element = mock();
  private final DateTimeFormatCondition formatCondition = mock();
  private final LocalDateTime expectedDateTime = LocalDateTime.of(2022, 10, 11, 12, 13, 14);
  private final DateTimeEquals condition = new DateTimeEquals(expectedDateTime, formatCondition);

  @Test
  void correctDateTimeValueWithCorrectFormat() {
    when(formatCondition.check(any(), any())).thenReturn(new CheckResult(ACCEPT, expectedDateTime));
    when(formatCondition.format(any())).thenReturn("formatted date");

    CheckResult check = condition.check(driver, element);

    assertThat(check.verdict()).isEqualTo(ACCEPT);
    assertThat(check.actualValue()).isEqualTo("formatted date");

    verify(formatCondition).check(driver, element);
    verify(formatCondition).format(expectedDateTime);
  }

  @Test
  void returnFormatConditionCheckResultInCaseOfRejectedVerdict() {
    CheckResult value = new CheckResult(REJECT, "some value");
    when(formatCondition.check(any(), any())).thenReturn(value);

    assertThat(condition.check(driver, element)).isSameAs(value);

    verify(formatCondition).check(driver, element);
  }

  @Test
  void incorrectDateTimeValue() {
    when(formatCondition.check(any(), any())).thenReturn(new CheckResult(ACCEPT, expectedDateTime.minusDays(1)));
    when(formatCondition.format(any())).thenReturn("formatted date");

    CheckResult check = condition.check(driver, element);

    assertThat(check.verdict()).isEqualTo(REJECT);
    assertThat(check.actualValue()).isEqualTo("formatted date");

    verify(formatCondition).check(driver, element);
    verify(formatCondition).format(expectedDateTime.minusDays(1));
  }

  @Test
  void stringRepresentation() {
    assertThat(new DateTimeEquals(expectedDateTime, "yyyy/MM/dd HH:mm:ss"))
      .hasToString("datetime value \"2022/10/11 12:13:14\"");
  }
}
