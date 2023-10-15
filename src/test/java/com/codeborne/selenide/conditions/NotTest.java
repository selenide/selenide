package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;

import java.time.LocalDateTime;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotTest {
  Condition originalCondition = mock();
  Not notCondition;

  @BeforeEach
  void commonMockCalls() {
    // constructor call
    when(originalCondition.getName()).thenReturn("original condition name");
    notCondition = new Not(originalCondition, false);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    // constructor call
    verify(originalCondition).getName();
    Mockito.verifyNoMoreInteractions(originalCondition);
  }

  @Test
  void getName() {
    assertThat(notCondition.getName()).isEqualTo("not original condition name");
  }

  @Test
  void actualValue() {
    Driver driver = mock();
    WebElement webElement = mock();
    when(originalCondition.check(any(Driver.class), any(WebElement.class)))
      .thenReturn(new CheckResult(REJECT, "original condition actual value"));

    CheckResult checkResult = notCondition.check(driver, webElement);
    assertThat(checkResult.actualValue()).isEqualTo("original condition actual value");
    assertThat(checkResult.verdict()).isEqualTo(ACCEPT);
    verify(originalCondition).check(driver, webElement);
  }

  @Test
  void applyFalse() {
    Driver driver = mock();
    WebElement webElement = mock();
    LocalDateTime timestamp = LocalDateTime.parse("2020-12-31T23:58:59");
    when(originalCondition.check(any(), any())).thenReturn(new CheckResult(ACCEPT, null, "displayed", timestamp));

    assertThat(notCondition.check(driver, webElement)).isEqualTo(new CheckResult(REJECT, null, "displayed", timestamp));
    verify(originalCondition).check(driver, webElement);
  }

  @Test
  void applyTrue() {
    Driver driver = mock();
    WebElement webElement = mock();
    LocalDateTime timestamp = LocalDateTime.parse("2020-12-31T23:58:59");
    when(originalCondition.check(any(), any())).thenReturn(new CheckResult(REJECT, null, "hidden", timestamp));

    assertThat(notCondition.check(driver, webElement)).isEqualTo(new CheckResult(ACCEPT, null, "hidden", timestamp));
    verify(originalCondition).check(driver, webElement);
  }

  @Test
  void hasToString() {
    when(originalCondition.toString()).thenReturn("original condition toString");

    assertThat(notCondition).hasToString("not original condition toString");
  }
}
