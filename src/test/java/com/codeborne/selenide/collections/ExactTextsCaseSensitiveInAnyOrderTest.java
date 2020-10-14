package com.codeborne.selenide.collections;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExactTextsCaseSensitiveInAnyOrderTest implements WithAssertions {

  @Test
  void shouldMatchWithSameOrder() {
    ExactTextsCaseSensitiveInAnyOrder texts = new ExactTextsCaseSensitiveInAnyOrder(asList("One", "Two", "Three"));
    testApplyMethod(true, texts);
  }

  private void testApplyMethod(boolean shouldMatch, ExactTextsCaseSensitiveInAnyOrder texts) {
    WebElement mockElement1 = mock(WebElement.class);
    WebElement mockElement2 = mock(WebElement.class);
    WebElement mockElement3 = mock(WebElement.class);

    when(mockElement1.getText()).thenReturn("One");
    when(mockElement2.getText()).thenReturn("Two");
    when(mockElement3.getText()).thenReturn("Three");
    assertThat(texts.test(asList(mockElement1, mockElement2, mockElement3)))
      .isEqualTo(shouldMatch);
  }

  @Test
  void shouldMatchWithDifferentOrder() {
    ExactTextsCaseSensitiveInAnyOrder texts = new ExactTextsCaseSensitiveInAnyOrder(asList("One", "Three", "Two"));
    testApplyMethod(true, texts);
  }

  @Test
  void shouldNotMatchWithSameOrderAndDifferentCase() {
    ExactTextsCaseSensitiveInAnyOrder texts = new ExactTextsCaseSensitiveInAnyOrder("one", "two", "three");
    testApplyMethod(false, texts);
  }

  @Test
  void shouldNotMatchWithDifferentOrderAndDifferentCase() {
    ExactTextsCaseSensitiveInAnyOrder texts = new ExactTextsCaseSensitiveInAnyOrder("one", "three", "two");
    testApplyMethod(false, texts);
  }

  @Test
  void shouldNotMatchWithSmallerListSize() {
    ExactTextsCaseSensitiveInAnyOrder texts = new ExactTextsCaseSensitiveInAnyOrder("One", "Two");
    testApplyMethod(false, texts);
  }

  @Test
  void shouldNotMatchWithBiggerListSize() {
    ExactTextsCaseSensitiveInAnyOrder texts = new ExactTextsCaseSensitiveInAnyOrder("One", "Two", "Three", "four");
    testApplyMethod(false, texts);
  }

  @Test
  void shouldHaveCorrectToString() {
    assertThat(new ExactTextsCaseSensitiveInAnyOrder("One", "Two"))
      .hasToString("Exact texts case sensitive in any order [One, Two]");
  }

}
