package com.codeborne.selenide.collections;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TextsTest {

  @Test
  public void testApplyWithEmptyList() {
    assertFalse(new Texts("One", "Two", "Three").apply(emptyList()));
  }

  @Test
  public void testApplyWithWrongSizeList() {
    Texts texts = new Texts(asList("One", "Two", "Three"));
    assertFalse(texts.apply(singletonList(mock(WebElement.class))));
  }

  @Test
  public void testApplyWithMatchOnPartialText() {
    testApplyMethod(true);
  }

  @Test
  public void testApplyWithNoMatchOnPartialText() {
    testApplyMethod(false);
  }

  private void testApplyMethod(boolean shouldMatch) {
    Texts texts = new Texts(asList("One", "Two"));
    WebElement mockElement1 = mock(WebElement.class);
    WebElement mockElement2 = mock(WebElement.class);

    when(mockElement1.getText()).thenReturn(shouldMatch ? "OneThing" : "Three");
    when(mockElement2.getText()).thenReturn(shouldMatch ? "Two" : "Selenide");
    assertEquals(shouldMatch, texts.apply(asList(mockElement1, mockElement2)));
  }

  @Test
  public void testToString() {
    assertEquals("Texts [One, Two]", new Texts(asList("One", "Two")).toString());
  }

}
