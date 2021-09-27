package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Mocks.mockElement;
import static com.codeborne.selenide.Mocks.mockSelect;
import static com.codeborne.selenide.Mocks.option;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

final class TextCaseSensitiveTest {

  private final Driver driver = mock(Driver.class);
  private final WebElement elementShort = mockElement("One");
  private final WebElement elementLong = mockElement("ZeroOneTwo");
  private final WebElement singleSelectElement = mockSelect(option("One", true), option("Two"), option("Three"));
  private final WebElement multiSelectElement = mockSelect(option("One", true), option("Two", true), option("Three", true));

  @Test
  void shouldMatchExpectedTextWithSameCase() {
    assertThat(new CaseSensitiveText("One").apply(driver, elementShort)).isEqualTo(true);
  }

  @Test
  void shouldNotMatchExpectedTextWithDifferentCase() {
    assertThat(new CaseSensitiveText("one").apply(driver, elementShort)).isEqualTo(false);
  }

  @Test
  void shouldNotMatchDifferentExpectedText() {
    assertThat(new CaseSensitiveText("Two").apply(driver, elementShort)).isEqualTo(false);
  }

  @Test
  void shouldMatchExpectedTextWithinOtherText() {
    assertThat(new CaseSensitiveText("One").apply(driver, elementLong)).isEqualTo(true);
  }

  @Test
  void shouldMatchExpectedTextInSelectedOptions() {
    assertThat(new CaseSensitiveText("One").apply(driver, singleSelectElement)).isEqualTo(true);

    assertThat(new CaseSensitiveText("Two").apply(driver, multiSelectElement)).isEqualTo(true);
    assertThat(new CaseSensitiveText("OneTwo").apply(driver, multiSelectElement)).isEqualTo(true);
  }

  @Test
  void shouldNotMatchExpectedTextWithDifferentCaseInSelectedOptions() {
    assertThat(new CaseSensitiveText("one").apply(driver, singleSelectElement)).isEqualTo(false);

    assertThat(new CaseSensitiveText("one").apply(driver, multiSelectElement)).isEqualTo(false);
    assertThat(new CaseSensitiveText("oneTwo").apply(driver, multiSelectElement)).isEqualTo(false);
  }

  @Test
  void shouldNotMatchExpectedTextInNonSelectedOptions() {
    assertThat(new CaseSensitiveText("Two").apply(driver, singleSelectElement)).isEqualTo(false);
    assertThat(new CaseSensitiveText("Three").apply(driver, singleSelectElement)).isEqualTo(false);
  }

  @Test
  void shouldHaveCorrectToString() {
    assertThat(new CaseSensitiveText("One")).hasToString("text case sensitive 'One'");
  }
}
