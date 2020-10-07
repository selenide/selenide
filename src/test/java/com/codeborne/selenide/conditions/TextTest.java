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
