package com.codeborne.selenide.collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TextsTest {

  @Test
  void testApplyWithEmptyList() {
    Assertions.assertFalse(new Texts("One", "Two", "Three").apply(emptyList()));
  }

  @Test
  void testApplyWithWrongSizeList() {
    Texts texts = new Texts(asList("One", "Two", "Three"));
    Assertions.assertFalse(texts.apply(singletonList(mock(WebElement.class))));
  }

  @Test
  void testApplyWithMatchOnPartialText() {
    testApplyMethod(true);
  }

  private void testApplyMethod(boolean shouldMatch) {
    Texts texts = new Texts(asList("One", "Two"));
    WebElement mockElement1 = mock(WebElement.class);
    WebElement mockElement2 = mock(WebElement.class);

    when(mockElement1.getText()).thenReturn(shouldMatch ? "OneThing" : "Three");
    when(mockElement2.getText()).thenReturn(shouldMatch ? "Two" : "Selenide");
    Assertions.assertEquals(shouldMatch, texts.apply(asList(mockElement1, mockElement2)));
  }

  @Test
  void testApplyWithNoMatchOnPartialText() {
    testApplyMethod(false);
  }

  @Test
  void testToString() {
    Assertions.assertEquals("Texts [One, Two]", new Texts(asList("One", "Two")).toString());
  }
}
