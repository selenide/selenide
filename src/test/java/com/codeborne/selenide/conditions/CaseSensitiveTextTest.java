package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.Mocks;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CaseSensitiveTextTest {
  private final Driver driver = new DriverStub();
  private final WebElement element = Mocks.mockWebElement("div", "");

  private final CaseSensitiveText condition = new CaseSensitiveText("John Malkovich");

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(element);
  }

  @Test
  void shouldMatchExpectedPartOfActualTextWithSameCase() {
    when(element.getText()).thenReturn("John Malkovich The First");

    assertThat(condition.apply(driver, element)).isTrue();
    verify(element).getTagName();
    verify(element).getText();
  }

  @Test
  void shouldMatchExpectedPartOfActualTextWithSameCaseIgnoresWhitespaces() {
    when(element.getText()).thenReturn("John Malkovich\t The   \n First");

    assertThat(new CaseSensitiveText("John        Malkovich The   ").apply(driver, element)).isTrue();
    verify(element).getTagName();
    verify(element).getText();
  }

  @Test
  void shouldNotMatchExpectedPartOfActualTextWithDifferentCase() {
    when(element.getText()).thenReturn("john Malkovich the first");

    assertThat(condition.apply(driver, element)).isFalse();
    verify(element).getTagName();
    verify(element).getText();
  }

  @Test
  void shouldNotMatchActualPartOfExpectedText() {
    when(element.getText()).thenReturn("John");

    assertThat(condition.apply(driver, element)).isFalse();
    verify(element).getTagName();
    verify(element).getText();
  }

  @Test
  void shouldHaveCorrectToString() {
    assertThat(condition).hasToString("text case sensitive 'John Malkovich'");
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
    verify(element).getTagName();
    verify(element).getText();
  }
}
