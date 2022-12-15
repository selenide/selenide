package com.codeborne.selenide.collections;

import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.ex.TextsSizeMismatch;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.Mocks.mockCollection;
import static com.codeborne.selenide.Mocks.mockElement;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ExactTextsCaseSensitiveTest {
  @Test
  void varArgsConstructor() {
    ExactTextsCaseSensitive exactTextsCaseSensitive = new ExactTextsCaseSensitive("One", "Two", "Three");
    assertThat(exactTextsCaseSensitive.expectedTexts)
      .as("Expected texts list")
      .isEqualTo(asList("One", "Two", "Three"));
  }

  @Test
  void applyOnWrongSizeList() {
    ExactTextsCaseSensitive exactTextsCaseSensitive = new ExactTextsCaseSensitive("One", "Two", "Three");

    assertThat(exactTextsCaseSensitive.test(singletonList(mock())))
      .isFalse();
  }

  @Test
  void applyOnCorrectSizeAndCorrectElementsText() {
    ExactTextsCaseSensitive exactTextsCaseSensitive = new ExactTextsCaseSensitive("One", "Two");
    WebElement webElement1 = mockElement("One");
    WebElement webElement2 = mockElement("Two");

    assertThat(exactTextsCaseSensitive.test(asList(webElement1, webElement2)))
      .isTrue();
  }

  @Test
  void applyOnCorrectListSizeButWrongElementsText() {
    ExactTextsCaseSensitive exactTextsCaseSensitive = new ExactTextsCaseSensitive("One", "Two");
    WebElement webElement1 = mockElement("One");
    WebElement webElement2 = mockElement("One");

    assertThat(exactTextsCaseSensitive.test(asList(webElement1, webElement2)))
      .isFalse();
  }

  @Test
  void failWithNullElementsList() {
    failOnEmptyOrNullElementsList(null);
  }

  private void failOnEmptyOrNullElementsList(List<WebElement> elements) {
    ExactTextsCaseSensitive exactTextsCaseSensitive = new ExactTextsCaseSensitive("One");
    RuntimeException cause = new IllegalArgumentException("bad thing happened");

    assertThatThrownBy(() -> exactTextsCaseSensitive.fail(mockCollection("Collection description"), elements, cause, 10000))
      .isInstanceOf(ElementNotFound.class)
      .hasMessage(String.format("Element not found {Collection description}%nExpected: Exact texts case sensitive [One]%n" +
        "Timeout: 10 s.%n" +
        "Caused by: java.lang.IllegalArgumentException: bad thing happened"));
  }

  @Test
  void failWithEmptyElementsList() {
    failOnEmptyOrNullElementsList(emptyList());
  }

  @Test
  void failOnTextMismatch() {
    ExactTextsCaseSensitive exactTextsCaseSensitive = new ExactTextsCaseSensitive("One");
    Exception cause = new Exception("Exception method");

    WebElement mockedWebElement = mock();
    when(mockedWebElement.getText()).thenReturn("Hello");

    assertThatThrownBy(() ->
      exactTextsCaseSensitive.fail(mockCollection("Collection description"), singletonList(mockedWebElement), cause, 10000))
      .isInstanceOf(TextsMismatch.class)
      .hasMessage(String.format("Texts mismatch%n" +
        "Actual: [Hello]%n" +
        "Expected: [One]%n" +
        "Collection: Collection description%n" +
        "Timeout: 10 s."));
  }

  @Test
  void failOnTextCaseMismatch() {
    ExactTextsCaseSensitive exactTextsCaseSensitive = new ExactTextsCaseSensitive("ONE");
    Exception cause = new Exception("Exception method");

    WebElement mockedWebElement = mock();
    when(mockedWebElement.getText()).thenReturn("One");

    assertThatThrownBy(() ->
      exactTextsCaseSensitive.fail(mockCollection("Collection description"), singletonList(mockedWebElement), cause, 10000))
      .isInstanceOf(TextsMismatch.class)
      .hasMessage(String.format("Texts mismatch%n" +
        "Actual: [One]%n" +
        "Expected: [ONE]%n" +
        "Collection: Collection description%n" +
        "Timeout: 10 s."));
  }

  @Test
  void failOnTextSizeMismatch() {
    ExactTextsCaseSensitive exactTextsCaseSensitive = new ExactTextsCaseSensitive("One", "Two");
    Exception cause = new Exception("Exception method");
    WebElement webElement = mockElement("One");

    assertThatThrownBy(() -> exactTextsCaseSensitive.fail(mockCollection("Collection description"),
      singletonList(webElement), cause, 10000)
    )
      .isInstanceOf(TextsSizeMismatch.class)
      .hasMessageContaining("Actual: [One], List size: 1")
      .hasMessageContaining("Expected: [One, Two], List size: 2")
      .hasMessageEndingWith(String.format("Collection: Collection description%nTimeout: 10 s."));
  }

  @Test
  void testToString() {
    ExactTextsCaseSensitive exactTextsCaseSensitive = new ExactTextsCaseSensitive("One", "Two", "Three");
    assertThat(exactTextsCaseSensitive)
      .hasToString("Exact texts case sensitive [One, Two, Three]");
  }

  @Test
  void emptyArrayIsNotAllowed() {
    assertThatThrownBy(ExactTextsCaseSensitive::new)
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("No expected texts given");
  }

  @Test
  void emptyListIsNotAllowed() {
    assertThatThrownBy(() -> new ExactTextsCaseSensitive(emptyList()))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("No expected texts given");
  }
}
