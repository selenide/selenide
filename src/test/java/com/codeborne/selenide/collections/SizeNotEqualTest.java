package com.codeborne.selenide.collections;

import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SizeNotEqualTest {
  @Test
  public void testApplyWithEmptyList() {
    assertTrue(new SizeNotEqual(10).apply(emptyList()));
  }

  @Test
  public void testApplyWithWrongSizeList() {
    assertTrue(new SizeNotEqual(10).apply(singletonList(mock(WebElement.class))));
  }

  @Test
  public void testApplyWithCorrectSizeNotEqual() {
    assertFalse(new SizeNotEqual(1).apply(singletonList(mock(WebElement.class))));
  }

  @Test
  public void testFailMethod() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    when(mockedWebElementCollection.description()).thenReturn("Collection description");

    try {
      new SizeNotEqual(10).fail(mockedWebElementCollection,
          emptyList(),
          new Exception("Exception message"),
          10000);
    } catch (ListSizeMismatch ex) {
      assertEquals(": expected: <> 10, actual: 0, collection: Collection description\n" +
          "Elements: []", ex.getMessage());
    }
  }

  @Test
  public void testToString() {
    assertEquals("size <> 10", new SizeNotEqual(10).toString());
  }
}
