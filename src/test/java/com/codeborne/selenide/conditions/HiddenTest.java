package com.codeborne.selenide.conditions;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class HiddenTest {
  private final Hidden condition = new Hidden();
  private final WebElement element = mock(WebElement.class);

  @Test
  void negate() {
    assertThat(condition.missingElementSatisfiesCondition()).isTrue();
    assertThat(condition.negate().missingElementSatisfiesCondition()).isFalse();
  }

  @Test
  void name() {
    assertThat(condition.getName()).isEqualTo("hidden");
    assertThat(condition.negate().getName()).isEqualTo("not hidden");
  }

  @Test
  void satisfied_if_element_is_not_visible() {
    when(element.isDisplayed()).thenReturn(false);
    assertThat(condition.apply(null, element)).isTrue();
  }

  @Test
  void not_satisfied_if_element_is_visible() {
    when(element.isDisplayed()).thenReturn(true);
    assertThat(condition.apply(null, element)).isFalse();
  }
}
