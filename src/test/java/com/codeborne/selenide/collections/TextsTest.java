package com.codeborne.selenide.collections;

import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

public class TextsTest {

  @Test
  public void testApplyWithEmptyList() {
    Texts texts = new Texts("One", "Two", "Three");
    assertFalse(texts.apply(Collections.emptyList()));
  }

  @Test
  public void testApplyWithWrongSizeList() {
    Texts texts = new Texts(Arrays.asList("One", "Two", "Three"));
    assertFalse(texts.apply(Collections.singletonList(Mockito.mock(WebElement.class))));
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
    Texts texts = new Texts(Arrays.asList("One", "Two"));
    WebElement mockElement1 = Mockito.mock(WebElement.class);
    WebElement mockElement2 = Mockito.mock(WebElement.class);

    when(mockElement1.getText()).thenReturn(shouldMatch ? "OneThing" : "Three");
    when(mockElement2.getText()).thenReturn(shouldMatch ? "Two" : "Selenide");
    assertEquals(shouldMatch, texts.apply(Arrays.asList(mockElement1, mockElement2)));
  }

  @Test
  public void testToString() {
    Texts texts = new Texts(Arrays.asList("One", "Two"));
    assertEquals("Texts [One, Two]", texts.toString());
  }

}
