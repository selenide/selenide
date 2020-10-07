package com.codeborne.selenide.collections;

import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.ex.TextsSizeMismatch;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.Mocks.mockCollection;
import static com.codeborne.selenide.Mocks.mockElement;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ExactTextsTest implements WithAssertions {
  @Test
  void varArgsConstructor() {
    ExactTexts exactTexts = new ExactTexts("One", "Two", "Three");
    assertThat(exactTexts.expectedTexts)
      .as("Expected texts list")
      .isEqualTo(asList("One", "Two", "Three"));
  }

  @Test
  void applyOnWrongSizeList() {
    ExactTexts exactTexts = new ExactTexts("One", "Two", "Three");

    assertThat(exactTexts.test(singletonList(mock(WebElement.class))))
      .isFalse();
  }

  @Test
  void applyOnCorrectSizeAndCorrectElementsText() {
    ExactTexts exactTexts = new ExactTexts("One", "Two");
    WebElement webElement1 = mockElement("One");
    WebElement webElement2 = mockElement("Two");

    assertThat(exactTexts.test(asList(webElement1, webElement2)))
      .isTrue();
  }

  @Test
  void applyOnCorrectListSizeButWrongElementsText() {
    ExactTexts exactTexts = new ExactTexts("One", "Two");
    WebElement webElement1 = mockElement("One");
    WebElement webElement2 = mockElement("One");

    assertThat(exactTexts.test(asList(webElement1, webElement2)))
      .isFalse();
  }

  @Test
  void failWithNullElementsList() {
    failOnEmptyOrNullElementsList(null);
  }

  private void failOnEmptyOrNullElementsList(List<WebElement> elements) {
    ExactTexts exactTexts = new ExactTexts("One");
    RuntimeException cause = new IllegalArgumentException("bad thing happened");

    assertThatThrownBy(() -> exactTexts.fail(mockCollection("Collection description"), elements, cause, 10000))
      .isInstanceOf(ElementNotFound.class)
      .hasMessage(String.format("Element not found {Collection description}%nExpected: Exact texts [One]%nScreenshot: null%n" +
        "Timeout: 10 s.%n" +
        "Caused by: java.lang.IllegalArgumentException: bad thing happened"));
  }

  @Test
  void failWithEmptyElementsLIst() {
    failOnEmptyOrNullElementsList(emptyList());
  }

  @Test
  void failOnTextMismatch() {
    ExactTexts exactTexts = new ExactTexts("One");
    Exception cause = new Exception("Exception method");

    WebElement mockedWebElement = mock(WebElement.class);
    when(mockedWebElement.getText()).thenReturn("Hello");

    assertThatThrownBy(() ->
      exactTexts.fail(mockCollection("Collection description"), singletonList(mockedWebElement), cause, 10000))
      .isInstanceOf(TextsMismatch.class)
      .hasMessage(String.format("Texts mismatch%n" +
        "Actual: [Hello]%n" +
        "Expected: [One]%n" +
        "Collection: Collection description%n" +
        "Screenshot: null%nTimeout: 10 s."));
  }

  @Test
  void failOnTextSizeMismatch() {
    ExactTexts exactTexts = new ExactTexts("One", "Two");
    Exception cause = new Exception("Exception method");
    WebElement webElement = mockElement("One");

    assertThatThrownBy(() -> exactTexts.fail(mockCollection("Collection description"),
      singletonList(webElement), cause, 10000)
    )
      .isInstanceOf(TextsSizeMismatch.class)
      .hasMessageContaining("Actual: [One], List size: 1")
      .hasMessageContaining("Expected: [One, Two], List size: 2")
      .hasMessageEndingWith(String.format("Collection: Collection description%nScreenshot: null%nTimeout: 10 s."));
  }

  @Test
  void testToString() {
    ExactTexts exactTexts = new ExactTexts("One", "Two", "Three");
    assertThat(exactTexts)
      .hasToString("Exact texts [One, Two, Three]");
  }

  @Test
  void emptyArrayIsNotAllowed() {
    assertThatThrownBy(ExactTexts::new)
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("No expected texts given");
  }

  @Test
  void emptyListIsNotAllowed() {
    assertThatThrownBy(() -> new ExactTexts(emptyList()))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("No expected texts given");
  }
}
