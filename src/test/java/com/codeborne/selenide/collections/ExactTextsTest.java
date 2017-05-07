package com.codeborne.selenide.collections;

import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ExactTextsTest {

  @Test
  public void varArgsConstructor() throws NoSuchFieldException, IllegalAccessException {
    ExactTexts exactTexts = new ExactTexts("One", "Two", "Three");
    Field expectedTextsFIeld = exactTexts.getClass().getDeclaredField("expectedTexts");
    assertEquals("Expected texts list", Arrays.asList("One", "Two", "Three"), expectedTextsFIeld.get(exactTexts));
  }

  @Test
  public void testApplyOnWrongSizeList() {
    ExactTexts exactTexts = new ExactTexts("One", "Two", "Three");
    assertFalse(exactTexts.apply(singletonList(Mockito.mock(WebElement.class))));
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
    WebElement mockedWebElement1 = Mockito.mock(WebElement.class);
    WebElement mockedWebElement2 = Mockito.mock(WebElement.class);
    when(mockedWebElement1.getText()).thenReturn(exactText1);
    when(mockedWebElement2.getText()).thenReturn(shouldMatch ? exactText2 : exactText1);
    assertEquals(shouldMatch, exactTexts.apply(Arrays.asList(mockedWebElement1, mockedWebElement2)));
  }

  @Test
  public void testFailWithNullElementsList() {
    failOnEmptyOrNullElementsList(null);
  }

  @Test
  public void testFailWithEmptyElementsLIst() {
    failOnEmptyOrNullElementsList(Collections.emptyList());
  }

  private void failOnEmptyOrNullElementsList(List<WebElement> elements) {
    ExactTexts exactTexts = new ExactTexts("One");
    Exception exception = new Exception("Exception method");
    try {
      exactTexts.fail(Mockito.mock(WebElementsCollection.class), elements, exception, 10000);
    } catch (ElementNotFound ex) {
      assertEquals("Element not found {null}\nExpected: [One]", ex.getMessage());
    }
  }

  @Test
  public void failOnTextMismatch() {
    ExactTexts exactTexts = new ExactTexts("One");
    Exception exception = new Exception("Exception method");

    WebElement mockedWebElement = Mockito.mock(WebElement.class);
    when(mockedWebElement.getText()).thenReturn("Hello");

    WebElementsCollection mockedElementsCollection = Mockito.mock(WebElementsCollection.class);
    when(mockedElementsCollection.description()).thenReturn("Collection description");

    try {
      exactTexts.fail(mockedElementsCollection,
                      Collections.singletonList(mockedWebElement),
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
      new ExactTexts(new ArrayList<>());
      fail("expected IllegalArgumentException");
    }
    catch (IllegalArgumentException expected) {
      assertThat(expected.getMessage(), is("No expected texts given"));
    }
  }
}
