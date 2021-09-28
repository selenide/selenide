package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExactTextTest {
  private final Driver driver = mock(Driver.class);
  private final WebElement element = mock(WebElement.class);

  private final ExactText condition = new ExactText("John Malkovich");

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(driver, element);
  }

  @Test
  void shouldMatchExpectedTextWithSameCase() {
    when(element.getText()).thenReturn("John Malkovich");

    assertThat(condition.apply(driver, element)).isTrue();
    verify(element).getText();
  }

  @Test
  void shouldMatchExpectedTextWithDifferentCase() {
    when(element.getText()).thenReturn("john Malkovich");

    assertThat(condition.apply(driver, element)).isTrue();
    verify(element).getText();
  }

  @Test
  void shouldNotMatchExpectedPartOfActualText() {
    when(element.getText()).thenReturn("test John Malkovich test");

    assertThat(condition.apply(driver, element)).isFalse();
    verify(element).getText();
  }

  @Test
  void shouldNotMatchActualPartOfExpectedText() {
    when(element.getText()).thenReturn("John");

    assertThat(condition.apply(driver, element)).isFalse();
    verify(element).getText();
  }

  @Test
  void shouldHaveCorrectToString() {
    assertThat(condition).hasToString("exact text 'John Malkovich'");
  }

  @Test
  void shouldNotHaveActualValueBeforeAnyMatching() {
    assertThat(condition.actualValue(driver, element)).isNull();
  }

  @Test
  void shouldHaveCorrectActualValueAfterMatching() {
    when(element.getText()).thenReturn("John");
    condition.apply(driver, element);

    assertThat(condition.actualValue(driver, element)).isEqualTo("John");
    verify(element).getText();
  }
}
