package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotTest {
  Condition originalCondition = mock(Condition.class);
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
    Driver driver = mock(Driver.class);
    WebElement webElement = mock(WebElement.class);
    when(originalCondition.actualValue(any(Driver.class), any(WebElement.class)))
      .thenReturn("original condition actual value");


    assertThat(notCondition.actualValue(driver, webElement)).isEqualTo("original condition actual value");
    verify(originalCondition).actualValue(driver, webElement);
  }

  @Test
  void applyFalse() {
    Driver driver = mock(Driver.class);
    WebElement webElement = mock(WebElement.class);
    when(originalCondition.apply(any(Driver.class), any(WebElement.class))).thenReturn(true);

    assertThat(notCondition.apply(driver, webElement)).isFalse();
    verify(originalCondition).apply(driver, webElement);
  }

  @Test
  void applyTrue() {
    Driver driver = mock(Driver.class);
    WebElement webElement = mock(WebElement.class);
    when(originalCondition.apply(any(Driver.class), any(WebElement.class))).thenReturn(false);

    assertThat(notCondition.apply(driver, webElement)).isTrue();
    verify(originalCondition).apply(driver, webElement);
  }

  @Test
  void hasToString() {
    when(originalCondition.toString()).thenReturn("original condition toString");

    assertThat(notCondition).hasToString("not original condition toString");
  }
}
