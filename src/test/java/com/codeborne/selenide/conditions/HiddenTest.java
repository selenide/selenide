package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class HiddenTest {
  private final Driver driver = mock();
  private final Hidden condition = new Hidden();
  private final WebElement element = mock();

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
    assertThat(condition.check(driver, element).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void not_satisfied_if_element_is_visible() {
    when(element.isDisplayed()).thenReturn(true);
    assertThat(condition.check(driver, element).verdict()).isEqualTo(REJECT);
  }
}
