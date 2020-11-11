package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

final class TextTest implements WithAssertions {
  private Driver driver = mock(Driver.class);

  @Test
  void apply_for_textInput() {
    assertThat(new Text("Hello World").apply(driver, elementWithText("Hello World"))).isTrue();
    assertThat(new Text("Hello World").apply(driver, elementWithText("Hello"))).isFalse();
  }

  @Test
  void apply_matchTextPartially() {
    assertThat(new Text("Hello").apply(driver, elementWithText("Hello World"))).isTrue();
    assertThat(new Text("World").apply(driver, elementWithText("Hello World"))).isTrue();
  }

  @Test
  void apply_for_select() {
    assertThat(new Text("Hello World").apply(driver, select("Hello", "World"))).isFalse();
    assertThat(new Text("Hello World").apply(driver, select("Hello", " World"))).isTrue();
  }

  @Test
  void to_string() {
    assertThat(new Text("Hello World")).hasToString("text 'Hello World'");
  }

  @Test
  void negate_to_string() {
    assertThat(new Text("Hello World").negate()).hasToString("not text 'Hello World'");
  }

  @Test
  void apply_for_textInput_caseInsensitive() {
    WebElement element = elementWithText("John Malkovich The First");
    assertThat(new Text("john malkovich").apply(driver, element)).isTrue();
  }

  @Test
  void apply_for_select_caseInsensitive() {
    WebElement element = select("John", " Malkovich", " The First");
    assertThat(new Text("john malkovich").apply(driver, element)).isTrue();
  }

  @Test
  void apply_for_textInput_ignoresWhitespaces() {
    assertThat(new Text("john the malkovich").apply(driver, elementWithText("John  the\n Malkovich")))
      .isTrue();
    assertThat(new Text("This is nonbreakable space").apply(driver, elementWithText("This is nonbreakable\u00a0space")))
      .isTrue();
  }

  @Test
  void shouldNotHaveActualValueBeforeAnyMatching() {
    WebElement element = elementWithText("Hello");

    assertThat(new Text("Hello World").actualValue(driver, element)).isNull();
    verifyNoMoreInteractions(driver, element);
  }

  @Test
  void shouldHaveCorrectActualValueAfterMatching() {
    Text condition = new Text("Hello");
    WebElement element = elementWithText("Hello World");
    condition.apply(driver, element);

    assertThat(condition.actualValue(driver, element)).isEqualTo("Hello World");
    verify(element).getTagName();
    verify(element).getText();
    verifyNoMoreInteractions(driver, element);
  }

  @Test
  void shouldHaveCorrectActualValueAfterSelectMatching() {
    Text condition = new Text("Hello");
    WebElement element = select("Hello", " World");
    condition.apply(driver, element);

    assertThat(condition.actualValue(driver, element)).isEqualTo("Hello World");
    // One time in Text condition, second in selenium Select
    verify(element, times(2)).getTagName();
  }

  private WebElement elementWithText(String text) {
    WebElement webElement = mock(WebElement.class);
    when(webElement.getText()).thenReturn(text);
    return webElement;
  }

  private WebElement select(String... optionTexts) {
    WebElement select = elementWithText("Hello World");
    when(select.getTagName()).thenReturn("select");

    List<WebElement> options = Stream.of(optionTexts)
      .map(this::elementWithText)
      .peek(option -> when(option.isSelected()).thenReturn(true))
      .collect(toList());

    when(select.findElements(By.tagName("option"))).thenReturn(options);
    return select;
  }
}
