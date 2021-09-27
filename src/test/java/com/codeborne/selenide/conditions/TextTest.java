package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Mocks.mockElement;
import static com.codeborne.selenide.Mocks.mockSelect;
import static com.codeborne.selenide.Mocks.option;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

final class TextTest {
  private final Driver driver = mock(Driver.class);

  @Test
  void apply_for_textInput() {
    assertThat(new Text("Hello World").apply(driver, mockElement("Hello World"))).isTrue();
    assertThat(new Text("Hello World").apply(driver, mockElement("Hello"))).isFalse();
  }

  @Test
  void apply_matchTextPartially() {
    assertThat(new Text("Hello").apply(driver, mockElement("Hello World"))).isTrue();
    assertThat(new Text("World").apply(driver, mockElement("Hello World"))).isTrue();
  }

  @Test
  void apply_for_select() {
    assertThat(new Text("Hello World").apply(driver, mockSelect(option("Hello", true), option("World", true)))).isFalse();
    assertThat(new Text("Hello World").apply(driver, mockSelect(option("Hello", true), option(" World", true)))).isTrue();
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
    WebElement element = mockElement("John Malkovich The First");
    assertThat(new Text("john malkovich").apply(driver, element)).isTrue();
  }

  @Test
  void apply_for_select_caseInsensitive() {
    WebElement element = mockSelect(
      option("John", true),
      option(" Malkovich", true),
      option(" The First", true)
    );
    assertThat(new Text("john malkovich").apply(driver, element)).isTrue();
  }

  @Test
  void apply_for_textInput_ignoresWhitespaces() {
    assertThat(new Text("john the malkovich").apply(driver, mockElement("John  the\n Malkovich")))
      .isTrue();
    assertThat(new Text("This is nonbreakable space").apply(driver, mockElement("This is nonbreakable\u00a0space")))
      .isTrue();
  }

  @Test
  void shouldNotHaveActualValueBeforeAnyMatching() {
    WebElement element = mockElement("Hello");

    assertThat(new Text("Hello World").actualValue(driver, element)).isNull();
    verifyNoMoreInteractions(driver, element);
  }

  @Test
  void shouldHaveCorrectActualValueAfterMatching() {
    Text condition = new Text("Hello");
    WebElement element = mockElement("Hello World");
    condition.apply(driver, element);

    assertThat(condition.actualValue(driver, element)).isEqualTo("Hello World");
    verify(element).getTagName();
    verify(element).getText();
    verifyNoMoreInteractions(driver, element);
  }

  @Test
  void shouldHaveCorrectActualValueAfterSelectMatching() {
    Text condition = new Text("Hello");
    WebElement element = mockSelect(option("Hello", true), option(" World", true));
    condition.apply(driver, element);

    assertThat(condition.actualValue(driver, element)).isEqualTo("Hello World");
    // One time in Text condition, second in selenium Select
    verify(element, times(2)).getTagName();
  }
}
