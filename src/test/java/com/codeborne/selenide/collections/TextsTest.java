package com.codeborne.selenide.collections;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class TextsTest implements WithAssertions {
  @Test
  void applyWithEmptyList() {
    assertThat(new Texts("One", "Two", "Three").test(emptyList()))
      .isFalse();
  }

  @Test
  void applyWithWrongSizeList() {
    Texts texts = new Texts(asList("One", "Two", "Three"));
    assertThat(texts.test(singletonList(element(""))))
      .isFalse();
  }

  @Test
  void applyWithMatchOnPartialText() {
    Texts texts = new Texts(asList("One", "Two"));
    assertThat(texts.test(asList(element("OneThing"), element("Two")))).isEqualTo(true);
  }

  @Test
  void applyWithNoMatchOnPartialText() {
    Texts texts = new Texts(asList("One", "Two"));
    assertThat(texts.test(asList(element("Three"), element("Selenide")))).isEqualTo(false);
  }

  @Test
  void testToString() {
    assertThat(new Texts(asList("One", "Two")))
      .hasToString("texts [One, Two]");
  }

  private WebElement element(String text) {
    WebElement webElement = mock(WebElement.class);
    when(webElement.getText()).thenReturn(text);
    return webElement;
  }
}
