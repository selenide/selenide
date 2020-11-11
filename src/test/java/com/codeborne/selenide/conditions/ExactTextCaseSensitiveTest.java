package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExactTextCaseSensitiveTest {

  private final Driver driver = mock(Driver.class);
  private final WebElement element = mock(WebElement.class);

  private final ExactTextCaseSensitive condition = new ExactTextCaseSensitive("One");

  @BeforeEach
  void setUp() {
    when(element.getText()).thenReturn("One");
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(driver, element);
  }

  @Test
  void shouldMatchExpectedTextWithSameCase() {
    assertThat(condition.apply(driver, element)).isTrue();
    verify(element).getText();
  }

  @Test
  void shouldNotMatchExpectedTextWithDifferentCase() {
    assertThat(new ExactTextCaseSensitive("one").apply(driver, element)).isFalse();
    verify(element).getText();
  }

  @Test
  void shouldNotMatchDifferentExpectedText() {
    assertThat(new ExactTextCaseSensitive("Two").apply(driver, element)).isFalse();
    verify(element).getText();
  }

  @Test
  void shouldHaveCorrectToString() {
    assertThat(condition).hasToString("exact text case sensitive 'One'");
  }

  @Test
  void shouldNotHaveActualValueBeforeAnyMatching() {
    assertThat(condition.actualValue(driver, element)).isNull();
  }

  @Test
  void shouldHaveCorrectActualValueAfterMatching() {
    ExactTextCaseSensitive condition = new ExactTextCaseSensitive("Two");
    condition.apply(driver, element);

    assertThat(condition.actualValue(driver, element)).isEqualTo("One");
    verify(element).getText();
  }
}
