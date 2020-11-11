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

class CaseSensitiveTextTest {
  private final Driver driver = mock(Driver.class);
  private final WebElement element = mock(WebElement.class);

  private final CaseSensitiveText condition = new CaseSensitiveText("John Malcovich");

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(driver, element);
  }

  @Test
  void shouldMatchExpectedPartOfActualTextWithSameCase() {
    when(element.getText()).thenReturn("John Malcovich The First");

    assertThat(condition.apply(driver, element)).isTrue();
    verify(element).getText();
  }

  @Test
  void shouldMatchExpectedPartOfActualTextWithSameCaseIgnoresWhitespaces() {
    when(element.getText()).thenReturn("John Malkovich\t The   \n First");

    assertThat(new CaseSensitiveText("John        Malkovich The   ").apply(driver, element)).isTrue();
    verify(element).getText();
  }

  @Test
  void shouldNotMatchExpectedPartOfActualTextWithDifferentCase() {
    when(element.getText()).thenReturn("john malcovich the first");

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
    assertThat(condition).hasToString("textCaseSensitive 'John Malcovich'");
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
