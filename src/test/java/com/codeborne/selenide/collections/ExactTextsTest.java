package com.codeborne.selenide.collections;

import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExactTextsTest {

  @Test
  public void varArgsConstructor() {
    ExactTexts exactTexts = new ExactTexts("One", "Two", "Three");
    assertEquals("Expected texts list", asList("One", "Two", "Three"), exactTexts.expectedTexts);
  }

  @Test
  public void testApplyOnWrongSizeList() {
    ExactTexts exactTexts = new ExactTexts("One", "Two", "Three");
    assertFalse(exactTexts.apply(singletonList(mock(WebElement.class))));
  }

  @Test
  public void testApplyOnCorrectSizeAndCorrectElementsText() {
    testApplyMethodOnDifferentCondtions(true);
  }

  @Test
  public void testApplyOnCorrectListSizeButWrongElementsText() {
    testApplyMethodOnDifferentCondtions(false);
  }

  private void testApplyMethodOnDifferentCondtions(boolean shouldMatch) {
    String exactText1 = "One";
    String exactText2 = "Two";
    ExactTexts exactTexts = new ExactTexts(exactText1, exactText2);
    WebElement mockedWebElement1 = mock(WebElement.class);
    WebElement mockedWebElement2 = mock(WebElement.class);
    when(mockedWebElement1.getText()).thenReturn(exactText1);
    when(mockedWebElement2.getText()).thenReturn(shouldMatch ? exactText2 : exactText1);
    assertEquals(shouldMatch, exactTexts.apply(asList(mockedWebElement1, mockedWebElement2)));
  }

  @Test
  public void testFailWithNullElementsList() {
    failOnEmptyOrNullElementsList(null);
  }

  @Test
  public void testFailWithEmptyElementsLIst() {
    failOnEmptyOrNullElementsList(emptyList());
  }

  private void failOnEmptyOrNullElementsList(List<WebElement> elements) {
    ExactTexts exactTexts = new ExactTexts("One");
    Exception exception = new Exception("Exception method");
    try {
      exactTexts.fail(mock(WebElementsCollection.class), elements, exception, 10000);
    } catch (ElementNotFound ex) {
      assertEquals("Element not found {null}\nExpected: [One]", ex.getMessage());
    }
  }

  @Test
  public void failOnTextMismatch() {
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
      assertEquals("\nActual: [Hello]\n" +
          "Expected: [One]\n" +
          "Collection: Collection description", ex.getMessage());
    }
  }

  @Test
  public void testToString() {
    ExactTexts exactTexts = new ExactTexts("One", "Two", "Three");
    assertEquals("Exact texts [One, Two, Three]", exactTexts.toString());
  }

  @Test
  public void emptyArrayIsNotAllowed() {
    try {
      new ExactTexts();
      fail("expected IllegalArgumentException");
    }
    catch (IllegalArgumentException expected) {
      assertThat(expected.getMessage(), is("No expected texts given"));
    }
  }

  @Test
  public void emptyListIsNotAllowed() {
    try {
      new ExactTexts(emptyList());
      fail("expected IllegalArgumentException");
    }
    catch (IllegalArgumentException expected) {
      assertThat(expected.getMessage(), is("No expected texts given"));
    }
  }
}
