package com.codeborne.selenide.collections;

import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class SizeGreaterThanOrEqualTest {

  @Test
  public void testConstructor() throws NoSuchFieldException, IllegalAccessException {
    int expectedSize = 10;
    SizeGreaterThanOrEqual listSize = new SizeGreaterThanOrEqual(expectedSize);
    Field expectedSizeField = listSize.getClass().getDeclaredField("expectedSize");
    assertEquals("List expected size", expectedSize, expectedSizeField.get(listSize));
  }

  @Test
  public void testApplyWithEmptyList() {
    int expectedSize = 10;
    SizeGreaterThanOrEqual listSize = new SizeGreaterThanOrEqual(expectedSize);
    assertFalse(listSize.apply(Collections.emptyList()));
  }

  @Test
  public void testApplyWithWrongSizeList() {
    int expectedSize = 10;
    SizeGreaterThanOrEqual listSize = new SizeGreaterThanOrEqual(expectedSize);
    assertFalse(listSize.apply(Collections.singletonList(Mockito.mock(WebElement.class))));
  }

  @Test
  public void testApplyWithSameSize() {
    int expectedSize = 1;
    SizeGreaterThanOrEqual listSize = new SizeGreaterThanOrEqual(expectedSize);
    WebElement mockedWebElement = Mockito.mock(WebElement.class);
    assertTrue(listSize.apply(Collections.singletonList(mockedWebElement)));
  }

  @Test
  public void testApplyWithGreaterSize() {
    int expectedSize = 1;
    SizeGreaterThanOrEqual listSize = new SizeGreaterThanOrEqual(expectedSize);
    WebElement mockedWebElement = Mockito.mock(WebElement.class);
    assertTrue(listSize.apply(Arrays.asList(mockedWebElement, mockedWebElement)));
  }

  @Test
  public void testFailMethod() {
    SizeGreaterThanOrEqual listSize = new SizeGreaterThanOrEqual(10);
    WebElementsCollection mockedWebElementCollection = Mockito.mock(WebElementsCollection.class);
    when(mockedWebElementCollection.description()).thenReturn("Collection description");

    try {
      listSize.fail(mockedWebElementCollection,
          Collections.emptyList(),
          new Exception("Exception message"),
          10000);
    } catch (ListSizeMismatch ex) {
      assertEquals(": expected: >= 10, actual: 0, collection: Collection description\n" +
          "Elements: []", ex.getMessage());
    }
  }

  @Test
  public void testToString() {
    SizeGreaterThanOrEqual listSize = new SizeGreaterThanOrEqual(10);
    assertEquals("size >= 10", listSize.toString());
  }
}
