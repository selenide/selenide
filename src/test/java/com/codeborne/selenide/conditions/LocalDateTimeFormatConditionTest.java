package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.time.LocalDateTime;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class LocalDateTimeFormatConditionTest {
  private final Driver driver = mock();
  private final LocalDateTimeFormatCondition condition = new LocalDateTimeFormatCondition("yyyy/MM/dd HH:mm:ss");
  private final WebElement element = mock();

  @Test
  void correctValueFormat() {
    when(element.getAttribute(anyString())).thenReturn("2022/10/11 12:13:14");

    CheckResult check = condition.check(driver, element);

    assertThat(check.verdict()).isEqualTo(ACCEPT);
    assertThat(check.actualValue()).isEqualTo(LocalDateTime.of(2022, 10, 11, 12, 13, 14));

    verify(element).getAttribute("value");
  }

  @Test
  void incorrectValueFormat() {
    when(element.getAttribute(anyString())).thenReturn("2022-10-11 12:13:14");

    CheckResult check = condition.check(driver, element);

    assertThat(check.verdict()).isEqualTo(REJECT);
    assertThat(check.actualValue()).isEqualTo("2022-10-11 12:13:14");

    verify(element).getAttribute("value");
  }

  @Test
  void formatLocalDateAccordingToHeldPattern() {
    assertThat(condition.format(LocalDateTime.of(2022, 10, 11, 12, 13, 14)))
      .isEqualTo("2022/10/11 12:13:14");
  }

  @Test
  void stringRepresentationOfCondition() {
    assertThat(condition.toString()).isEqualTo("datetime value format: \"yyyy/MM/dd HH:mm:ss\"");
  }
}
