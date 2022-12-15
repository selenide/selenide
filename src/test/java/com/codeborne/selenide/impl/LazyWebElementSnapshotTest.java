package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LazyWebElementSnapshotTest {

  private final WebElementSource source = mock();

  @Test
  void elementShouldBeCached() {
    // Given
    when(source.getWebElement()).thenAnswer(invocation -> mock(WebElement.class));
    LazyWebElementSnapshot cachedSource = new LazyWebElementSnapshot(source);

    // When
    WebElement webElement1 = cachedSource.getWebElement();
    WebElement webElement2 = cachedSource.getWebElement();

    // Then
    assertThat(webElement1).isNotNull().isSameAs(webElement2);
    verify(source, times(1)).getWebElement();
  }
}
