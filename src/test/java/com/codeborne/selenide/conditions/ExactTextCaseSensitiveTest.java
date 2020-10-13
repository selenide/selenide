package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExactTextCaseSensitiveTest {

  private final Driver driver = mock(Driver.class);
  private final WebElement element = mock(WebElement.class);

  @BeforeEach
  void setUp() {
    when(element.getText()).thenReturn("One");
  }

  @Test
  void shouldMatchExpectedTextWithSameCase() {
    assertThat(new ExactTextCaseSensitive("One").apply(driver, element)).isEqualTo(true);
  }

  @Test
  void shouldNotMatchExpectedTextWithDifferentCase() {
    assertThat(new ExactTextCaseSensitive("one").apply(driver, element)).isEqualTo(false);
  }

  @Test
  void shouldNotMatchDifferentExpectedText() {
    assertThat(new ExactTextCaseSensitive("Two").apply(driver, element)).isEqualTo(false);
  }

  @Test
  void shouldHaveCorrectToString() {
    assertThat(new ExactTextCaseSensitive("One")).hasToString("exact text case sensitive 'One'");
  }

}
