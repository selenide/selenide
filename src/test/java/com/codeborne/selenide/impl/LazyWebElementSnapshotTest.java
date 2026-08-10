package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LazyWebElementSnapshotTest {

  private final WebElement element = mock();
  private final WebElementSource source = mock();

  @Test
  void elementShouldBeCached() {
    // Given
    when(source.getWebElement()).thenReturn(element);
    LazyWebElementSnapshot cachedSource = new LazyWebElementSnapshot(source);

    // When
    WebElement result1 = cachedSource.getWebElement();
    WebElement result2 = cachedSource.getWebElement();

    // Then
    assertThat(result1).isSameAs(result2);
    assertThat(result1).isSameAs(element);
    verify(source, times(1)).getWebElement();
  }
}
