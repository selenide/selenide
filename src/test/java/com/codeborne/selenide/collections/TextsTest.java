package com.codeborne.selenide.collections;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TextsTest implements WithAssertions {
  @Test
  void testApplyWithEmptyList() {
    assertThat(new Texts("One", "Two", "Three").apply(emptyList()))
      .isFalse();
  }

  @Test
  void testApplyWithWrongSizeList() {
    Texts texts = new Texts(asList("One", "Two", "Three"));
    assertThat(texts.apply(singletonList(mock(WebElement.class))))
      .isFalse();
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
    assertThat(texts.apply(asList(mockElement1, mockElement2)))
      .isEqualTo(shouldMatch);
  }

  @Test
  void testApplyWithNoMatchOnPartialText() {
    testApplyMethod(false);
  }

  @Test
  void testToString() {
    assertThat(new Texts(asList("One", "Two")))
      .hasToString("Texts [One, Two]");
  }
}
