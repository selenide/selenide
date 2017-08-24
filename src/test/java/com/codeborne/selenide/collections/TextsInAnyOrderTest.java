package com.codeborne.selenide.collections;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TextsInAnyOrderTest {

  @Test
  public void testApplyWithSameOrder() {
    TextsInAnyOrder texts = new TextsInAnyOrder(asList("One", "Two", "Three"));
    testApplyMethod(true, texts);
  }
  
  @Test
  public void testApplyWithDifferentOrder() {
    TextsInAnyOrder texts = new TextsInAnyOrder(asList("Two", "One", "Three"));
    testApplyMethod(true, texts);
  }
  
  @Test
  public void testApplyWithWrongListSize() {
    TextsInAnyOrder texts = new TextsInAnyOrder(asList("Two", "One"));
    testApplyMethod(false, texts);
  }
  
   @Test
  public void testApplyWithBiggerSize() {
    TextsInAnyOrder texts = new TextsInAnyOrder(asList("Two", "One", "four", "three"));
    testApplyMethod(false, texts);
  }



  private void testApplyMethod(boolean shouldMatch, TextsInAnyOrder texts) {
   WebElement mockElement1 = mock(WebElement.class);
    WebElement mockElement2 = mock(WebElement.class);
    WebElement mockElement3 = mock(WebElement.class);

    when(mockElement1.getText()).thenReturn("One");
    when(mockElement2.getText()).thenReturn("Two");
    when(mockElement3.getText()).thenReturn("Three");
    assertEquals(shouldMatch, texts.apply(asList(mockElement1, mockElement2, mockElement3)));
  
	  
  }
  
  @Test
  public void testToString() {
    assertEquals("TextsInAnyOrder [One, Two]", new TextsInAnyOrder(asList("One", "Two")).toString());
  }

}
