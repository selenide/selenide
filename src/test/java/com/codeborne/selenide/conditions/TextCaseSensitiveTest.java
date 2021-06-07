package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class TextCaseSensitiveTest {

  private final Driver driver = mock(Driver.class);
  private final WebElement elementShort = elementWithText("One");
  private final WebElement elementLong = elementWithText("ZeroOneTwo");
  private final WebElement singleSelectElement = selectSingle("One", "Two", "Three"); // only the first element is selected
  private final WebElement multiSelectElement = selectMulti("One", "Two", "Three");  //  all elements are selected

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
    assertThat(new CaseSensitiveText("One")).hasToString("textCaseSensitive 'One'");
  }

  private WebElement elementWithText(String text) {
    WebElement webElement = mock(WebElement.class);
    when(webElement.getText()).thenReturn(text);
    return webElement;
  }

  private WebElement selectSingle(String... optionTexts) {
    WebElement select = mock(WebElement.class);
    when(select.getTagName()).thenReturn("select");

    List<WebElement> options = Stream.of(optionTexts)
      .map(this::elementWithText)
      .peek(option -> when(option.isSelected()).thenReturn(false))
      .collect(toList());

    when(options.get(0).isSelected()).thenReturn(true);

    when(select.findElements(By.tagName("option"))).thenReturn(options);
    return select;
  }

  private WebElement selectMulti(String... optionTexts) {
    WebElement select = mock(WebElement.class);
    when(select.getTagName()).thenReturn("select");

    List<WebElement> options = Stream.of(optionTexts)
      .map(this::elementWithText)
      .peek(option -> when(option.isSelected()).thenReturn(true))
      .collect(toList());

    when(select.findElements(By.tagName("option"))).thenReturn(options);
    return select;
  }
}
