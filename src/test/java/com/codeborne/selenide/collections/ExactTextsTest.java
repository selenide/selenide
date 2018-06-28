package com.codeborne.selenide.collections;

import java.util.List;

import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExactTextsTest implements WithAssertions {
  @Test
  void varArgsConstructor() {
    ExactTexts exactTexts = new ExactTexts("One", "Two", "Three");
    assertThat(exactTexts.expectedTexts)
      .as("Expected texts list")
      .isEqualTo(asList("One", "Two", "Three"));
  }

  @Test
  void testApplyOnWrongSizeList() {
    ExactTexts exactTexts = new ExactTexts("One", "Two", "Three");

    assertThat(exactTexts.apply(singletonList(mock(WebElement.class))))
      .isFalse();
  }

  @Test
  void testApplyOnCorrectSizeAndCorrectElementsText() {
    testApplyMethodOnDifferentConditions(true);
  }

  private void testApplyMethodOnDifferentConditions(boolean shouldMatch) {
    String exactText1 = "One";
    String exactText2 = "Two";
    ExactTexts exactTexts = new ExactTexts(exactText1, exactText2);
    WebElement mockedWebElement1 = mock(WebElement.class);
    WebElement mockedWebElement2 = mock(WebElement.class);
    when(mockedWebElement1.getText()).thenReturn(exactText1);
    when(mockedWebElement2.getText()).thenReturn(shouldMatch ? exactText2 : exactText1);

    assertThat(exactTexts.apply(asList(mockedWebElement1, mockedWebElement2)))
      .isEqualTo(shouldMatch);
  }

  @Test
  void testApplyOnCorrectListSizeButWrongElementsText() {
    testApplyMethodOnDifferentConditions(false);
  }

  @Test
  void testFailWithNullElementsList() {
    failOnEmptyOrNullElementsList(null);
  }

  private void failOnEmptyOrNullElementsList(List<WebElement> elements) {
    ExactTexts exactTexts = new ExactTexts("One");
    Exception exception = new Exception("Exception method");
    try {
      exactTexts.fail(mock(WebElementsCollection.class), elements, exception, 10000);
    } catch (ElementNotFound ex) {
      assertThat(ex)
        .hasMessage("Element not found {null}\nExpected: [One]");
    }
  }

  @Test
  void testFailWithEmptyElementsLIst() {
    failOnEmptyOrNullElementsList(emptyList());
  }

  @Test
  void failOnTextMismatch() {
    ExactTexts exactTexts = new ExactTexts("One");
    Exception exception = new Exception("Exception method");

    WebElement mockedWebElement = mock(WebElement.class);
    when(mockedWebElement.getText()).thenReturn("Hello");

    WebElementsCollection mockedElementsCollection = mock(WebElementsCollection.class);
    when(mockedElementsCollection.description()).thenReturn("Collection description");

    try {
      exactTexts.fail(mockedElementsCollection,
        singletonList(mockedWebElement),
        exception,
        10000);
    } catch (TextsMismatch ex) {
      assertThat(ex)
        .hasMessage("\nActual: [Hello]\nExpected: [One]\nCollection: Collection description");
    }
  }

  @Test
  void testToString() {
    ExactTexts exactTexts = new ExactTexts("One", "Two", "Three");
    assertThat(exactTexts)
      .hasToString("Exact texts [One, Two, Three]");
  }

  @Test
  void emptyArrayIsNotAllowed() {
    try {
      new ExactTexts();
      fail("expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      assertThat(expected)
        .hasMessage("No expected texts given");
    }
  }

  @Test
  void emptyListIsNotAllowed() {
    try {
      new ExactTexts(emptyList());
      fail("expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      assertThat(expected)
        .hasMessage("No expected texts given");
    }
  }
}
