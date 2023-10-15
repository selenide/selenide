package com.codeborne.selenide.conditions.datetime;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class DateFormatConditionTest {
  private final Driver driver = mock();
  private final DateFormatCondition condition = new DateFormatCondition("yyyy/MM/dd");
  private final WebElement element = mock();

  @Test
  void correctValueFormat() {
    when(element.getAttribute(anyString())).thenReturn("2022/10/11");

    CheckResult check = condition.check(driver, element);

    assertThat(check.verdict()).isEqualTo(ACCEPT);
    assertThat(check.actualValue()).isEqualTo(LocalDate.of(2022, 10, 11));

    verify(element).getAttribute("value");
  }

  @Test
  void incorrectValueFormat() {
    when(element.getAttribute(anyString())).thenReturn("2022-10-11");

    CheckResult check = condition.check(driver, element);

    assertThat(check.verdict()).isEqualTo(REJECT);
    assertThat(check.actualValue()).isEqualTo("2022-10-11");

    verify(element).getAttribute("value");
  }

  @Test
  void formatLocalDateAccordingToHeldPattern() {
    assertThat(condition.format(LocalDate.of(2022, 10, 11))).isEqualTo("2022/10/11");
  }

  @Test
  void stringRepresentation() {
    assertThat(condition).hasToString("date format \"yyyy/MM/dd\"");
  }
}
